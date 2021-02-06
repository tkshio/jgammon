package com.github.tkshio.jgammon;

import com.github.tkshio.jgammon.common.director.Player;
import com.github.tkshio.jgammon.common.evaluator.OnePlyPlayer;
import com.github.tkshio.jgammon.common.evaluator.TwoPlyPlayer;
import com.github.tkshio.jgammon.gammon.BackgammonState;
import com.github.tkshio.jgammon.tdlearn.TDConf;
import com.github.tkshio.jgammon.tdlearn.TDLearnEvaluatorReader;
import com.github.tkshio.jgammon.tdlearn.bg.BGConf;

import java.io.IOException;
import java.io.InputStream;

public class TDPlayerBuilder {
    private final static String path = "/td_default.txt";

    public static Player<BackgammonState> buildPlayerFromInputStream(
            InputStream is,
            int depth) throws IOException {
        TDConf<BackgammonState> conf = BGConf.builder().build();
        return buildPlayerFromInputStream(is, depth, conf);
    }

    public static Player<BackgammonState> buildPlayerFromInputStream(
            InputStream is,
            int depth, TDConf<BackgammonState> conf) throws IOException {

        var evs = TDLearnEvaluatorReader.readAsStableEv(conf, is);

        Player<BackgammonState> player;
        if (depth == 1) {
            player = OnePlyPlayer.<BackgammonState>builder()
                    .evs(evs)
                    .name("TDLearn(1ply)")
                    .build();
        } else {
            player = TwoPlyPlayer.<BackgammonState>builder()
                    .evs(evs)
                    .name("TDLearn(2ply)")
                    .build();
        }
        return player;
    }

    public static Player<BackgammonState> buildDefaultPlayer(int depth) throws IOException {
        try (InputStream is = getDefaultTDAsStream()) {
            return TDPlayerBuilder.buildPlayerFromInputStream(is, depth);
        }
    }

    public static InputStream getDefaultTDAsStream() {
        return TDPlayerBuilder.class.getResourceAsStream(path);
    }
}
