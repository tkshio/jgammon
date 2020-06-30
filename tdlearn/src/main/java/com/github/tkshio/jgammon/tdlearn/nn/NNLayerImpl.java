package com.github.tkshio.jgammon.tdlearn.nn;

import com.github.tkshio.jgammon.tdlearn.matrix.Matrix;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link NNLayer}の実装クラス
 *
 * @param <MATRIX> 行列を表現するクラス
 */
@Data
class NNLayerImpl<MATRIX> implements NNLayer<MATRIX> {
    protected final Matrix<MATRIX> weight;
    protected final Matrix<MATRIX> bias;

    /**
     * コンストラクター
     *
     * @param weight 重み
     * @param bias   バイアス
     */
    NNLayerImpl(Matrix<MATRIX> weight, Matrix<MATRIX> bias) {
        this.weight = weight;
        this.bias = bias;
    }

    @Override
    public Matrix<MATRIX> apply(Matrix<MATRIX> input) {
        return input.multiply(weight).add(bias).sigmoid();
    }


    @Override
    public NNLayer<MATRIX> update(NNLayerAmount<MATRIX> delta) {
        var _weight = weight.add(delta.getWeight());
        var _bias = bias.add(delta.getBias());
        return new NNLayerImpl<>(_weight, _bias);
    }


    @Override
    public NNLayerGradient<MATRIX> calcGradient(Matrix<MATRIX> input, Matrix<MATRIX> output) {
        var outputGradient = output.sigmoidDev();
        var weightGradient = input.transpose().multiply(outputGradient);
        return new NNLayerGradient<>(new NNLayerAmount<>(weightGradient, outputGradient), this);
    }

    @Override
    public List<NNLayerAmount<MATRIX>> calcGradients(Matrix<MATRIX> input, Matrix<MATRIX> output, NNLayerGradient<MATRIX> gradient) {
        List<NNLayerAmount<MATRIX>> gradients = new ArrayList<>();
        var outputGradient = output.sigmoidDev();
        var inputTranspose = input.transpose();

        for (Matrix<MATRIX> g : gradient.getGradients()) {
            var biasGradient = outputGradient.hadamard_product(g);
            var weightGradient = inputTranspose.multiply(biasGradient);

            gradients.add(new NNLayerAmount<>(weightGradient, biasGradient));
        }
        return gradients;
    }

}
