package com.github.tkshio.jgammon.tdlearn.nntd;

import com.github.tkshio.jgammon.tdlearn.matrix.MatrixFactory;
import com.github.tkshio.jgammon.tdlearn.nn.NNLayer;
import com.github.tkshio.jgammon.tdlearn.nn.NNLayerAmount;
import com.github.tkshio.jgammon.tdlearn.nn.NNLayerGradient;
import com.github.tkshio.jgammon.tdlearn.td.TDEval;
import com.github.tkshio.jgammon.tdlearn.td.TDLearner;

import java.util.List;
import java.util.function.Supplier;

/**
 * {@link TDLearner}の実装クラス
 *
 * <p> Temporal-Difference法では評価の都度更新量を算出するが、
 * 本来評価値の誤差は最終段階まで得られない。
 * そのため、通常の逆伝播法とは異なる取り扱いをする必要があり、
 * このクラスではそれが実装されている。
 *
 * @param <MATRIX> 行列を表現するクラス
 */
public class NNTDLearner<MATRIX> implements TDLearner<NNTDDelta<MATRIX>,
        NNTDGradient<MATRIX>>,
        NNLayerExposer {

    private final MatrixFactory<MATRIX> matrixFactory;

    private NNLayer<MATRIX> hiddenLayer;
    private NNLayer<MATRIX> outputLayer;

    NNTDLearner(MatrixFactory<MATRIX> matrixFactory,
                NNLayer<MATRIX> hiddenLayer,
                NNLayer<MATRIX> outputLayer

    ) {

        this.matrixFactory = matrixFactory;
        this.hiddenLayer = hiddenLayer;
        this.outputLayer = outputLayer;
    }


    @Override
    public TDEval<NNTDGradient<MATRIX>> eval(double[] input) {

        var inputMatrix = matrixFactory.create(new double[][]{input});
        var hiddenOut = hiddenLayer.apply(inputMatrix);
        var outputOut = outputLayer.apply(hiddenOut);

        // TDLearnはこの二つの勾配を受け取り、次回の学習時の誤差を掛けて更新量を得る
        Supplier<NNTDGradient<MATRIX>> gradient = () ->
        {
            NNLayerAmount<MATRIX> outputGradientAmount;
            List<NNLayerAmount<MATRIX>> hiddenGradientAmounts;

            {
                // 出力層については、出力層の勾配は単純に求めればよい
                NNLayerGradient<MATRIX> outputLayerGradient =
                        outputLayer.calcGradient(hiddenOut, outputOut);

                outputGradientAmount = outputLayerGradient.getAmount();
                // 通常の誤差伝播法では出力層の勾配を出した次に隠れ層へ遡及させるが、
                // TDでは遡及させる値を出力層の各ノード別に分けて保持させる
                // （誤差情報がないので、それぞれの値をそのまま足せない：
                // 通常の誤差伝播法であれば、誤差で重みづけして加算する）

                // 出力層の勾配を隠れ層の出力にかけておいてから、勾配を算出させる
                hiddenGradientAmounts = hiddenLayer.calcGradients(
                        inputMatrix, hiddenOut, outputLayerGradient);
            }

            return new NNTDGradient<>(outputGradientAmount, hiddenGradientAmounts);
        };

        return new TDEval<>() {
            @Override
            public double[] getOutput() {
                return outputOut.getData()[0];
            }

            @Override
            public NNTDGradient<MATRIX> getGradient() {
                return gradient.get();
            }
        };
    }

    @Override
    public void update(NNTDDelta<MATRIX> sum) {
        outputLayer = outputLayer.update(sum.getOutput());
        hiddenLayer = hiddenLayer.update(sum.getHidden());
    }

    @Override
    public NNLayer<MATRIX> getHiddenLayer() {
        return hiddenLayer;
    }

    @Override
    public NNLayer<MATRIX> getOutputLayer() {
        return outputLayer;
    }
}

