package com.github.tkshio.jgammon.tdlearn.codecs;

import java.util.function.Function;

/**
 * 先手・後手それぞれに対応した{@link OutputCodec}のペアを提供する
 *
 * @param <STATE> 入力対象となる局面を表すクラス
 */
public interface OutputCodecs<STATE> {

    /**
     * DecadedEvalのファクトリーメソッド
     *
     * @param <STATE> 局面を表すクラス
     * @param value   評価値
     * @param desc    任意のテキスト
     * @return {@code DecodedEval}
     */
    static <STATE> DecodedEval<STATE> toEval(double value, Function<STATE, String> desc) {
        return new DecodedEval<>() {
            @Override
            public double getValue() {
                return value;
            }

            @Override
            public Function<STATE, String> getDescription() {
                return desc;
            }
        };
    }

    /**
     * 先手の局面に合わせた変換を行う{@link OutputCodec}を返す
     *
     * @return {@code OutputCodec}オブジェクト
     */
    OutputCodec<STATE> sente();

    /**
     * 後手の局面に合わせた変換を行う{@link OutputCodec}を返す
     *
     * @return {@code OutputCodec}オブジェクト
     */
    OutputCodec<STATE> gote();

    /**
     * OutputCodecによって生成されるデータの個数（出力ノード数）
     *
     * @return データの個数
     */
    int getOutputSize();
}
