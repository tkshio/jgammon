package com.github.tkshio.jgammon.common.evaluator;

/**
 * 特定の局面を評価する機能のインターフェース
 *
 * @param <STATE> 評価対象となる局面のクラス
 */
public interface StateEvaluator<STATE> {

    /**
     * 与えられた局面の評価値を返す
     *
     * @param state 評価対象の局面
     * @return 評価値
     */
    Eval eval(STATE state);


    /**
     * 開始局面
     *
     * @param state 開始局面（先手観点）
     */
    default void initialState(STATE state) {
    }

    /**
     * 対局に勝利した
     *
     * @param state 勝利局面（勝者観点）
     */
    default void won(STATE state) {
    }


    /**
     * 手番が多くなりすぎた場合など、対局が中断した
     */
    default void abort() {

    }

    /**
     * 対局に敗北した
     *
     * @param state 敗北局面（勝者観点）
     */
    default void lost(STATE state) {
    }

    /**
     * 引き分けとなった
     *
     * @param state 引き分けの局面（引き分け決定時のプレイヤーの観点）
     */
    default void draw(STATE state) {
    }

    ;
}
