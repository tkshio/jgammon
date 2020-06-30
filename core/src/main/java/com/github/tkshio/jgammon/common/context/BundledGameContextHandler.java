package com.github.tkshio.jgammon.common.context;

import com.github.tkshio.jgammon.common.director.GameInfo;
import com.github.tkshio.jgammon.common.director.Player;
import com.github.tkshio.jgammon.common.evaluator.Eval;
import lombok.Builder;
import lombok.Singular;

import java.io.PrintStream;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 複数のGameContextHandlerをまとめて処理するクラス
 *
 * @param <STATE>局面を表すクラス
 */
@Builder
public class BundledGameContextHandler<STATE> implements GameContextHandler<STATE> {

    @Singular
    private final List<GameContextHandler<STATE>> handlers;
    private final PrintStream out;

    @Override
    public void startTrials(int fromInclusive, int n) {
        handlers.forEach(handler -> handler.startTrials(fromInclusive, n));
    }

    @Override
    public void endTrials(long n,
                          String redLabel, int redPoints,
                          String whiteLabel, int whitePoints,
                          String... msgs) {
        handlers.forEach(handler -> handler.endTrials(n,
                redLabel, redPoints,
                whiteLabel, whitePoints,
                msgs));
    }

    @Override
    public void startGame(GameInfo info, STATE initialState) {
        handlers.forEach(handler -> handler.startGame(info, initialState));
    }

    @Override
    public void abortGame(STATE initialState) {
        handlers.forEach(handler -> handler.abortGame(initialState));
    }

    @Override
    public void whiteWin(GameInfo info, Player<STATE> winner, Player<STATE> loser, STATE finalState) {
        handlers.forEach(handler -> handler.whiteWin(info, winner, loser, finalState));
    }

    @Override
    public void redWin(GameInfo info, Player<STATE> winner, Player<STATE> loser, STATE finalState) {
        handlers.forEach(handler -> handler.redWin(info, winner, loser, finalState));
    }

    @Override
    public void beginWhitePly(String label, STATE state) {
        handlers.forEach(handler -> handler.beginWhitePly(label, state));
    }

    @Override
    public void endWhitePly(String label, STATE state) {
        handlers.forEach(handler -> handler.endWhitePly(label, state));
    }

    @Override
    public void beginRedPly(String label, STATE state) {
        handlers.forEach(handler -> handler.beginRedPly(label, state));
    }

    @Override
    public void endRedPly(String label, STATE state) {
        handlers.forEach(handler -> handler.endRedPly(label, state));
    }

    @Override
    public void startEvaluation(String label, STATE current, Supplier<Stream<STATE>> candidates) {
        handlers.forEach(handler -> handler.startEvaluation(label, current, candidates));
    }

    @Override
    public void redMoveEvaluated(STATE state, Eval e) {
        handlers.forEach(handler -> handler.redMoveEvaluated(state, e));
    }

    @Override
    public void whiteMoveEvaluated(STATE state, Eval e) {
        handlers.forEach(handler -> handler.whiteMoveEvaluated(state, e));
    }

    @Override
    public void endEvaluation(String label, STATE state, Eval e) {
        handlers.forEach(handler -> handler.endEvaluation(label, state, e));
    }

    /**
     * ビルダークラス
     *
     * @param <STATE> 局面を表すクラス
     */
    public static class BundledGameContextHandlerBuilder<STATE> {

    }

}

