package com.github.tkshio.jgammon.tdlearn.td;

import lombok.Data;

/**
 * {@link TDLearner}の学習状態を表す
 *
 * <p> 学習状態は、ある時点<sub>t</sub>における、以下の各項目からなる。
 * <ul>
 *     <li>現時点およびそれまでの各時点1,...tでの勾配の累積w<sub>t</sub></li>
 *     <li>出力値</li>
 * </ul>
 * <p>
 * Temporal-Differnceでは、次の時点<sub>t+1</sub>での出力値の差分を取り、
 * 勾配の累積w<sub>t</sub>を掛けることで時点tでの評価に対する更新量が算出される。
 *
 * @param <GRADIENT> 勾配（およびその累積）を表すクラス
 */
@Data
public class TDLearningState<GRADIENT extends TDGradient<?, GRADIENT>> {
    private final GRADIENT eligibilityTrace;
    private final double[] output;

    /**
     * 学習の初期状態を表すオブジェクトを生成する
     *
     * @param eval {@link TDLearner#eval(double[])}による評価値
     * @param <G>  勾配（およびその累積）を表すクラス
     * @return 学習の初期状態
     */
    static <G extends TDGradient<?, G>>
    TDLearningState<G> initialState(TDEval<G> eval) {
        return new TDLearningState<>(eval.getGradient(), eval.getOutput());
    }

}
