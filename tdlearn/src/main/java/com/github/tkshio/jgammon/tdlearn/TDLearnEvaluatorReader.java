package com.github.tkshio.jgammon.tdlearn;

import com.github.tkshio.jgammon.common.evaluator.Eval;
import com.github.tkshio.jgammon.common.evaluator.StateEvaluator;
import com.github.tkshio.jgammon.common.utils.SGTuple;
import com.github.tkshio.jgammon.tdlearn.codecs.InputCodec;
import com.github.tkshio.jgammon.tdlearn.codecs.OutputCodec;
import com.github.tkshio.jgammon.tdlearn.matrix.Matrix;
import com.github.tkshio.jgammon.tdlearn.matrix.MatrixFactory;
import com.github.tkshio.jgammon.tdlearn.nn.NNLayer;
import com.github.tkshio.jgammon.tdlearn.nn.NNLayerFactory;
import com.github.tkshio.jgammon.tdlearn.nntd.NNTDLFactory;
import com.github.tkshio.jgammon.tdlearn.nntd.NNTDLearner;
import com.github.tkshio.jgammon.tdlearn.td.TDLearner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * {@link TDNetworkWriter}によって出力されたファイルを読み取り、{@link StateEvaluator}として返す
 */
public class TDLearnEvaluatorReader {
    /**
     * 指定された入力から評価器の対を生成する
     *
     * <p> 生成された評価器は学習機能を持たない。ネットワークのノード数はファイルの内容から自動的に決定される。また、{@link TDConf}からはCodecを取得している。
     *
     * <p>なお、ファイルの内容の検査は行っていない。
     *
     * @param conf    設定情報
     * @param reader  入力
     * @param <STATE> 局面を表すクラス
     * @return 生成された評価器の対
     * @throws IOException ファイル読み込みにおいて何らかのエラーが発生した
     */
    public static <STATE>
    SGTuple<StateEvaluator<STATE>> readAsStableEv(TDConf<STATE> conf,
                                                  InputStream is) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        var tdLearner = read(conf, reader);

        // Codecをファイルから指定できるようにすると、TDConfは不要になる
        StateEvaluator<STATE> senteEv = createEv(
                tdLearner,
                conf.getInputCodecs().sente(),
                conf.getOutputCodecs().sente()
        );

        StateEvaluator<STATE> goteEv = createEv(
                tdLearner,
                conf.getInputCodecs().gote(),
                conf.getOutputCodecs().gote()
        );

        return SGTuple.of(senteEv, goteEv);
    }

    /**
     * 指定された入力からTDLearnEvaluatorを生成する
     *
     * <p> ネットワークのノード数は入力内容から自動的に決定される。また、{@link TDConf}からはCodecを取得している。
     *
     * <p>なお、ファイルの内容の検査は行っていない。
     *
     * @param <STATE> 局面を表すクラス
     * @param conf    設定情報
     * @param reader  入力
     * @return 生成されたオブジェクト
     * @throws IOException ファイル読み込みにおいて何らかのエラーが発生した
     */
    public static <STATE>
    TDLearnEvaluator<STATE> readAsTrainableEv(
            TDConf<STATE> conf,
            BufferedReader reader) throws IOException {

        NNTDLearner<?> nntdLearner = read(
                conf.getMatrixFactory(),
                conf.getInitializer(),
                reader);

        return TDLearnEvaluator.create(conf, nntdLearner);
    }

    private static <STATE> NNTDLearner<?> read(TDConf<STATE> conf,
                                               BufferedReader reader) throws IOException {
        return read(conf.getMatrixFactory(), conf.getInitializer(), reader);
    }

    private static <M> NNTDLearner<M> read(MatrixFactory<M> matrixFactory,
                                           Supplier<Double> initializer,
                                           BufferedReader reader) throws IOException {
        NNTDLearner<M> nntdNetworkFactory;
        {
            NNLayer<M> hiddenLayer;
            NNLayer<M> outputLayer;
            // ファイルに並んでいる順に１レイヤーずつ読み取って、TDNを構成する
            // 生成したEvaluatorはTDLearnEvaluatorではないので、学習機能もない
            {
                Matrix<M> hiddenWeight = readMatrix(reader, matrixFactory);
                Matrix<M> hiddenBias = readMatrix(reader, matrixFactory);
                Matrix<M> outputWeight = readMatrix(reader, matrixFactory);
                Matrix<M> outputBias = readMatrix(reader, matrixFactory);

                hiddenLayer = NNLayerFactory.create(hiddenWeight, hiddenBias);
                outputLayer = NNLayerFactory.create(outputWeight, outputBias);
            }
            nntdNetworkFactory = NNTDLFactory.buildFactory(matrixFactory,
                    initializer)
                    .create(hiddenLayer, outputLayer);

        }
        return nntdNetworkFactory;
    }

    private static <STATE>
    StateEvaluator<STATE> createEv(TDLearner<?, ?> tdLearner,
                                   InputCodec<STATE> inputCodec,
                                   OutputCodec<STATE> outputCodec
    ) {
        return state -> {
            double[] in = inputCodec.encode(state);
            double[] out = tdLearner.eval(in).getOutput();
            var eval = outputCodec
                    .decode(out, state);

            return new Eval() {
                @Override
                public String asString() {
                    return eval.getDescription().apply(state);
                }

                @Override
                public double getScore() {
                    return eval.getValue();
                }
            };
        };
    }

    private static <M> Matrix<M> readMatrix(BufferedReader reader,
                                            MatrixFactory<M> matrixFactory
    ) throws IOException {
        String line;
        List<double[]> data = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("/")) {
                continue;
            }
            if (line.isEmpty()) {
                break;
            }
            String[] valueStrings = line.split(",");
            double[] values = new double[valueStrings.length];
            Arrays.setAll(values, i -> Double.parseDouble(valueStrings[i]));
            data.add(values);
        }

        double[][] matrix = data.toArray(new double[][]{});
        return matrixFactory.create(matrix);
    }
}
