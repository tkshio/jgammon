package com.github.tkshio.jgammon.tdlearn.nn;

import com.github.tkshio.jgammon.tdlearn.matrix.Matrix;

/**
 * {@link NNLayer}に対する演算量を表現する、加算・乗算可能なクラス
 *
 * @param <MATRIX> 行列を表現するクラス
 */
public class NNLayerAmount<MATRIX> extends NNLayerImpl<MATRIX> {
    public NNLayerAmount(Matrix<MATRIX> outputWeight, Matrix<MATRIX> outputBias) {
        super(outputWeight, outputBias);
    }

    /**
     * 加算した結果を新しいオブジェクトとして返す
     *
     * <p>このメソッドの呼び出しは、元のオブジェクトには影響を与えない。
     *
     * @param delta 加算する値
     * @return 加算した結果
     */
    public NNLayerAmount<MATRIX> add(NNLayerAmount<MATRIX> delta) {
        var _weight = weight.add(delta.getWeight());
        var _bias = bias.add(delta.getBias());
        return new NNLayerAmount<>(_weight, _bias);
    }

    /**
     * 指定した値を乗算した結果を新しいオブジェクトとして返す
     *
     * <p>このメソッドの呼び出しは、元のオブジェクトには影響を与えない。
     *
     * @param lambda 乗算する値
     * @return 乗算した結果
     */
    public NNLayerAmount<MATRIX> scalarMultiply(double lambda) {
        return new NNLayerAmount<>(weight.scalarMultiply(lambda), bias.scalarMultiply(lambda));
    }


}
