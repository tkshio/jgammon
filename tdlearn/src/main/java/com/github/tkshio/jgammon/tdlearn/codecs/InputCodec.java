package com.github.tkshio.jgammon.tdlearn.codecs;

/**
 * ニューラルネットワークへ入力できる形式にデータを変換する
 *
 * @param <STATE> 入力対象となる局面を表すクラス
 */
public interface InputCodec<STATE> {
    /**
     * 与えられた局面を変換する
     *
     * @param state 対象となる局面
     * @return 変換結果
     */
    double[] encode(STATE state);
}
