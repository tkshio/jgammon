package com.github.tkshio.jgammon.common.move;

import java.util.Collection;

/**
 * ある盤面に対して、一手ずつ進めつつ可能な手を列挙する機能を提供するインターフェース
 *
 * @param <MOVE> ムーブを表すクラス
 * @param <KEY>  列挙する際の引数（バックギャモンであればロールの目）を表すクラス
 */
public interface MovesStack<MOVE, KEY> {

    /**
     * ファクトリーメソッド
     *
     * @param <BOARD>       盤面を表すクラス
     * @param <MOVE>        ムーブを表すクラス
     * @param <KEY>         ムーブの列挙にあたって、引数となるクラス
     * @param moveOperator  ムーブ適用機能を担うオブジェクト
     * @param movesSupplier 可能なムーブを列挙する機能を担うオブジェクト
     * @param board         開始盤面
     * @return 生成されたMovesStackオブジェクト
     */
    static <BOARD, MOVE, KEY>
    MovesStack<MOVE, KEY> create(MoveOperator<BOARD, MOVE> moveOperator,
                                 MovesSupplier<BOARD, MOVE, KEY> movesSupplier,
                                 BOARD board) {
        return new MovesStack<>() {
            @Override
            public Collection<MOVE> listupMoves(int from, KEY key) {
                return movesSupplier.listupPossibleMoves(board, from, key);
            }

            @Override
            public MovesStack<MOVE, KEY> stack(MOVE move) {
                return create(moveOperator, movesSupplier, moveOperator.applyMove(board, move));
            }
        };
    }

    /**
     * @param from 対象となる位置（この位置の駒も列挙対象に含む）
     * @param key  列挙にあたって、引数となるオブジェクト（バックギャモンであればロールの目）
     * @return 可能なムーブのリスト、ない場合は空リストが返る
     * @see MovesSupplier#listupPossibleMoves
     */
    Collection<MOVE> listupMoves(int from, KEY key);

    /**
     * 一手進める：すなわち、このMovesStackが保持する局面に与えられたムーブを適用し、
     * 適用後の盤面についてMovesStackを生成する
     *
     * <p>{@link #create} で指定したMoveOperator,MovesSupplierはそのまま引き継がれる
     *
     * @param move 適用するムーブ
     * @return 生成されたMovesStackオブジェクト
     */
    MovesStack<MOVE, KEY> stack(MOVE move);

}
