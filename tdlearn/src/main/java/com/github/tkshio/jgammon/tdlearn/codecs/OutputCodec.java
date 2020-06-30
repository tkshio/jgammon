package com.github.tkshio.jgammon.tdlearn.codecs;

/**
 * ニューラルネットワークの出力値を比較可能な評価値に変換する
 *
 * @param <STATE> 局面を表すクラス
 */
public interface OutputCodec<STATE> {
    /**
     * ニューラルネットワークの出力を評価値に変換する
     *
     * <p>終了状態になっている場合など、局面で評価値が決まる場合はその値を採用する
     *
     * @param values ニューラルネットワークからの出力
     * @param state  上記の出力に対応する局面
     * @return 評価結果
     */
    DecodedEval<STATE> decode(double[] values, STATE state);

    /**
     * 局面が終了状態になっている場合、それに対応するニューラルネットワークの出力値を返す
     *
     * <p>この値は、Temporal-Differenceでの学習の最終段階に使用する
     *
     * @param state 局面
     * @return ニューラルネットワークの出力値
     */
    double[] encode(STATE state);
}
