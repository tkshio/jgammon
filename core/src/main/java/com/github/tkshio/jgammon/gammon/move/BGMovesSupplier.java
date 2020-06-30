package com.github.tkshio.jgammon.gammon.move;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * バックギャモンのルールに則った、{@link com.github.tkshio.jgammon.common.move.MovesSupplier MovesSupplier}の実装
 *
 * <p>同インターフェースは単一メソッドなので、継承ではなくstaticメソッドを提供する形になっている
 */
public class BGMovesSupplier {

    /**
     * 与えられた盤面・ロール目での、指定位置以降（含む）の駒について、
     * 可能なムーブを列挙する
     *
     * @param board 対象となる盤面
     * @param from  対象となる位置（この位置の駒も列挙対象に含む）
     * @param roll  ロール目
     * @return 可能なムーブのリスト、ない場合は空リストが返る
     */
    public static Collection<Move> listupWithDefaultRule(BGBoard board, int from, int roll) {
        Stream<Move> ret;

        // ベアオフかどうかで分岐
        if (board.isInBearingOff()) {
            ret = listupForBearingOff(board, from, roll);
        } else {
            ret = listupMoves(board, from, roll);
        }
        return ret.collect(Collectors.toList());
    }

    /**
     * あがり局面から、指定のロール目で可能な通常の（ベアオフでない）ムーブを生成する
     *
     * @param board 対象盤面
     * @param from  この位置およびそれ以降のみをムーブの開始位置とする
     * @param roll  ロール目
     * @return ムーブ結果のStream
     * <p>
     * fromは冗長なムーブ生成を回避するために使用されている。
     */
    public static Stream<Move> listupForBearingOff(BGBoard board, int from, int roll) {
        final Stream<Move> ret;

        var highestPiecePosOpt = listupMovablePoints(board, 0).findFirst();

        // 動かせる駒がない
        if (highestPiecePosOpt.isEmpty()) {
            return Stream.empty();
        }

        // 最後尾の駒の位置
        int highestPiecePos = highestPiecePosOpt.getAsInt();

        if (highestPiecePos + roll > board.getBearOffPos()) {
            if (highestPiecePos >= from) {
                // 最後尾の駒でもオーバーランするなら、それを上げる手しかない
                var move = Move.bearOff(highestPiecePos);
                ret = Stream.of(move);
            } else {
                ret = Stream.empty();
            }
        } else {

            // ロール目より手前は普通のムーブ
            Stream<Move> moves = listupMoves(board, from, roll);

            // ベアオフムーブ（ロール目により一意に定まる）できるコマがあれば、それも追加
            int pos = board.getBearOffPos() - roll;
            if (board.getPointAt(pos) > 0) {
                var bearOffMove = Move.bearOff(pos);
                ret = Stream.concat(moves, Stream.of(bearOffMove));
            } else {
                ret = moves;
            }
        }
        return ret;
    }

    /**
     * 移動可能な駒があるポイントを返す
     *
     * @param board 対象局面
     * @param from  オンザバーでなければ、このポイント（含む）以降のみを対象とする
     * @return 移動可能な駒があるポイントの番号を生成するIntStream
     */
    public static IntStream listupMovablePoints(BGBoard board, int from) {
        // オンザバーならバーから、
        // そうでなければ駒のあるポイントから
        return (board.getPointAt(0) >= 1) ?
                (IntStream.of(0)) :
                (IntStream.range(from, board.getBearOffPos()).filter(p -> board.getPointAt(p) > 0));
    }

    /**
     * あがり局面でない局面から、指定のロール目で可能な通常の（ベアオフでない）ムーブを生成する
     *
     * @param board 対象局面
     * @param from  この位置およびそれ以降のみをムーブの開始位置とする
     * @param roll  ロール目
     * @return ムーブ結果のStream
     * <p>
     * fromは冗長なムーブ生成を回避するために使用されている。
     */
    public static Stream<Move> listupMoves(BGBoard board, int from, int roll) {
        // 移動可能な駒それぞれについて
        return listupMovablePoints(board, from)

                // 行先が盤面内であるものについて
                .takeWhile(pos -> pos + roll < board.getBearOffPos())

                // ロール目だけ進める手を作成し
                .mapToObj(pos -> Move.of(pos, pos + roll,
                        board.getOpponentPointAt(pos + roll) == 1))

                // 行先がブロックされているものは除外する
                .filter(move -> (board.getOpponentPointAt(move.getTo()) < 2));

    }
}
