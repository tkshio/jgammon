package com.github.tkshio.jgammon.tdlearn.bg;

import com.github.tkshio.jgammon.gammon.BackgammonState;
import com.github.tkshio.jgammon.tdlearn.TDConf;

/**
 * バックギャモン固有の定義があらかじめ指定されているTDConfのビルダーメソッド
 *
 * <p>現在は{@code Input/OutputCodecs}のみ指定している。
 */
public class BGConf {
    public static TDConf.TDConfBuilder<BackgammonState> builder() {
        return TDConf.<BackgammonState>builder()
                .inputCodecs(BGInputCodecs.DEFAULT)
                .outputCodecs(BGOutputCodecs.DEFAULT);
    }

}
