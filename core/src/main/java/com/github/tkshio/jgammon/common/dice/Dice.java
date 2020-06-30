package com.github.tkshio.jgammon.common.dice;

/**
 * サイコロの機能を提供する
 */
public interface Dice {
    /**
     * 1-sizeまでの範囲の値をロールする
     *
     * @return ロールされた目
     */
    int roll();

    /**
     * 出目の最大値を返す
     *
     * @return 出目の最大値
     */
    int size();

    /**
     * 乱数発生器などの内部状態を初期化する
     */
    default void reset() {
    }

    /**
     * reset()が呼ばれた回数を返す
     *
     * @return リセット回数
     */
    default int resetCount() {
        return 0;
    }
}
