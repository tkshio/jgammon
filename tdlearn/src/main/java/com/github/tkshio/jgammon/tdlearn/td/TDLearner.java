package com.github.tkshio.jgammon.tdlearn.td;


/**
 * Temporal Learingにおいて、学習側が{@link TDTrainer}に対して提供するべき機能
 *
 * @param <DELTA>    更新量を表すクラス
 * @param <GRADIENT> 勾配（およびその累積）を表すクラス
 */
public interface TDLearner<DELTA, GRADIENT> {

    /**
     * 入力値を評価し、同時にその出力値に対する勾配を算出する
     *
     * @param input 入力値
     * @return 評価結果
     */
    TDEval<GRADIENT> eval(double[] input);

    /**
     * {@link TDTrainer}が算出した更新量を受け取り、自身を更新する
     *
     * <p>{@link TDTrainer}は、{@link #eval(double[])}の結果から更新量を算出する。
     *
     * <p> 学習を行わない場合は単に更新量を無視すればよく、デフォルトの実装はそうなっている。
     *
     * @param sum 更新量
     */
    default void update(DELTA sum) {
    }
}
