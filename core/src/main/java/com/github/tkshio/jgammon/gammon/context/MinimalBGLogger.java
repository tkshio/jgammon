package com.github.tkshio.jgammon.gammon.context;

import com.github.tkshio.jgammon.common.context.GameContextHandler;
import com.github.tkshio.jgammon.common.director.GameInfo;
import com.github.tkshio.jgammon.common.director.Player;
import com.github.tkshio.jgammon.gammon.BackgammonState;

import java.util.function.Consumer;

public class MinimalBGLogger implements GameContextHandler<BackgammonState> {
    private final Consumer<String> out;
    SimpleBGFormatter fmt = new SimpleBGFormatter();

    private MinimalBGLogger(Consumer<String> out) {
        this.out = out;
    }

    public static MinimalBGLogger create() {
        return create(System.out::println);
    }

    public static MinimalBGLogger create(Consumer<String> out) {
        return new MinimalBGLogger(out);
    }

    public static MinimalBGLogger create(Consumer<String> out,
                                         Consumer<String> progressOut) {
        return new MinimalBGLogger(out) {
            @Override
            public void whiteWin(GameInfo info,
                                 Player<BackgammonState> winner,
                                 Player<BackgammonState> loser,
                                 BackgammonState finalState) {
                win(info, winner, loser, finalState);
            }

            @Override
            public void redWin(GameInfo info,
                               Player<BackgammonState> winner,
                               Player<BackgammonState> loser,
                               BackgammonState finalState) {
                win(info, winner, loser, finalState);
            }

            private void win(GameInfo info,
                             Player<BackgammonState> winner,
                             Player<BackgammonState> loser,
                             BackgammonState finalState) {
                var msg = fmt.endOfGame(winner, loser, finalState, info);
                progressOut.accept(msg);

            }


            @Override
            public void abortGame(BackgammonState state) {
                var msg = "ABORT";
                progressOut.accept(msg);

            }
        };
    }


    @Override
    public void startTrials(int fromInclusive, int n) {
        out.accept(fmt.startTrials(fromInclusive, n));
    }


    @Override
    public void endTrials(long n, String redLabel, int redWin,
                          String whiteLabel, int whiteWin, String... msgs) {
        for (String msg : fmt.endTrials(n, redLabel, redWin,
                whiteLabel, whiteWin, msgs)) {
            out.accept(msg);
        }
    }

}

