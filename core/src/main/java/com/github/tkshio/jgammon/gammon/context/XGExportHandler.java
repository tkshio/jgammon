package com.github.tkshio.jgammon.gammon.context;

import com.github.tkshio.jgammon.common.context.GameContextHandler;
import com.github.tkshio.jgammon.common.director.GameInfo;
import com.github.tkshio.jgammon.common.director.Player;
import com.github.tkshio.jgammon.gammon.BackgammonState;
import com.github.tkshio.jgammon.gammon.move.CheckerPlay;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;


public class XGExportHandler implements GameContextHandler<BackgammonState> {
    private final Consumer<String> out;
    private final AtomicInteger no = new AtomicInteger(0);
    private String redMove = "";

    XGExportHandler(Consumer<String> out) {
        this.out = out;
    }

    public static XGExportHandler create(Consumer<String> out) {
        return new XGExportHandler(out);
    }

    @Override
    public void startTrials(int fromInclusive, int n) {
        printHeader();
    }

    @Override
    public void startGame(GameInfo info, BackgammonState initialState) {
        printGameHeader(info);
        no.set(0);
        redMove = "";
    }

    void println() {
        println("");
    }

    void println(String s) {
        out.accept(s);
    }

    @Override
    public void endRedPly(String label, BackgammonState state) {
        this.redMove = formatCheckerPlay(state.getLastCheckerPlay());
    }

    private String formatCheckerPlay(CheckerPlay checkerPlay) {
        String s = checkerPlay.formatInDesc();
        return checkerPlay.getMoves().length == 0 ? s + "Cannot Move" : s;
    }

    @Override
    public void endWhitePly(String label, BackgammonState state) {
        printMovesOfTurn(redMove,
                formatCheckerPlay(state.getLastCheckerPlay()));
    }

    private void printMovesOfTurn(String redMove, String whiteMove) {
        println(String.format("%3d) %-34s%s", no.addAndGet(1), redMove, whiteMove));
    }

    @Override
    public void whiteWin(GameInfo info, Player<BackgammonState> winner, Player<BackgammonState> loser, BackgammonState finalState) {
        var result = finalState.getResult();
        println(String.format("%40sWins %d point", "", result.getPoint()));
        println();
        println();
    }

    @Override
    public void redWin(GameInfo info, Player<BackgammonState> winner, Player<BackgammonState> loser, BackgammonState finalState) {
        var result = finalState.getResult();
        println(String.format("%3d) %-34s", no.addAndGet(1), redMove));
        println(String.format("      Wins %d point", result.getPoint()));
        println();
        println();
    }

    @Override
    public void abortGame(BackgammonState state) {
        println(" Wins 0 point");
        println();
        println();
    }

    private void printGameHeader(GameInfo info) {
        println(String.format(" Game %d", info.getGameNo()));
        println(String.format(" %-38s%s",
                String.format("%s : %d", info.getPlayers().red(), info.getPoints().red()),
                String.format("%s : %d", info.getPlayers().white(), info.getPoints().white())));

    }

    private void printHeader() {
        println("; [Jacoby \"Off\"]");
        println("; [Beaver \"On\"]");
        println("; [CubeLimit \"1\"]");
        println();
        println("0 point match");
        println();
    }
}
