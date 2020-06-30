package com.github.tkshio.jgammon.gammon;

import com.github.tkshio.jgammon.gammon.move.CheckerPlay;
import com.github.tkshio.jgammon.gammon.move.Roll;
import org.junit.Test;


public class TestRollApplierForDoublet {


    @Test
    public void doublet() {
        BackgammonBoard board = BackgammonBoard.create(BGType.standard,
                new int[]{
                        0,
                        0, 1, 0, -2, 0, 0, // can't move
                        0, 2, 0, -1, 0, 0, // 17->15*->13->11->9
                        0, 0, 2, 0, 0, 0,  // 10->8->6->4->2
                        0, 0, 0, 0, 0, 0,
                        0
                }
        );

        var moves = BackgammonBoardRollApplier.create().listupMoves(board, Roll.of(2, 2))
                .sorted(TestUtils::compare)
                .toArray(CheckerPlay[]::new);

        String[] answer = {
                "22:17/15* 17/15 15/13 15/13",
                "22:17/15* 17/15 15/13 13/11",
                "22:17/15* 17/15 15/13 10/8",
                "22:17/15* 17/15 10/8 10/8",
                "22:17/15* 17/15 10/8 8/6",
                "22:17/15* 15/13 13/11 11/9",
                "22:17/15* 15/13 13/11 10/8",
                "22:17/15* 15/13 10/8 10/8",
                "22:17/15* 15/13 10/8 8/6",
                "22:17/15* 10/8 10/8 8/6",
                "22:17/15* 10/8 8/6 6/4",
                "22:10/8 10/8 8/6 8/6",
                "22:10/8 10/8 8/6 6/4",
                "22:10/8 8/6 6/4 4/2"
        };

        TestUtils.compareMoves(answer, moves);

    }

    @Test
    public void doubletBlocked() {
        BackgammonBoard board = BackgammonBoard.create(BGType.standard,
                new int[]{
                        0,
                        3, 0, 0, 0, 0, 0,
                        -2, 0, 0, 0, 0, 0,
                        3, 0, 0, 0, 0, 0,
                        -2, 0, 0, 0, 0, 0,
                        0
                }
        );

        var moves = BackgammonBoardRollApplier.create().listupMoves(board, Roll.of(3, 3))
                .sorted(TestUtils::compare)
                .toArray(CheckerPlay[]::new);
        ;


        String[] answer = {
                "33:24/21 24/21 24/21 12/9",
                "33:24/21 24/21 12/9 12/9",
                "33:24/21 12/9 12/9 12/9",
        };

        TestUtils.compareMoves(answer, moves);
    }

    @Test
    public void doublet4OnTheBar() {
        BackgammonBoard board = BackgammonBoard.create(BGType.standard,
                new int[]{
                        4,
                        0, 0, 0, 0, 0, 0,
                        -2, 0, 0, 0, 0, 0,
                        3, 0, 0, 0, 0, 0,
                        -2, 0, 0, 0, 0, 0,
                        0
                }
        );

        var moves = BackgammonBoardRollApplier.create().listupMoves(board, Roll.of(3, 3))
                .sorted(TestUtils::compare)
                .toArray(CheckerPlay[]::new);
        ;


        String[] answer = {
                "33:BAR/22 BAR/22 BAR/22 BAR/22"
        };

        TestUtils.compareMoves(answer, moves);
    }

    @Test
    public void doublet3OnTheBar() {
        BackgammonBoard board = BackgammonBoard.create(BGType.standard,
                new int[]{
                        3,
                        0, 0, 0, 0, 0, 0,
                        -2, 0, 0, 0, 0, 0,
                        3, 0, 0, 0, 0, 0,
                        -2, 0, 0, 0, 0, 0,
                        0
                }
        );

        var moves = BackgammonBoardRollApplier.create().listupMoves(board, Roll.of(3, 3))
                .sorted(TestUtils::compare)
                .toArray(CheckerPlay[]::new);
        ;


        String[] answer = {
                "33:BAR/22 BAR/22 BAR/22 22/19",
                "33:BAR/22 BAR/22 BAR/22 12/9"
        };

        TestUtils.compareMoves(answer, moves);
    }

