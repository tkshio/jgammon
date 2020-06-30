package com.github.tkshio.jgammon.gammon.utils;

import com.github.tkshio.jgammon.gammon.BGType;
import com.github.tkshio.jgammon.gammon.BackgammonBoard;
import com.github.tkshio.jgammon.gammon.BackgammonState;
import com.github.tkshio.jgammon.gammon.BackgammonStateOperator;
import org.junit.Test;

public class TestPrinter {

    @Test
    public void runPrinterWithStandardBoard() {
        BackgammonBoard board = BackgammonBoard.create(BGType.mini);
        BackgammonState state = BackgammonStateOperator.create().redGoesFirst(board);
        BackgammonBoardPrinter.print(System.out::println, state.getAbsoluteBoard());
        // 例外が出なければ良しとする
    }

    @Test
    public void runPrinterWithMiniBoard() {
        BackgammonBoard board = BackgammonBoard.create(BGType.half);
        BackgammonState state = BackgammonStateOperator.create().redGoesFirst(board);
        BackgammonBoardPrinter.print(System.out::println, state.getAbsoluteBoard());
        // 例外が出なければ良しとする
    }

    @Test
    public void runPrinterWithBoard() {
        BackgammonBoard board = BackgammonBoard.create(BGType.standard);
        BackgammonState state = BackgammonStateOperator.create().redGoesFirst(board);
        BackgammonBoardPrinter.print(System.out::println, state.getAbsoluteBoard());
        // 例外が出なければ良しとする
    }
}
