package com.github.tkshio.jgammon.gammon.director;

import com.github.tkshio.jgammon.common.context.GameContextHandler;
import com.github.tkshio.jgammon.common.dice.PresetDice;
import com.github.tkshio.jgammon.common.dice.RandomDice;
import com.github.tkshio.jgammon.common.director.GameInfo;
import com.github.tkshio.jgammon.common.director.Player;
import com.github.tkshio.jgammon.gammon.*;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestSelfPlay {
    @Test
    public void testWhiteWin() {
        int[] init = {
                0,
                -5, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 15,
                0
        };
        BackgammonBoard _board = BackgammonBoard.create(BGType.standard, init);

        GameContextHandler<BackgammonState> logger = new GameContextHandler<>() {
            int turn = 0;

            @Override
            public void startGame(GameInfo info, BackgammonState initialState) {

                assertEquals(0, turn);
                int[] current = TestUtils.toIntArray(initialState.getAbsoluteBoard());
                assertArrayEquals(init, current);
            }

            @Override
            public void beginWhitePly(String label, BackgammonState state) {
                int[] current = TestUtils.toIntArray(state.getBoard());
                int[][] answers = new int[][]{
                        {
                                0,
                                -15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5,
                                0
                        },
                        {
                                0,
                                -13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3,
                                0
                        },
                        {
                                0,
                                -11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                                0
                        }
                };
                assertArrayEquals(answers[turn], current);
            }

            @Override
            public void endWhitePly(String label, BackgammonState state) {
                int[] current = TestUtils.toIntArray(state.getBoard());
                int[][] answers = {
                        {
                                0,
                                -3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15,
                                0
                        },
                        {
                                0,
                                -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13,
                                0
                        },
                        {
                                0,
                                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 11,
                                0
                        }
                };
                assertArrayEquals("TURN:" + turn, answers[turn], current);
            }

            @Override
            public void whiteWin(GameInfo info, Player<BackgammonState> winner, Player<BackgammonState> loser, BackgammonState state) {
                Assert.assertEquals(BackgammonResult.Single, state.getResult());
            }

            @Override
            public void beginRedPly(String label, BackgammonState state) {
                int[] current = TestUtils.toIntArray(state.getBoard());
                int[][] answers = {{
                        0,
                        -3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15,
                        0
                }, {
                        0,
                        -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13,
                        0
                }, {
                        0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 11,
                        0
                }
                };

                assertArrayEquals("TURN:" + turn, answers[turn], current);
            }

            @Override
            public void endRedPly(String label, BackgammonState state) {
                int[] current = TestUtils.toIntArray(state.getRevertedBoard());
                int[][] answers = {{
                        0,
                        -3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13,
                        0
                }, {
                        0,
                        -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 11,
                        0
                }};

                assertArrayEquals("TURN:" + turn, answers[turn], current);
                turn++;
            }

            @Override
            public void redWin(GameInfo info, Player<BackgammonState> winner, Player<BackgammonState> loser, BackgammonState state) {
                fail();
            }

        };

        var selfPlay = BackgammonAutoPlay.builder()
                .bgDice(BackgammonDice.create(
                        PresetDice.create(RandomDice.create(6), 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6)
                ))
                .contextHandler(logger)
                .initialBoard(() -> _board)
                .build();
        selfPlay.run(1);
    }

    @Test
    public void testRedWin() {
        int[] init = {
                0,
                -15, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 5,
                0
        };
        BackgammonBoard _board = BackgammonBoard.create(BGType.standard, init);
        GameContextHandler<BackgammonState> logger = new GameContextHandler<>() {
            int turn = 0;

            @Override
            public void startGame(GameInfo info, BackgammonState initialState) {
                assertEquals(0, turn);
                int[] current = TestUtils.toIntArray(initialState.getAbsoluteBoard());
                assertArrayEquals(init, current);
            }

            @Override
            public void beginWhitePly(String label, BackgammonState state) {
                int[] current = TestUtils.toIntArray(state.getRevertedBoard());
                int[][] answers = new int[][]{
                        {
                                0,
                                -15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5,
                                0
                        },
                        {
                                0,
                                -13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3,
                                0
                        },
                        {
                                0,
                                -11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                                0
                        }

                };
                assertArrayEquals(answers[turn], current);
            }

            @Override
            public void endWhitePly(String label, BackgammonState state) {
                int[] current = TestUtils.toIntArray(state.getBoard());
                int[][] answers = {
                        {
                                0,
                                -13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5,
                                0
                        },
                        {
                                0,
                                -11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3,
                                0
                        },
                        {
                                0,
                                -9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                                0
                        }
                };

                assertArrayEquals("TURN:" + turn, answers[turn], current);
            }

            @Override
            public void whiteWin(GameInfo info, Player<BackgammonState> winner, Player<BackgammonState> loser, BackgammonState state) {
                fail();
            }

            @Override
            public void beginRedPly(String label, BackgammonState state) {
                int[] current = TestUtils.toIntArray(state.getBoard());
                int[][] answers = {{
                        0,
                        -13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5,
                        0
                }, {
                        0,
                        -11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3,
                        0
                }, {
                        0,
                        -9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                        0
                }
                };

                assertArrayEquals("TURN:" + turn, answers[turn], current);
            }

            @Override
            public void endRedPly(String label, BackgammonState state) {
                int[] current = TestUtils.toIntArray(state.getRevertedBoard());
                int[][] answers = {{
                        0,
                        -13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3,
                        0
                }, {
                        0,
                        -11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                        0
                }, {
                        0,
                        -9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0
                }};

                assertArrayEquals("TURN:" + turn, answers[turn], current);
                turn++;
            }


            @Override
            public void redWin(GameInfo info, Player<BackgammonState> winner, Player<BackgammonState> loser, BackgammonState state) {
                assertEquals(BackgammonResult.Single, state.getResult());
            }

        };


        var selfPlay = BackgammonAutoPlay.builder()
                .bgDice(BackgammonDice.create(
                        PresetDice.create(RandomDice.create(6), 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6)
                ))
                .contextHandler(logger)
                .initialBoard(() -> _board)
                .build();

        selfPlay.run(1, 1);
    }
}
