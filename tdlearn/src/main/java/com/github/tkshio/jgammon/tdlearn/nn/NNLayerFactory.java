package com.github.tkshio.jgammon.tdlearn.nn;

import com.github.tkshio.jgammon.tdlearn.matrix.Matrix;
import com.github.tkshio.jgammon.tdlearn.matrix.MatrixFactory;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * {@link NNLayer}のファクトリークラス
 */
public class NNLayerFactory {
    /**
     * NNLayerオブジェクトを生成する
     *
     * @param matrixFactory 行列を生成するファクトリークラス
     * @param inputNodes    入力ノード数
     * @param outputNodes   出力ノード数
     * @param initializer   初期値を設定するメソッド
     * @param <MATRIX>      行列を表現するクラス
     * @return 生成されたオブジェクト
     */
    public static <MATRIX> NNLayer<MATRIX> create(MatrixFactory<MATRIX> matrixFactory, int inputNodes, int outputNodes, Supplier<Double> initializer) {

        double[][] weightArray = new double[inputNodes][];
        Arrays.setAll(weightArray, (int row) -> {
            double[] rowArr = new double[outputNodes];
            Arrays.setAll(rowArr, col -> initializer.get());
            return rowArr;
        });

        double[][] biasArray = new double[1][outputNodes];
        Arrays.setAll(biasArray[0], (int col) -> initializer.get());

        var weight = matrixFactory.create(weightArray);
        var bias = matrixFactory.create(biasArray);
        return create(weight, bias);
    }

    /**
     * あらかじめ用意された行列でNNLayerオブジェクトを生成する
     *
     * @param weight   重みを定義した行列
     * @param bias     バイアスを定義した行列
     * @param <MATRIX> 行列を表現するクラス
     * @return 生成されたオブジェクト
     */
    public static <MATRIX> NNLayer<MATRIX> create(Matrix<MATRIX> weight, Matrix<MATRIX> bias) {
        return new NNLayerImpl<>(weight, bias);
    }
}
