package com.github.tkshio.jgammon.tdlearn;

import com.github.tkshio.jgammon.common.director.Player;
import com.github.tkshio.jgammon.common.evaluator.OnePlyPlayer;
import com.github.tkshio.jgammon.common.utils.SGTuple;
import com.github.tkshio.jgammon.gammon.BackgammonState;
import com.github.tkshio.jgammon.gammon.context.SimpleBGLogger;
import com.github.tkshio.jgammon.gammon.director.BackgammonAutoPlay;
import com.github.tkshio.jgammon.tdlearn.bg.BGConf;
import org.junit.Test;

import java.util.Random;

/*
任意のアルゴリズム同士での対戦の実行例
 */
public class TestVSPlay {
    @Test
    public void matchTDvsRandom() {

        var backgammonLogger = SimpleBGLogger.create();

        Random r = new Random();
        Player<BackgammonState> randomEvaluator =
                OnePlyPlayer.<BackgammonState>builder()
                        .evs(SGTuple.of(backgammonState -> r::nextGaussian))
                        .name("Random")
                        .build();

        TDConf<BackgammonState> conf = BGConf.builder().build();

        TDLearnEvaluator<BackgammonState> evaluators = TDLearnEvaluator.create(conf);
        OnePlyPlayer<BackgammonState> tdGammonEvaluator =
                OnePlyPlayer.<BackgammonState>builder()
                        .evs(evaluators.getEvs())
                        .name("TDGammon")
                        .build();

        BackgammonAutoPlay.builder()
                .contextHandler(backgammonLogger)
                .white(tdGammonEvaluator)
                .red(randomEvaluator).build().run(1);
    }
}
