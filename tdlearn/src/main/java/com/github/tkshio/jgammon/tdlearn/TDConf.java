package com.github.tkshio.jgammon.tdlearn;

import com.github.tkshio.jgammon.tdlearn.codecs.InputCodecs;
import com.github.tkshio.jgammon.tdlearn.codecs.OutputCodecs;
import com.github.tkshio.jgammon.tdlearn.matrix.ApacheMath3MatrixFactory;
import com.github.tkshio.jgammon.tdlearn.matrix.MatrixFactory;
import lombok.Builder;
import lombok.Value;

import java.util.Random;
import java.util.function.Supplier;

/**
 * 構築に必要な情報を保持する
 *
 * @param <STATE> 局面を表すクラス
 */
@Builder
@Value
public class TDConf<STATE> {
    private static final double LAMBDA_DEFAULT = 0.7;
    private static final double LRATE_DEFAULT = 0.01;
    private final static Random r = new Random();
    /**
     * STATE型のオブジェクトを入力層に変換するロジックの定義
     */
    InputCodecs<STATE> inputCodecs;
    /**
     * ニューラルネットワークからの出力を評価値に、およびその逆の変換を行うロジックの定義
     */
    OutputCodecs<STATE> outputCodecs;
    /**
     * 隠れ層のノード数
     */
    @Builder.Default
    int hiddenNodes = 80;
    /**
     * Temporal-Difference法で使用するλ
     *
     * <p>{@literal 0 <= λ <= 1}の範囲で指定する。デフォルトは{@value #LAMBDA_DEFAULT}。
     */
    @Builder.Default
    double lambda = LAMBDA_DEFAULT;
    /**
     * 学習時、誤差量に対して、実際に更新する量の割合
     *
     * <p>0以上１未満のごく小さい値を指定する。デフォルトは{@value #LRATE_DEFAULT}。
     */
    @Builder.Default
    double learning_rate = LRATE_DEFAULT;
    /**
     * 行列のファクトリークラス
     */
    @Builder.Default
    MatrixFactory<?> matrixFactory = new ApacheMath3MatrixFactory();
    /**
     * 行列の初期化関数
     */
    @Builder.Default
    Supplier<Double> initializer = () -> r.nextGaussian() * 0.01;

    /**
     * TDConfのビルダークラス
     *
     * @param <STATE> 局面を表すクラス
     */
    public static class TDConfBuilder<STATE> {
    }
}
