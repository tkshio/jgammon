package com.github.tkshio.jgammon.tdlearn.nntd;

import com.github.tkshio.jgammon.tdlearn.matrix.ApacheMath3MatrixFactory;
import com.github.tkshio.jgammon.tdlearn.matrix.MatrixFactory;
import com.github.tkshio.jgammon.tdlearn.nn.NNLayer;
import com.github.tkshio.jgammon.tdlearn.nn.NNLayerFactory;
import com.github.tkshio.jgammon.tdlearn.td.TDLearner;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.Random;
import java.util.function.Supplier;

/**
 * {@link NNTDLearner}のファクトリークラス
 * <p>
 * 行列を表現するクラスを指定する役割を担っている。
 *
 * @param <MATRIX> 行列を表現するクラス
 */
public class NNTDLFactory<MATRIX> {
    private final Supplier<Double> initializer;
    private final MatrixFactory<MATRIX> matrixFactory;


    private NNTDLFactory(MatrixFactory<MATRIX> matrixFactory,
                         Supplier<Double> initializer) {
        this.matrixFactory = matrixFactory;
        this.initializer = initializer;
    }

    /**
     * デフォルトである、Apache Math3を使用して構築する
     *
     * @return 構築されたオブジェクト
     */
    public static NNTDLFactory<RealMatrix> buildFactory() {
        Random r = new Random();
        return new NNTDLFactory<>(new ApacheMath3MatrixFactory(),
                () -> r.nextGaussian() * 0.01);
    }

    /**
     * 行列を表現するクラスのファクトリーを指定して構築する
     *
     * @param matrixFactory 行列を表現するクラスのファクトリー
     * @param initializer   行列の初期化関数
     * @param <M>           行列を表現するクラス
     * @return 構築されたオブジェクト
     */
    public static <M>
    NNTDLFactory<M> buildFactory(MatrixFactory<M> matrixFactory,
                                 Supplier<Double> initializer) {
        return new NNTDLFactory<>(matrixFactory, initializer);
    }

    private static <MATRIX>
    NNTDLearner<MATRIX> create(MatrixFactory<MATRIX> matrixFactory,
                               NNLayer<MATRIX> hiddenLayer,
                               NNLayer<MATRIX> outputLayer) {

        return new NNTDLearner<>(matrixFactory, hiddenLayer, outputLayer);
    }

    /**
     * ノード数を指定して{@link TDLearner}を構築する
     *
     * @param inputNodeNum  入力ノードの数
     * @param hiddenNodeNum 隠れノードの数
     * @param outputNodeNum 出力ノードの数
     * @return 生成されたTDNetwork
     */
    public NNTDLearner<MATRIX> create(int inputNodeNum,
                                      int hiddenNodeNum,
                                      int outputNodeNum) {

        var outputLayer = NNLayerFactory.create(matrixFactory,
                hiddenNodeNum, outputNodeNum, initializer);
        var hiddenLayer = NNLayerFactory.create(matrixFactory,
                inputNodeNum, hiddenNodeNum, initializer);

        return create(matrixFactory, hiddenLayer, outputLayer);
    }

    /**
     * すでに構築された{@link NNLayer}から{@link TDLearner}を構築する
     *
     * @param hiddenLayer 隠れ層
     * @param outputLayer 出力層
     * @return 構築されたTDNetwork
     */
    public NNTDLearner<MATRIX> create(NNLayer<MATRIX> hiddenLayer,
                                      NNLayer<MATRIX> outputLayer) {
        return create(matrixFactory, hiddenLayer, outputLayer);
    }
}