    @Test
    public void doublet2OnTheBar() {
        BackgammonBoard board = BackgammonBoard.create(BGType.standard,
                new int[]{
                        2,
                        0, 0, 0, 0, 0, 0,
                        -2, 0, 0, 0, 0, 0,
                        3, 0, 0, 0, 0, 0,
                        -2, 0, 0, 0, 0, 0,
                        0
                }
        );

        var moves = BackgammonBoardRollApplier.create().listupMoves(board, Roll.of(3, 3))
                .sorted(TestUtils::compare)
                .toArray(CheckerPlay[]::new);
        ;


        String[] answer = {
                "33:BAR/22 BAR/22 22/19 22/19",
                "33:BAR/22 BAR/22 22/19 19/16",
                "33:BAR/22 BAR/22 22/19 12/9",
                "33:BAR/22 BAR/22 12/9 12/9",
        };

        TestUtils.compareMoves(answer, moves);
    }

    @Test
    public void doublet1OnTheBar() {
        BackgammonBoard board = BackgammonBoard.create(BGType.standard,
                new int[]{
                        1,
                        0, 0, 0, 0, 0, 0,
                        -2, 0, 0, 0, 0, 0,
                        3, 0, 0, 0, 0, 0,
                        -2, 0, 0, 0, 0, 0,
                        0
                }
        );

        var moves = BackgammonBoardRollApplier.create().listupMoves(board, Roll.of(3, 3))
                .sorted(TestUtils::compare)
                .toArray(CheckerPlay[]::new);
        ;


        String[] answer = {
                "33:BAR/22 22/19 19/16 16/13",
                "33:BAR/22 22/19 19/16 12/9",
                "33:BAR/22 22/19 12/9 12/9",
                "33:BAR/22 12/9 12/9 12/9",
        };

        TestUtils.compareMoves(answer, moves);
    }

    @Test
    public void doubletBearOff() {
        BackgammonBoard board = BackgammonBoard.create(BGType.standard,
                new int[]{
                        0,
                        0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0,
                        0, 0, 3, 3, 0, 0,
                        0
                }
        );

        var moves = BackgammonBoardRollApplier.create().listupMoves(board, Roll.of(3, 3))
                .sorted(TestUtils::compare)
                .toArray(CheckerPlay[]::new);
        ;


        String[] answer = {
                "33:21/24 21/24 21/24 22/OFF",
                "33:21/24 21/24 22/OFF 22/OFF",
                "33:21/24 22/OFF 22/OFF 22/OFF",
        };

        TestUtils.compareMoves(answer, moves);
    }

    @Test
    public void doubletCantUseAll() {
        BackgammonBoard board = BackgammonBoard.create(BGType.standard,
                new int[]{
                        0,
                        0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0,
                        0, 3, 0, 0, 0, 0,
                        0, 0, 0, -2, 0, 0,
                        0
                }
        );

        var moves = BackgammonBoardRollApplier.create().listupMoves(board, Roll.of(4, 4))
                .sorted(TestUtils::compare)
                .toArray(CheckerPlay[]::new);
        ;


        String[] answer = {
                "44:11/7 11/7 11/7"
        };

        TestUtils.compareMoves(answer, moves);
    }

    @Test
    public void doubletCantUseAllInBearOff() {
        BackgammonBoard board = BackgammonBoard.create(BGType.standard,
                new int[]{
                        0,
                        0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0,
                        0, 1, 1, 1, 0, -2,
                        0
                }
        );

        var moves = BackgammonBoardRollApplier.create().listupMoves(board, Roll.of(3, 3))
                .sorted(TestUtils::compare)
                .toArray(CheckerPlay[]::new);
        ;


        String[] answer = {
                "33:5/2 3/OFF"

        };

        TestUtils.compareMoves(answer, moves);
    }
}

