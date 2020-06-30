package com.github.tkshio.jgammon.gammon;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestIsEndOfGame {
    @Test
    public void isLost() {
        int[] init = {
                0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 1,
                0
        };
        {
            var state = buildState(init);

            assertTrue(state.getResult().isOver());
            assertEquals(BackgammonResult.Single, state.getResult());
        }
        {
            init[10] = 14;
            var state = buildState(init);
            assertTrue(state.getResult().isOver());
            assertEquals(BackgammonResult.Gammon, state.getResult());
        }
        {
            init[1] = 14;
            init[10] = 0;
            var state = buildState(init);
            assertTrue(state.getResult().isOver());
            assertEquals(BackgammonResult.Backgammon, state.getResult());
        }
        {
            init[1] = 14;
            init[10] = -1;
            var state = buildState(init);
            assertFalse(state.getResult().isOver());
            assertEquals(BackgammonResult.InGame, state.getResult());
        }
        {
            init[0] = 1;
            init[1] = 0;
            init[11] = 13;
            init[10] = 0;
            var state = buildState(init);
            assertTrue(state.getResult().isOver());
            assertEquals(BackgammonResult.Backgammon, state.getResult());
        }

    }

    private BackgammonState buildState(int[] init) {
        BackgammonBoard board = BackgammonBoard.create(BGType.standard, init);
        return BackgammonStateOperator.create().whiteGoesFirst(board);
    }
}
