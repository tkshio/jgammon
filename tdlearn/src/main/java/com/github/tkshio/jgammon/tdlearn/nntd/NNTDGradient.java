package com.github.tkshio.jgammon.tdlearn.nntd;

import com.github.tkshio.jgammon.tdlearn.matrix.Matrix;
import com.github.tkshio.jgammon.tdlearn.nn.NNLayer;
import com.github.tkshio.jgammon.tdlearn.nn.NNLayerAmount;
import com.github.tkshio.jgammon.tdlearn.td.TDGradient;
import lombok.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@link NNTDLearner}の各層の勾配をまとめて保持する
 *
 * @param <MATRIX> 行列を表現するクラス
 */
@Value
public class NNTDGradient<MATRIX>
        implements TDGradient<NNTDDelta<MATRIX>, NNTDGradient<MATRIX>> {

    protected NNLayerAmount<MATRIX> outputGradient;
    protected List<NNLayerAmount<MATRIX>> hiddenGradients;

    private static <M>
    NNLayerAmount<M> calcDelta(double[] amount,
                               NNLayer<M> gradient) {

        Matrix<M> outputWeight = gradient.getWeight();
        var amounts = new double[outputWeight.rows()][outputWeight.columns()];
        Arrays.fill(amounts, amount);
        var _outputWeight = outputWeight.hadamard_product(amounts);
        var _outputBias = gradient.getBias().hadamard_product(new double[][]{amount});

        return new NNLayerAmount<>(_outputWeight, _outputBias);
    }

    @Override
    public NNTDGradient<MATRIX> accum(NNTDGradient<MATRIX> gradientToAdd,
                                      double lambda) {

        NNLayerAmount<MATRIX> outputSum = outputGradient.scalarMultiply(lambda).add(gradientToAdd.getOutputGradient());
        List<NNLayerAmount<MATRIX>> hiddenSums = new ArrayList<>(hiddenGradients.size());

        List<NNLayerAmount<MATRIX>> toAdd = gradientToAdd.getHiddenGradients();
        for (int i = 0; i < hiddenGradients.size(); i++) {
            var added = hiddenGradients.get(i).scalarMultiply(lambda).add(toAdd.get(i));
            hiddenSums.add(added);
        }

        return new NNTDGradient<>(outputSum, hiddenSums);
    }

    @Override
    public NNTDDelta<MATRIX> delta(double[] amount) {

        var _output = calcDelta(amount, outputGradient);
        var _hidden = hiddenGradients.get(0).scalarMultiply(amount[0]);

        for (int i = 1; i < amount.length; i++) {
            _hidden = _hidden.add(hiddenGradients.get(i).scalarMultiply(amount[i]));
        }

        return new NNTDDelta<>(_output, _hidden);
    }

}