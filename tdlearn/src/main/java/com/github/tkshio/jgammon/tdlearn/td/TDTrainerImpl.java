package com.github.tkshio.jgammon.tdlearn.td;

import java.util.Arrays;

/**
 * {@link TDTrainer}の実装クラス
 *
 * <p> {@code calcDelta} と {@code calcNextState}により、Temporal-Differenceではない学習を行うことができる
 */
class TDTrainerImpl implements TDTrainer {

    private final double learning_rate;
    private final double lambda;

    TDTrainerImpl(double learning_rate, double lambda) {
        this.learning_rate = learning_rate;
        this.lambda = lambda;
    }

    @Override
    public <DELTA, GRADIENT extends TDGradient<DELTA, GRADIENT>>
    TDLearningState<GRADIENT> initialState(TDLearner<DELTA, GRADIENT> tdLearner,
                                           double[] input) {
        var initialGradient = tdLearner.eval(input);
        return TDLearningState.initialState(initialGradient);
    }

    @Override
    public <DELTA, GRADIENT extends TDGradient<DELTA, GRADIENT>>
    void commitWithReward(TDLearner<DELTA, GRADIENT> tdLearner,
                          TDLearningState<GRADIENT> state,
                          double[] rewards) {

        updateNN(tdLearner, state, rewards);
    }

    @Override
    public <DELTA, GRADIENT extends TDGradient<DELTA, GRADIENT>>
    TDLearningState<GRADIENT> learn(
            TDLearner<DELTA, GRADIENT> tdLearner,
            TDLearningState<GRADIENT> prevState,
            TDEval<GRADIENT> tdEval) {

        updateNN(tdLearner, prevState, tdEval.getOutput());

        return calcNextState(prevState, tdEval);
    }


    private <DELTA, GRADIENT extends TDGradient<DELTA, GRADIENT>>
    void updateNN(TDLearner<DELTA, GRADIENT> tdLearner,
                  TDLearningState<GRADIENT> prevState,
                  double[] output) {

        // この時点の評価E_tとの差分により、直前の時点t_1に対する更新量が求まる
        // delta <- ( E_t+1 - E_t ) * gradient
        DELTA del = calcDelta(prevState, output);
        tdLearner.update(del);
    }

    /**
     * 直前の時点t-1における学習状態S<sub>t-1</sub>に、時点tでの評価結果を反映させ、新しい学習状態S<sub>t</sub>を得る
     *
     * @param prevState  直前の学習状態
     * @param tdEval     現在の評価結果
     * @param <DELTA>    更新量を表すクラス
     * @param <GRADIENT> 勾配(およびその累積)を表すクラス
     * @return 次の学習状態
     */
    <DELTA, GRADIENT extends TDGradient<DELTA, GRADIENT>>
    TDLearningState<GRADIENT> calcNextState(TDLearningState<GRADIENT> prevState,
                                            TDEval<GRADIENT> tdEval) {

        // 次のlearn()に引き継ぐ、勾配の累積値を求める
        // gradient_sum <- gradient + λ * gradient_sum
        var prevEligibilityTrace = prevState.getEligibilityTrace();
        var eligibilityTrace = prevEligibilityTrace.accum(tdEval.getGradient(), lambda);

        return new TDLearningState<>(eligibilityTrace, tdEval.getOutput());
    }

    /**
     * 直前の時点t-1における学習状態S<sub>t-1</sub>と時点tでの評価値から時点<sub>t-1</sub>での評価に対する、更新量を計算する
     *
     * @param prevState  直前の学習状態
     * @param eval       現在の評価値
     * @param <DELTA>    更新量を表すクラス
     * @param <GRADIENT> 勾配(およびその累積)を表すクラス
     * @return 更新量
     */
    <DELTA, GRADIENT extends TDGradient<DELTA, GRADIENT>>
    DELTA calcDelta(TDLearningState<GRADIENT> prevState,
                    double[] eval) {

        var eligibilityTrace = prevState.getEligibilityTrace();
        double[] lastEval = prevState.getOutput();

        return _calcDelta(eval, lastEval, eligibilityTrace);
    }

    /**
     * Temporal-Difference法ではなく、現時点の状態と最終結果から直接更新量を求める
     *
     * <p>数式上は、各時点についてこのメソッドで得られる更新量の総和は、
     * {@code calcDelta}と{@code calcNextState}で得られる各時点での更新量の総和に等しい
     * （実際は数値計算上の誤差がある。また、{@code learn}では随時ネットワークの重みを
     * 更新しているので、上記の通りにはならない）。
     *
     * @param tdLearner  学習を行うオブジェクト
     * @param input      入力値
     * @param reward     最終結果
     * @param <DELTA>    更新量を表すクラス
     * @param <GRADIENT> 勾配(およびその累積)を表すクラス
     * @return 更新量
     */
    <DELTA, GRADIENT extends TDGradient<DELTA, GRADIENT>>
    DELTA calcDelta(TDLearner<DELTA, GRADIENT> tdLearner,
                    double[] input,
                    double[] reward) {

        var eval = tdLearner.eval(input);
        double[] output = eval.getOutput();
        return _calcDelta(reward, output, eval.getGradient());
    }

    // delta_W = learning_rate x ( E_cur  - E_last ) x (gradient_sum)

    /**
     * 出力と目標値との差分と勾配との積から更新量を求める
     *
     * @param curEval    出力値
     * @param lastEval   目標値
     * @param gradient   勾配の累積
     * @param <DELTA>    更新量を表すクラス
     * @param <GRADIENT> 勾配(およびその累積)を表すクラス
     * @return 更新量
     */
    private <DELTA, GRADIENT extends TDGradient<DELTA, GRADIENT>>
    DELTA _calcDelta(double[] curEval,
                     double[] lastEval,
                     GRADIENT gradient) {

        double[] amount = new double[curEval.length];
        Arrays.setAll(amount, i -> (curEval[i] - lastEval[i]) * learning_rate);

        return gradient.delta(amount);
    }

}
