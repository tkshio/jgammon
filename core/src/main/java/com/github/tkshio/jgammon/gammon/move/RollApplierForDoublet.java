package com.github.tkshio.jgammon.gammon.move;

import com.github.tkshio.jgammon.common.move.MovesStack;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ゾロ目に対して、可能なプレイを列挙するクラス
 */
interface RollApplierForDoublet {
    /**
     * movesStackが保持する盤面について、roll目をdepth回使える場合のプレイを列挙する
     *
     * @param movesStack 任意の盤面を保持するMovesStackオブジェクト
     * @param roll       ロール目
     * @param depth      ロール目の使用可能回数
     * @return 生成されたプレイのストリーム
     */
    static Stream<CheckerPlay> applyRoll(
            MovesStack<Move, Integer> movesStack,
            Roll roll,
            int depth) {

        // movesStackを使って、ムーブを適用した結果をツリー構造で生成する
        Collection<MoveTree> moves = recursiveListupMoves(
                movesStack, 0, roll.getHigherNumber(), depth);

        if (moves.isEmpty()) {
            // 可能なムーブがなければ、空のプレイで返す
            return Stream.of(CheckerPlay.of(roll));
        } else {
            // 返ってきたツリー構造を、CheckerPlayに読み替える
            Function<Move[], CheckerPlay> builder =
                    m -> CheckerPlay.of(roll, m);

            return moves.stream()
                    .flatMap(moveTree -> moveTree.traverseWith(builder))
                    ;
        }
    }

    /**
     * 与えられた盤面について、可能な手のリストを返す
     *
     * <p>リスト中の各要素は、その手を適用した後の盤面について、
     * それ以降に続けて適用可能な手を格納したツリー構造となっている
     *
     * @param movesStack 任意の盤面を保持するMovesStackオブジェクト
     * @param from       盤面の指定された位置を含むそれ以降を列挙対象とする
     * @param roll       ロール目
     * @param depth      ロール目の使用可能回数
     * @return movesStackの盤面から可能な手を格納したMoveTreeオブジェクトのリスト
     */
    static Collection<MoveTree> recursiveListupMoves(
            MovesStack<Move, Integer> movesStack,
            int from,
            int roll,
            int depth) {

        // 与えられた局面について、可能な手を列挙
        Collection<Move> nextMoves = movesStack.listupMoves(from, roll);

        // 可能な手がなければ、空ツリーを返す
        if (nextMoves.isEmpty()) {
            return Collections.emptyList();
        }

        // 再帰の底＝これ以上使ってよいロール目がないなら、
        // 子局面が空のMoveTreeオブジェクトのリストとして返す
        if (depth == 1) {
            return nextMoves.stream()
                    .map(MoveTree::new)
                    .collect(Collectors.toList());

        }

        // それぞれの手と、そこからの子局面をペアにしてツリーを生成、
        // Collectionにして返す
        var ret = nextMoves.stream()
                .map(nextMove -> {
                            var children = recursiveListupMoves(
                                    movesStack.stack(nextMove),
                                    nextMove.getFrom(),
                                    roll,
                                    depth - 1)
                                    .toArray(new MoveTree[]{});

                            return new MoveTree(nextMove, children);
                        }
                )
                .collect(Collectors.toList());

        // できるだけ多くロールを消費する手だけを残す
        Collection<MoveTree> deepests = new ArrayList<>(ret.size());
        int max = -1;
        for (MoveTree moveTree : ret) {
            int treeDepth = moveTree.getDepth();
            if (treeDepth > max) {
                max = treeDepth;
                deepests.clear();
                deepests.add(moveTree);
            } else if (treeDepth == max) {
                deepests.add(moveTree);
            }
        }
        return deepests;
    }


    /**
     * ムーブオブジェクトを格納するツリー
     */
    @Data
    class MoveTree {
        private final static MoveTree[] EMPTY = new MoveTree[]{};
        private final Move current;
        private final MoveTree[] children;
        // ツリーが自分を含め何段の子要素を従えているか（最下段＝１）
        private final int depth;

        /**
         * ツリーの末端を生成するコンストラクター
         *
         * @param current 格納するムーブオブジェクト
         */
        MoveTree(Move current) {
            this(current, EMPTY, 1);
        }

        /**
         * 子要素のあるツリーを生成するコンストラクター
         *
         * @param current  格納するムーブオブジェクト
         * @param children 格納する子ツリー
         */

        MoveTree(Move current, MoveTree... children) {
            this(current, children, children.length != 0 ? children[0].getDepth() + 1 : 1);
        }

        private MoveTree(Move current, MoveTree[] children, int depth) {
            this.current = current;
            this.children = children;
            this.depth = depth;
        }

        /**
         * ツリーの要素を任意の型に変換して、Streamで返す
         *
         * <p>ツリーの深さをNとしたとき、ツリーのすべての末端要素について、
         * 根本から末端までの各要素が保持するMoveオブジェクトを、
         * 配列長Nの配列に詰めなおした上で任意のオブジェクトに変換し、
         * Streamとして返す。
         * <p>
         * 一連のMoveをまとめて１つのCheckerPlayに変換している。
         *
         * @param builder Moveの配列からEを生成するビルダー
         * @param <E>     結果として必要なオブジェクトのクラス
         * @return ツリーの全要素を提供する、Eのストリーム
         */
        private <E> Stream<E> traverseWith(Function<Move[], E> builder) {

            Stream<E> ret;

            if (children.length == 0) {
                ret = Stream.of(builder.apply(new Move[]{current}));
            } else {
                // 最終的にbuilderに渡す配列
                // この配列は、再利用されており、streamの副作用として更新される
                Move[] movesForArgs = new Move[depth];
                movesForArgs[0] = current;

                // childrenを再帰的にたどりながらmovesに格納し、
                // 最後にmovesをbuilderに渡す
                ret = traverseWith(children, movesForArgs, 1, builder);
            }

            return ret;
        }

        // 上記の下請け関数、与えられたmovesForArgsの指定位置に結果を詰めていく
        // まずidxの位置に詰め、idx+1を引数として再帰する
        private <E> Stream<E> traverseWith(MoveTree[] children,
                                           Move[] movesForArgs,
                                           int idx,
                                           Function<Move[], E> builder) {

            // Arrays.streamはsequentialなので、この実装が成立する
            return Arrays.stream(children)
                    .flatMap(moveTree -> {
                        movesForArgs[idx] = moveTree.getCurrent();

                        Stream<E> ret;

                        MoveTree[] _children = moveTree.getChildren();
                        if (moveTree.depth == 2) {
                            // 最下段の直上なら、直接Streamを生成してしまう
                            ret = Arrays.stream(_children)
                                    .map(child -> {
                                        movesForArgs[idx + 1] = child.getCurrent();
                                        return builder.apply(movesForArgs);
                                    });
                        } else if (moveTree.depth == 1) {
                            // すでに最下段であった場合（もとの深さが２段の場合
                            return Stream.of(builder.apply(movesForArgs));
                        } else {
                            // それ以外は再帰
                            ret = traverseWith(_children, movesForArgs, idx + 1, builder);
                        }

                        return ret;
                    });
        }
    }
}
