package com.github.tkshio.jgammon.bench;

import com.github.tkshio.jgammon.TDPlayerBuilder;
import com.github.tkshio.jgammon.common.director.Player;
import com.github.tkshio.jgammon.gammon.BackgammonState;
import com.github.tkshio.jgammon.gammon.context.SimpleBGLogger;
import com.github.tkshio.jgammon.gammon.director.BackgammonAutoPlay;
import com.github.tkshio.jgammon.gammon.director.BackgammonDirectorConf;
import com.github.tkshio.jgammon.tdlearn.TDConf;
import com.github.tkshio.jgammon.tdlearn.bg.BGInputCodecsLegacy;
import com.github.tkshio.jgammon.tdlearn.bg.BGOutputCodecs;

import java.io.IOException;
import java.io.InputStream;

/*
任意のアルゴリズム同士での対戦の実行例
 */
public class TestVSPlay {
    public static void main(String[] args) throws IOException {
        matchTDLegacyVSLatestDefault();
    }

    static void matchTDLegacyVSLatestDefault() throws IOException {

        var backgammonLogger = SimpleBGLogger.create();

        int depth = 1;
        BackgammonDirectorConf dConf = BackgammonDirectorConf.onePlyDirectorConf();

        Player<BackgammonState> before = getLegacyPlayer(depth);
        Player<BackgammonState> after = TDPlayerBuilder.buildDefaultPlayer(depth);

        BackgammonAutoPlay.builder()
                .dConf(dConf)
                .contextHandler(backgammonLogger)
                .white(before)
                .red(after).build().run(100);
    }

    private static Player<BackgammonState> getLegacyPlayer(int depth) throws IOException {
        TDConf<BackgammonState> tdConf = TDConf.<BackgammonState>builder()
                .inputCodecs(BGInputCodecsLegacy.LEGACY_1_0)
                .outputCodecs(BGOutputCodecs.DEFAULT)
                .build();

        String path = "/td_legacy.1.0.txt";
        try (InputStream is = TestVSPlay.class.getResourceAsStream(path)) {
            return TDPlayerBuilder.buildPlayerFromInputStream(is, depth, tdConf);
        }
    }
}
