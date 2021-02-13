package com.github.tkshio.jgammon;

import com.github.tkshio.jgammon.common.evaluator.OnePlyPlayer;
import com.github.tkshio.jgammon.common.evaluator.PlayerBuilder;
import com.github.tkshio.jgammon.common.evaluator.TwoPlyPlayer;
import com.github.tkshio.jgammon.gammon.BackgammonState;
import com.github.tkshio.jgammon.tdlearn.TDConf;
import com.github.tkshio.jgammon.tdlearn.TDLearnEvaluatorReader;
import com.github.tkshio.jgammon.tdlearn.bg.BGConf;

import java.io.IOException;
import java.io.InputStream;

public class TDPlayerBuilder {
    private final static String path = "/td_default.txt";

    public static PlayerBuilder<BackgammonState> playerBuilderWithInputStream(
            InputStream is,
            int depth) throws IOException {
        TDConf<BackgammonState> conf = BGConf.builder().build();
        return playerBuilderWithInputStream(is, depth, conf);
    }

    public static PlayerBuilder<BackgammonState> playerBuilderWithInputStream(
            InputStream is,
            int depth, TDConf<BackgammonState> conf) throws IOException {

        var evs = TDLearnEvaluatorReader.readAsStableEv(conf, is);


        PlayerBuilder<BackgammonState> player;
        if (depth == 1) {
            player = OnePlyPlayer.<BackgammonState>builder()
                    .evs(evs)
                    .name("TDLearn(1ply)");
        } else {
            player = TwoPlyPlayer.<BackgammonState>builder()
                    .evs(evs)
                    .name("TDLearn(2ply)");
        }
        return player;
    }

    public static PlayerBuilder<BackgammonState> defaultPlayerBuilder(int depth) throws IOException {
        try (InputStream is = getDefaultTDAsStream()) {
            return TDPlayerBuilder.playerBuilderWithInputStream(is, depth);
        }
    }

    public static InputStream getDefaultTDAsStream() {
        return TDPlayerBuilder.class.getResourceAsStream(path);
    }
}
