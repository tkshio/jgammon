package com.github.tkshio.jgammon.gammon.move;

import com.github.tkshio.jgammon.common.move.MovesStack;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ゾロ目でない場合に可能なプレイを列挙するクラス
 */
class RollApplierForNonDoublet {

    /**
     * MovesStackが保持する盤面について、指定のロールで可能なプレイを列挙する
     *
     * @param movesStack 任意の盤面を保持するMovesStackオブジェクト
     * @param roll       使用するロール
     * @return 生成されたプレイのストリーム
     */
    static Stream<CheckerPlay> applyRoll(MovesStack<Move, Integer> movesStack, Roll roll) {
        int higher = roll.getHigherNumber();
        int lower = roll.getLowerNumber();

        // まずロールを片方だけ使った場合のプレイを列挙する
        Collection<Move> movesHigherOnly = movesStack.listupMoves(0, higher);
        Collection<Move> movesLowerOnly = movesStack.listupMoves(0, lower);

        // 入れ替え可能なムーブを除去するフィルターを準備
        DupMarker filter = new DupMarker();


        // まず大きいほうを先に使うプレイ（上記のプレイの続きとなる）を列挙し、フィルターに登録する
        // ベアリングオフの関係で、フィルター登録はこちらで行う必要がある -> DupMarker参照
        Collection<CheckerPlay> movesHigherFirst = buildMoveResults(
                movesStack, movesHigherOnly, lower, roll, filter::mark);

        // 小さいほうを先に使うプレイを列挙し、フィルターにより重複分を除去する
        Collection<CheckerPlay> movesLowerFirst = buildMoveResults(
                movesStack, movesLowerOnly, higher, roll, filter::shouldKeep);

        // 各リストを取捨選択して、結果を得る
        return combineMoves(roll, movesHigherOnly, movesLowerOnly,
                movesHigherFirst, movesLowerFirst);
    }


    // バックギャモンのルールに則り、候補手リストの取捨選択を行う

    // 1. 両方のロールが使える場合は、両方を使わなくてはいけない
    // 2. 片方しか使えない場合は、大きい目のロールを優先して使う
    // 3. どのロールも使えない場合は、動かせない
    private static Stream<CheckerPlay> combineMoves(
            Roll roll,
            Collection<Move> movesHigherOnly,
            Collection<Move> movesLowerOnly,
            Collection<CheckerPlay> movesHigherFirst,
            Collection<CheckerPlay> movesLowerFirst) {

        Stream<CheckerPlay> ret;
        if (!movesHigherFirst.isEmpty() || !movesLowerFirst.isEmpty()) {
            // どちらかを先に使えば両方使える
            ret = Stream.concat(
                    movesHigherFirst.stream(),
                    movesLowerFirst.stream()
            );
        } else {
            if (!movesHigherOnly.isEmpty()) {
                // 大きいほうだけが使える
                ret = singleMove(roll, movesHigherOnly);
            } else if (!movesLowerOnly.isEmpty()) {
                // 小さいほうだけが使える
                ret = singleMove(roll, movesLowerOnly);
            } else {
                // どれも使えないなら空ムーブしかない
                ret = Stream.of(CheckerPlay.of(roll));
            }
        }

        return ret;
    }

    // movesStackで与えられた局面について、まずmovesの各ムーブを適用し、
    // その続きとして rolltoFollowの目で可能な手を列挙、
    // CheckerPlayのストリームとして返す

    private static Collection<CheckerPlay> buildMoveResults(
            MovesStack<Move, Integer> movesStack,
            Collection<Move> moves,
            int rollToFollow,
            Roll roll,
            Predicate<CheckerPlay> filter) {

        return moves.stream()
                .flatMap(move -> movesStack
                        // ムーブを適用する
                        .stack(move)

                        // 次の手を列挙
                        .listupMoves(move.getFrom(), rollToFollow)

                        // CheckerPlayに読み替える
                        .stream()
                        .map(nextMove -> CheckerPlay.of(roll, move, nextMove))
                )

                // 得られた手の登録、または重複分の除去
                .filter(filter)
                .collect(Collectors.toList());
    }

    // ムーブのコレクションを、ムーブ1手だけのCheckerPlayに読み替える
    private static Stream<CheckerPlay> singleMove(
            Roll roll, Collection<Move> moves) {

        return moves.stream()
                .map(appliedMove -> CheckerPlay.of(roll, appliedMove));

    }

    // ロールを大→小の順、小→大の順で使うとき、それぞれの候補手で重複するムーブの登録・排除
    // 具体的には、以下の３つ
    // 1) 同じ場所から２つの駒をムーブ
    //    1/2, 1/3  <- 1/3, 1/2と重複する
    // 2) 同じ駒を2回ムーブで、途中のヒットがない
    //    1/2 2/4 (= 1/2/4)   <- 1/3/4と重複する
    // 3) どちらもベアリングオフ
    //    23/OFF, 24/OFF <- 24/OFF, 23/OFFと重複する
    private static class DupMarker {
        private final Set<Integer> f1;
        private final Set<Integer> f2;


        DupMarker() {
            this.f1 = new HashSet<>();
            this.f2 = new HashSet<>();
        }

        void verify(CheckerPlay checkerPlay) {
            if (checkerPlay.getRoll().isDoublet() || checkerPlay.getMoves().length != 2) {
                throw new IllegalArgumentException();
            }
        }

        // 条件に該当するとき、そのムーブの開始位置（上記の例でいえば1）にマークする
        // マークは条件別に分けて管理する
        boolean mark(CheckerPlay checkerPlay) {
            verify(checkerPlay);
            var moves = checkerPlay.getMoves();

            // 同じ場所から２つムーブ
            if (moves[0].getFrom() == moves[1].getFrom()) {
                f1.add(moves[0].getFrom());
            }

            // 同じ駒を２回ムーブ、かつ途中でヒットしない
            if (moves[0].getTo() == moves[1].getFrom() &&
                    !moves[0].isHit()) {
                f2.add(moves[0].getFrom());
            }

            // こちらは登録するメソッドなので、フィルターとしては常にtrue
            return true;
        }

        boolean shouldKeep(CheckerPlay checkerPlay) {
            verify(checkerPlay);
            var moves = checkerPlay.getMoves();
            var pos = moves[0].getFrom();

            // 条件1
            // どちらも条件１でマーク済からのムーブ
            if (f1.contains(pos) && moves[1].getFrom() == pos) {
                return false;
            }


            // 条件２
            // 条件２でマーク済の位置から、同じ駒を連続して動かしていて、かつヒットがない
            if (f2.contains(pos)
                    && moves[0].getTo() == moves[1].getFrom()
                    && !moves[0].isHit()) {
                return false;
            }

            // 条件３
            // 小さい目を先に使って両方上がるなら、
            // 大きい目を先に使ったときにも列挙されているはずなので除去してよい
            return !(moves[0].isBearOff() && moves[1].isBearOff());

            // ※逆は成立しないので、大きい目先行の処理に対して登録、
            // 小さい目先行の処理に対して除去処理、の順でなくてはいけない
        }
    }
}
