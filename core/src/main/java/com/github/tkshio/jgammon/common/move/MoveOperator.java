package com.github.tkshio.jgammon.common.move;

import com.github.tkshio.jgammon.gammon.move.RollApplier;

/**
 * {@link RollApplier}が使用する、ムーブを適用するメソッドのインターフェース
 *
 * @param <BOARD> ムーブの適用対象となるクラス
 * @param <MOVE>  ムーブを表現するクラス
 */
public interface MoveOperator<BOARD, MOVE> {

    /**
     * 与えられた盤面にムーブを適用した結果の盤面を、新しいオブジェクトとして返す
     *
     * @param board 適用対象の盤面
     * @param move  適用対象のムーブ
     * @return 適用後の盤面
     */
    BOARD applyMove(BOARD board, MOVE move);

}
