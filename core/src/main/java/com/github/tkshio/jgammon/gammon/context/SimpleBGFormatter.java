package com.github.tkshio.jgammon.gammon.context;

import com.github.tkshio.jgammon.common.director.GameInfo;
import com.github.tkshio.jgammon.common.director.Player;
import com.github.tkshio.jgammon.common.evaluator.Eval;
import com.github.tkshio.jgammon.gammon.AbsoluteBackgammonBoard;
import com.github.tkshio.jgammon.gammon.BackgammonState;
import com.github.tkshio.jgammon.gammon.move.CheckerPlay;
import com.github.tkshio.jgammon.gammon.utils.BackgammonBoardPrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

class SimpleBGFormatter {

    String[] startGame(GameInfo info, BackgammonState state) {
        return board(String.format("Game %d %s:%d - %s:%d",
                info.getGameNo(),
                info.getPlayers().red(), info.getPoints().red(),
                info.getPlayers().white(), info.getPoints().white()),
                state.getAbsoluteBoard()
        );
    }

    String abortGame(BackgammonState state) {
        return String.format("Game was aborted, over %d ply(s).", state.getPly());
    }

    String startTrials(int fromInclusive, int n) {
        return String.format("Start trial(s) %d-%d(%d)", fromInclusive, fromInclusive + n - 1, n);
    }

    String[] endTrials(long n,
                       String redLabel, int redWin,
                       String whiteLabel, int whiteWin,
                       String... msgs) {
        String[] ret = new String[msgs.length + 2];
        ret[0] = "";
        ret[1] = String.format("Trials done. %s:%d - %s:%d , %d game(s)",
                redLabel, redWin,
                whiteLabel, whiteWin,
                n);
        System.arraycopy(msgs, 0, ret, 2, msgs.length);
        return ret;
    }

    String[] ply(String label, BackgammonState state) {
        return new String[]{"-----------",
                String.format("Ply #%d %s:", state.getPly() + 1, label)};
    }

    public String[] formatMoveAndBoardRed(String label, BackgammonState state) {
        String move = move(state, CheckerPlay::formatInDesc);

        return board(move, state.getAbsoluteBoard());
    }

    public String[] formatMoveAndBoardWhite(String label, BackgammonState state) {
        String move = move(state, CheckerPlay::formatInAsc);

        return board(move, state.getAbsoluteBoard());
    }


    String endOfGame(Player<BackgammonState> winner,
                     Player<BackgammonState> loser,
                     BackgammonState state,
                     GameInfo info) {
        int no = info.getGameNo();
        int whitePoint = info.getPoints().white();
        int redPoint = info.getPoints().red();
        String result = state.getResult().name();
        return String.format("#%-3d %10s won by %-10s. %3d - %-3d (%4d plys) ",
                no, winner.getName(), result, whitePoint, redPoint, state.getPly());
    }

    String move(BackgammonState state, Function<CheckerPlay, String> formatter) {
        return String.format("move: %-17s",
                formatter.apply(state.getLastCheckerPlay()));
    }

    private String[] board(String move, AbsoluteBackgammonBoard board) {

        final List<String> buffer = new ArrayList<>();
        buffer.add(move);
        BackgammonBoardPrinter.println(buffer::add, "");
        BackgammonBoardPrinter.print(buffer::add, board);
        BackgammonBoardPrinter.println(buffer::add, "");
        return buffer.toArray(String[]::new);
    }


    String[] startEvaluation(String label, BackgammonState current, Supplier<Stream<BackgammonState>> options) {
        return new String[]{String.format("%s thinking ...", label), ""};
    }

    String evaluateMove(BackgammonState state, Eval e, Function<CheckerPlay, String> formatter) {
        var checkerPlay = state.getLastCheckerPlay();
        return String.format("%-17s (%.3f) %s", formatter.apply(checkerPlay),
                e.getScore(),
                e.asString());
    }

    String endEvaluation(String label, BackgammonState state, Eval e) {
        return "";
    }

}
