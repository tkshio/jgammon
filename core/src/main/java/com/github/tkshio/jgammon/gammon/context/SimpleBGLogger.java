package com.github.tkshio.jgammon.gammon.context;

import com.github.tkshio.jgammon.common.context.GameContextHandler;
import com.github.tkshio.jgammon.common.director.GameInfo;
import com.github.tkshio.jgammon.common.director.Player;
import com.github.tkshio.jgammon.common.evaluator.Eval;
import com.github.tkshio.jgammon.gammon.BackgammonState;
import com.github.tkshio.jgammon.gammon.move.CheckerPlay;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SimpleBGLogger implements GameContextHandler<BackgammonState> {
    private final Consumer<String> out;

    private final SimpleBGFormatter fmt = new SimpleBGFormatter();

    public SimpleBGLogger(Consumer<String> out) {
        this.out = out;
    }

    public static SimpleBGLogger create() {
        return new SimpleBGLogger(System.out::println);
    }

    public static SimpleBGLogger create(Consumer<String> out) {
        return new SimpleBGLogger(out);
    }


    @Override
    public void startGame(GameInfo info, BackgammonState state) {
        String[] msgs = fmt.startGame(info, state);
        for (var msg : msgs) {
            out.accept(msg);
        }
    }

    @Override
    public void abortGame(BackgammonState state) {
        String msg = fmt.abortGame(state);
        out.accept(msg);
    }

    @Override
    public void startTrials(int fromInclusive, int n) {
        String msg = fmt.startTrials(fromInclusive, n);
        out.accept(msg);
    }

    @Override
    public void endTrials(long n,
                          String redLabel, int redWin,
                          String whiteLabel, int whiteWin,
                          String... msgs) {
        String[] logMsgs = fmt.endTrials(n,
                redLabel, redWin,
                whiteLabel, whiteWin,
                msgs);
        for (String msg : logMsgs) {
            out.accept(msg);
        }
    }

    @Override
    public void beginWhitePly(String label, BackgammonState state) {
        String[] msgs = fmt.ply(label, state);
        for (String msg : msgs) {
            out.accept(msg);
        }
    }


    @Override
    public void endWhitePly(String label, BackgammonState state) {
        String[] msgs = fmt.formatMoveAndBoardWhite(label, state);
        for (String msg : msgs) {
            out.accept(msg);
        }
    }

    @Override
    public void beginRedPly(String label, BackgammonState state) {
        String[] msgs = fmt.ply(label, state);
        for (String msg : msgs) {
            out.accept(msg);
        }
    }

    @Override
    public void endRedPly(String label, BackgammonState state) {
        String[] msgs = fmt.formatMoveAndBoardRed(label, state);
        for (String msg : msgs) {
            out.accept(msg);
        }
    }

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
        String msg = fmt.endOfGame(winner, loser, finalState, info);
        out.accept(msg);
    }


    @Override
    public void startEvaluation(String label, BackgammonState
            current, Supplier<Stream<BackgammonState>> options) {
        String[] msgs = fmt.startEvaluation(label, current, options);
        for (String msg : msgs) {
            out.accept(msg);
        }
    }


    @Override
    public void redMoveEvaluated(BackgammonState state, Eval e) {
        String msg = fmt.evaluateMove(state, e, CheckerPlay::formatInDesc);
        out.accept(msg);
    }

    @Override
    public void whiteMoveEvaluated(BackgammonState state, Eval e) {
        String msg = fmt.evaluateMove(state, e, CheckerPlay::formatInAsc);
        out.accept(msg);
    }


    @Override
    public void endEvaluation(String label, BackgammonState state, Eval e) {
        String msg = fmt.endEvaluation(label, state, e);
        out.accept(msg);
    }
}
