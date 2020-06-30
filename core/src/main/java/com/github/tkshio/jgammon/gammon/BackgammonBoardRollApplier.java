package com.github.tkshio.jgammon.gammon;

import com.github.tkshio.jgammon.gammon.move.RollApplier;


/**
 * BackgammonBoardに対して、RollApplierの機能を接合するメソッドを提供する
 *
 * <p>BackgammonBoardはこのメソッドと、{@link com.github.tkshio.jgammon.gammon.move.BGBoard BGBoard}インターフェースのみを経由してRollApplier（moveパッケージ）に依存している。
 */
class BackgammonBoardRollApplier {
    /**
     * ファクトリーメソッド
     *
     * @return 生成されたRollApplierオブジェクト
     */
    static RollApplier<BackgammonBoard> create() {
        return RollApplier.create(BackgammonBoard::withMove);
    }
}
