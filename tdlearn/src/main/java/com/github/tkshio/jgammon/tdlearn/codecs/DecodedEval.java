package com.github.tkshio.jgammon.tdlearn.codecs;

import java.util.function.Function;

/**
 * OutputCodecの変換結果として使用するクラス
 *
 * <p>単純なスカラーである評価値に加え、ログ出力などに用いるテキストを定義する
 */
public interface DecodedEval<STATE> {
    /**
     * 評価値を得る
     *
     * @return 評価値は大きいほど有利なことを示す
     */
    double getValue();

    /**
     * テキストを遅延評価させる形で提供する
     *
     * @return テキストの内容は任意
     */
    Function<STATE, String> getDescription();
}
