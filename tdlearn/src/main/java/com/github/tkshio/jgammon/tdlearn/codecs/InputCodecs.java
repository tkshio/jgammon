package com.github.tkshio.jgammon.tdlearn.codecs;

/**
 * 先手・後手それぞれに対応した{@link InputCodec}のペアを提供する
 *
 * @param <STATE> 入力対象となる局面を表すクラス
 */
public interface InputCodecs<STATE> {

    /**
     * 先手の局面の変換を行う{@link InputCodec}を返す
     *
     * @return {@code InputCodec}オブジェクト
     */
    InputCodec<STATE> sente();

    /**
     * 後手の局面の変換を行う{@link InputCodec}を返す
     *
     * @return {@code InputCodec}オブジェクト
     */
    InputCodec<STATE> gote();

    /**
     * InputCodecによって生成されるデータの個数（入力ノード数）
     *
     * @return データの個数
     */
    int getInputSize();

}
