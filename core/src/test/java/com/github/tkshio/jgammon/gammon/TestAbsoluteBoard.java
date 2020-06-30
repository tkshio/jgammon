package com.github.tkshio.jgammon.gammon;

import com.github.tkshio.jgammon.gammon.move.CheckerPlay;
import org.junit.Test;

import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;

public class TestAbsoluteBoard {
    @Test
    public void absoluteBoardPos() {
        // Boardから生成したAbsoluteBoard、およびそれを繰り返し
        // 反転させてみての確認
        var stateOp = BackgammonStateOperator.create();
        int[] arr = new int[]{
                1,
                2, 4, 6, 8, 0, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                -3, -5, -7, 0, 0, 0,
                -9
        };

        var board = BackgammonBoard.create(BGType.standard, arr);
        var stateWhite = stateOp.whiteGoesFirst(board);
        testState(stateOp, stateWhite,
                (aBoard) -> {
                    for (int i = 0; i < arr.length; i++) {
                        assertEquals(arr[i], aBoard.getPointAt(i));
                    }
                }
        );

        var stateRed = stateOp.redGoesFirst(board);
        testState(stateOp, stateRed,
                (aBoard) -> {
                    for (int i = 0; i < arr.length; i++) {
                        assertEquals(arr[i], -aBoard.getPointAt(25 - i));
                    }
                }
        );
    }

    private void testState(BackgammonStateOperator stateOp,
                           BackgammonState state,
                           Consumer<AbsoluteBackgammonBoard> assertAsAbsoluteBoard) {
        {
            var aBoard = state.getAbsoluteBoard();
            assertAsAbsoluteBoard.accept(aBoard);
        }
        {
            state = stateOp.withCheckerPlay(state, CheckerPlay.EMPTY);
            var aBoard = state.getAbsoluteBoard();
            assertAsAbsoluteBoard.accept(aBoard);
        }
        {
            state = stateOp.withCheckerPlay(state, CheckerPlay.EMPTY);
            var aBoard = state.getAbsoluteBoard();
            assertAsAbsoluteBoard.accept(aBoard);
        }
        {
            state = stateOp.withCheckerPlay(state, CheckerPlay.EMPTY);
            var aBoard = state.getAbsoluteBoard();
            assertAsAbsoluteBoard.accept(aBoard);
        }
    }
}
