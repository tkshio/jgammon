package com.github.tkshio.jgammon.common.context;


import com.github.tkshio.jgammon.common.director.GameInfo;
import com.github.tkshio.jgammon.common.director.Player;
import com.github.tkshio.jgammon.common.evaluator.Eval;
import com.github.tkshio.jgammon.common.utils.RWTuple;

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * ゲームの進行の各段階を定義したインターフェース
 *
 * <p>進行はRed/Whiteの観点で定義されているが、Directorによる処理の内部では、TebanContextHandlerにより先手・後手にマッピングされる。
 *
 * @param <STATE> 局面を表すクラス
 */
public interface GameContextHandler<STATE> extends RWTuple<GameContextHandler.TebanContextHandler<STATE>> {
    default void startTrials(int fromInclusive, int n) {
    }

    default void endTrials(long total,
                           String redLabel, int redPoints,
                           String whiteLabel, int whitePoints,
                           String... msgs) {
    }

    default void abortGame(STATE state) {
    }

    default void startGame(GameInfo info, STATE initialState) {
    }

    default void whiteWin(GameInfo info, Player<STATE> winner, Player<STATE> loser, STATE finalState) {
    }

    default void redWin(GameInfo info, Player<STATE> winner, Player<STATE> loser, STATE finalState) {
    }

    default void beginWhitePly(String label, STATE state) {
    }

    default void endWhitePly(String label, STATE state) {
    }

    default void beginRedPly(String label, STATE state) {
    }

    default void endRedPly(String label, STATE state) {
    }

    default void startEvaluation(String label, STATE current, Supplier<Stream<STATE>> candidates) {
    }

    default void redMoveEvaluated(STATE state, Eval e) {
    }

    default void whiteMoveEvaluated(STATE state, Eval e) {
    }

    default void endEvaluation(String label, STATE state, Eval e) {
    }

    default TebanContextHandler<STATE> red() {
        return new TebanContextHandler<>() {
            @Override
            public void beginPly(String label, STATE state) {
                beginRedPly(label, state);
            }

            @Override
            public void endPly(String label, STATE state) {
                endRedPly(label, state);
            }

            @Override
            public void won(GameInfo info, Player<STATE> winner, Player<STATE> loser, STATE state) {
                GameContextHandler.this.redWin(info, winner, loser, state);
            }

            @Override
            public void startEvaluation(String label,
                                        STATE current,
                                        Supplier<Stream<STATE>> candidates) {
                GameContextHandler.this.startEvaluation(
                        label, current, candidates);
            }

            @Override
            public void evaluateMove(STATE state, Eval e) {
                GameContextHandler.this.redMoveEvaluated(state, e);
            }

            @Override
            public void endEvaluation(String label, STATE state, Eval e) {
                GameContextHandler.this.endEvaluation(label, state, e);
            }
        };
    }

    default TebanContextHandler<STATE> white() {
        return new TebanContextHandler<>() {
            @Override
            public void beginPly(String label, STATE state) {
                beginWhitePly(label, state);
            }

            @Override
            public void endPly(String label, STATE state) {
                endWhitePly(label, state);
            }

            @Override
            public void won(GameInfo info, Player<STATE> winner, Player<STATE> loser, STATE state) {
                GameContextHandler.this.whiteWin(info, winner, loser, state);
            }

            @Override
            public void startEvaluation(String label,
                                        STATE current,
                                        Supplier<Stream<STATE>> candidates) {
                GameContextHandler.this.startEvaluation(
                        label, current, candidates);
            }

            @Override
            public void evaluateMove(STATE state, Eval e) {
                GameContextHandler.this.whiteMoveEvaluated(state, e);
            }

            @Override
            public void endEvaluation(String label, STATE state, Eval e) {
                GameContextHandler.this.endEvaluation(label, state, e);
            }
        };
    }

    /**
     * 先手・後手の関係に読み替えられたインターフェース
     *
     * @param <STATE> 局面を表すクラス
     */
    interface TebanContextHandler<STATE> {
        default void beginPly(String label, STATE state) {
        }

        default void endPly(String label, STATE state) {
        }

        default void won(GameInfo info, Player<STATE> winner, Player<STATE> loser, STATE state) {
        }

        default void startEvaluation(String label, STATE current, Supplier<Stream<STATE>> candidates) {
        }

        default void evaluateMove(STATE state, Eval e) {
        }

        default void endEvaluation(String label, STATE state, Eval e) {
        }

    }
}
