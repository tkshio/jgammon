package com.github.tkshio.jgammon.gammon.move;

import com.github.tkshio.jgammon.common.move.MoveOperator;
import com.github.tkshio.jgammon.common.move.MovesStack;

import java.util.stream.Stream;

/**
 * 盤面とロールとを受け取り、可能なプレイを列挙する
 *
 * @param <BOARD> バックギャモンの盤面を表すクラス
 */
public interface RollApplier<BOARD> {

    /**
     * 標準のバックギャモンルールに基づくRollApplierを生成する
     *
     * @param mop     ムーブを担当するオブジェクト
     * @param <BOARD> 盤面の型
     * @return 生成されたRollApplierオブジェクト
     */
    static <BOARD extends BGBoard>
    RollApplier<BOARD> create(MoveOperator<BOARD, Move> mop) {
        return create(mop, 4);
    }

    /**
     * ゾロ目ボーナスのないルールでのRollApplierを返す
     *
     * @param mop     ムーブを担当するオブジェクト
     * @param <BOARD> 盤面の型
     * @return 生成されたRollApplierオブジェクト
     */
    static <BOARD extends BGBoard>
    RollApplier<BOARD> createDoubletDisabled(MoveOperator<BOARD, Move> mop) {
        return create(mop, 2);
    }

    private static <BOARD extends BGBoard>
    RollApplier<BOARD> create(MoveOperator<BOARD, Move> mop, int depth) {

        return (board, roll) -> {
            MovesStack<Move, Integer> movesStack = MovesStack
                    .create(mop,
                            BGMovesSupplier::listupWithDefaultRule,
                            board);

            Stream<CheckerPlay> checkerPlays;
            if (roll.isDoublet()) {
                checkerPlays = RollApplierForDoublet
                        .applyRoll(movesStack, roll, depth);
            } else {
                checkerPlays = RollApplierForNonDoublet
                        .applyRoll(movesStack, roll);
            }
            return checkerPlays;
        };
    }

    /**
     * 指定された盤面・ロールについて可能なプレイを列挙し、ストリームとして返す
     *
     * @param board 対象盤面
     * @param roll  ロール
     * @return 可能なプレイのストリーム
     */
    Stream<CheckerPlay> listupMoves(BOARD board, Roll roll);

}
