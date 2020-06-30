package com.github.tkshio.jgammon.tdlearn.td;

/**
 * Temporal-Difference法の本質的な機能の定義
 *
 * <p> このインターフェースでは、直前の状態との差分学習のみが提供されている。
 */
public interface TDTrainer {

    /**
     * 初期の学習状態を準備する
     *
     * @param tdLearner  学習を行うオブジェクト
     * @param input      入力ノードに与える入力値
     * @param <DELTA>    更新量を表すクラス
     * @param <GRADIENT> 勾配(およびその累積)を表すクラス
     * @return 次の呼び出し時に使用する学習状態
     */
    <DELTA, GRADIENT extends TDGradient<DELTA, GRADIENT>> TDLearningState<GRADIENT> initialState(
            TDLearner<DELTA, GRADIENT> tdLearner,
            double[] input);


    /**
     * 与えられた評価結果と直前までの学習状態から学習を行い、次に使用するための学習状態を返す
     *
     * @param tdLearner  学習を行うオブジェクト
     * @param prevState  直前の学習状態、本メソッドまたは{@link #initialState(TDLearner, double[])}の返値
     * @param tdEval     評価結果、{@link TDLearner#eval(double[])}の返値
     * @param <DELTA>    更新量を表すクラス
     * @param <GRADIENT> 勾配(およびその累積)を表すクラス
     * @return 次の呼び出し時に使用する学習状態
     */
    <DELTA, GRADIENT extends TDGradient<DELTA, GRADIENT>> TDLearningState<GRADIENT> learn(
            TDLearner<DELTA, GRADIENT> tdLearner,
            TDLearningState<GRADIENT> prevState,
            TDEval<GRADIENT> tdEval);


    /**
     * 最終結果を渡し、学習内容を確定させる
     *
     * @param tdLearner  学習を行うオブジェクト
     * @param prevState  直前の学習状態、{@link #learn(TDLearner, TDLearningState, TDEval)} または{@link #initialState(TDLearner, double[])}の返値
     * @param answer     最終結果としてフィードバックさせる値、出力値と同じ形式で与える
     * @param <DELTA>    更新量を表すクラス
     * @param <GRADIENT> 勾配(およびその累積)を表すクラス
     */
    <DELTA, GRADIENT extends TDGradient<DELTA, GRADIENT>>
    void commitWithReward(
            TDLearner<DELTA, GRADIENT> tdLearner,
            TDLearningState<GRADIENT> prevState,
            double[] answer);

}
