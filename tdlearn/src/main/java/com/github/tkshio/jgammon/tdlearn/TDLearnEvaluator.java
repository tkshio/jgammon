package com.github.tkshio.jgammon.tdlearn;

import com.github.tkshio.jgammon.common.evaluator.Eval;
import com.github.tkshio.jgammon.common.evaluator.StateEvaluator;
import com.github.tkshio.jgammon.common.utils.SGTuple;
import com.github.tkshio.jgammon.tdlearn.codecs.DecodedEval;
import com.github.tkshio.jgammon.tdlearn.codecs.InputCodec;
import com.github.tkshio.jgammon.tdlearn.codecs.OutputCodec;
import com.github.tkshio.jgammon.tdlearn.nntd.NNLayerExposer;
import com.github.tkshio.jgammon.tdlearn.nntd.NNTDLFactory;
import com.github.tkshio.jgammon.tdlearn.nntd.NNTDLearner;
import com.github.tkshio.jgammon.tdlearn.td.*;

/**
 * {@link TDLearner}オブジェクトをラップし、{@link StateEvaluator}の対として提供するクラス
 *
 * @param <STATE> 局面を表すクラス
 */
public class TDLearnEvaluator<STATE> {
    private final SGTuple<StateEvaluator<STATE>> evs;
    private final NNLayerExposer nni;

    private TDLearnEvaluator(SGTuple<StateEvaluator<STATE>> evs,
                             NNLayerExposer nni) {
        this.evs = evs;
        this.nni = nni;
    }

    /**
     * 設定情報から未学習の{@link TDLearner}を構築し、さらに評価器の対としてラップする
     *
     * @param conf    設定情報
     * @param <STATE> 局面を表すクラス
     * @return 生成されたTDLearnEvaluatorオブジェクト
     */
    public static <STATE> TDLearnEvaluator<STATE> create(TDConf<STATE> conf) {
        NNTDLearner<?> nntdLearner = NNTDLFactory.buildFactory(
                conf.getMatrixFactory(),
                conf.getInitializer())
                .create(
                        conf.getInputCodecs().getInputSize(),
                        conf.getHiddenNodes(),
                        conf.getOutputCodecs().getOutputSize()
                );

        return create(conf, nntdLearner);
    }

    /**
     * ファイルなどの読み込みですでに生成されたNNTDLearnerオブジェクトから、評価器を作成する
     *
     * @param conf        設定情報
     * @param nntdLearner 生成済みのNNTDLearnerオブジェクト
     * @param <STATE>     局面を表すクラス
     * @return 評価器
     */
    static <STATE>
    TDLearnEvaluator<STATE> create(TDConf<STATE> conf,
                                   NNTDLearner<?> nntdLearner) {
        return create(conf, nntdLearner, nntdLearner);
    }

    private static <DELTA, GRADIENT extends TDGradient<DELTA, GRADIENT>, STATE>
    TDLearnEvaluator<STATE> create(TDConf<STATE> conf,
                                   TDLearner<DELTA, GRADIENT> tdEv,
                                   NNLayerExposer nni) {

        StateManager<TDLearningState<GRADIENT>> sm = new StateManager<>();

        var td = TDTrainerBuilder.builder()
                .lambda(conf.getLambda())
                .learning_rate(conf.getLearning_rate())
                .build();

        var inputCodecs = conf.getInputCodecs();
        var outputCodecs = conf.getOutputCodecs();
        return new TDLearnEvaluator<>(
                SGTuple.of(
                        createEv(sm, tdEv, td,
                                inputCodecs.sente(), outputCodecs.sente()),
                        createEv(sm, tdEv, td,
                                inputCodecs.gote(), outputCodecs.gote())
                ),
                nni
        );
    }

    private static <D, G extends TDGradient<D, G>, STATE> StateEvaluator<STATE> createEv(
            StateManager<TDLearningState<G>> sm,
            TDLearner<D, G> tdLearner,
            TDTrainer td,
            InputCodec<STATE> inputCodec,
            OutputCodec<STATE> outputCodec) {

        return new StateEvaluator<>() {
            @Override
            public Eval eval(STATE state) {
                double[] input = inputCodec.encode(state);
                TDEval<G> TDEval = tdLearner.eval(input);
                DecodedEval<STATE> eval = outputCodec.decode(TDEval.getOutput(), state);
                return new Eval() {
                    @Override
                    public double getScore() {
                        return eval.getValue();
                    }

                    @Override
                    public void markAsChoice() {
                        learnFrom(TDEval);
                    }

                    @Override
                    public String asString() {
                        return eval.getDescription().apply(state);
                    }
                };
            }

            @Override
            public void won(STATE state) {
                td.commitWithReward(tdLearner, sm.get(),
                        outputCodec.encode(state));
                sm.reset();
            }

            @Override
            public void abort() {
                sm.reset();
            }

            private void learnFrom(TDEval<G> TDEval) {
                var lastState = sm.get();
                var nextState = td.learn(tdLearner, lastState, TDEval);
                sm.set(nextState);
            }


            @Override
            public void initialState(STATE state) {
                // initialStateは先手・後手で一回ずつ呼ばれるため、
                // 重複して初期化されるのを（念のため）回避している
                if (!sm.isInitialized()) {
                    sm.initWith(td.initialState(tdLearner,
                            inputCodec.encode(state)));
                }
            }
        };
    }

    /**
     * 評価器の対を返す
     *
     * @return 評価器の対
     */
    public SGTuple<StateEvaluator<STATE>> getEvs() {
        return evs;
    }

    public NNLayerExposer getTDNetworkInternal() {
        return nni;
    }

    /**
     * 初期化の有無を管理するためのクラス
     *
     * @param <T> 管理対象オブジェクトのクラス
     */
    private static class StateManager<T> {
        T tdState;
        private boolean isInitialized = false;

        public void reset() {
            isInitialized = false;
        }

        public T get() {
            return tdState;
        }

        public void set(T newState) {
            tdState = newState;
        }

        public boolean isInitialized() {
            return isInitialized;
        }

        public void initWith(T initialState) {
            isInitialized = true;
            tdState = initialState;
        }
    }
}