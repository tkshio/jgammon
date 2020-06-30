package com.github.tkshio.jgammon.gammon;

import com.github.tkshio.jgammon.gammon.move.BGBoard;
import com.github.tkshio.jgammon.gammon.move.CheckerPlay;
import com.github.tkshio.jgammon.gammon.move.Move;
import com.github.tkshio.jgammon.gammon.move.Roll;
import org.junit.Test;

import java.util.stream.IntStream;

import static com.github.tkshio.jgammon.gammon.move.BGMovesSupplier.listupMovablePoints;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TestBasicGammonBoardBehaviours {
    BackgammonStateOperator applier = BackgammonStateOperator.create();

    @Test
    public void initAndBasicMoves() {
        BackgammonBoard board = BackgammonBoard.create(BGType.standard);
        BackgammonState status = applier.whiteGoesFirst(board);
        // 初期盤面
        verifyInitialArrangement(board);

        // ムーブ可能な場所
        verifyInitialMovablePoints(listupMovablePoints(board, 0));

        // 1-3 ムーブの適用
        CheckerPlay checkerPlay1 = CheckerPlay.of(
                Roll.of(1, 2),
                Move.of(19, 20),
                Move.of(12, 14)
        );
        BackgammonState status1 = applier.withCheckerPlay(status, checkerPlay1);
        assertArrangement(status1.getRevertedBoard(), new int[]{
                0,
                2, 0, 0, 0, 0, -5, /*bar*/ 0, -3, 0, 0, 0, 4,
                -5, 1, 0, 0, 3, 0, /*bar*/ 4, 1, 0, 0, 0, -2,
                0
        });

        // 相手側のムーブとヒット
        CheckerPlay checkerPlay2 = CheckerPlay.of(
                Roll.of(4, 1),
                Move.of(1, 5, true),
                Move.of(5, 11, true)
        );


        BackgammonState board2;
        {
            board2 = applier.withCheckerPlay(status1, checkerPlay2);
        }
        assertArrangement(board2.getBoard(), new int[]{
                2,
                2, 0, 0, 0, 0, -5, /*bar*/ 0, -3, 0, 0, 0, 4,
                -5, -1, 0, 0, 3, 0,/*bar*/ 4, 0, 0, 0, 0, -1,
                0
        });

        // ゾロ目とヒット
        var move3 = CheckerPlay.of(
                Roll.of(5, 5),
                Move.of(0, 5),
                Move.of(0, 5),
                Move.of(19, 24, true),
                Move.of(12, 17)
        );
        var board3 = applier.withCheckerPlay(board2, move3);
        assertArrangement(board3.getRevertedBoard(), new int[]{
                0,
                2, 0, 0, 0, 2, -5, /*bar*/ 0, -3, 0, 0, 0, 3,
                -5, -1, 0, 0, 4, 0,/*bar*/ 3, 0, 0, 0, 0, 1,
                -1
        });

        // リターンヒット
        var move4 = CheckerPlay.of(
                Roll.of(1, 6),
                Move.of(0, 1, true),
                Move.of(11, 17)
        );
        var board4 = applier.withCheckerPlay(board3, move4);
        assertArrangement(board4.getBoard(), new int[]{
                1,
                2, 0, 0, 0, 2, -5, /*bar*/ 0, -4, 0, 0, 0, 3,
                -5, 0, 0, 0, 4, 0, /*bar*/ 3, 0, 0, 0, 0, -1,
                0
        });
    }

    private void verifyInitialMovablePoints(IntStream points) {
        int[] expected = new int[]{1, 12, 17, 19};
        assertArrayEquals(expected, points.toArray());
    }

    private void verifyInitialArrangement(BGBoard board) {
        int[] pos = {
                0,
                2, 0, 0, 0, 0, -5, /*bar*/ 0, -3, 0, 0, 0, 5,
                -5, 0, 0, 0, 3, 0, /*bar*/ 5, 0, 0, 0, 0, -2,
                0
        };
        assertArrangement(board, pos);
        //   assertFalse(board.isEndOfGame().isPresent());

    }

    private void assertArrangement(BGBoard board, int[] pos) {
        for (int i = 0; i < 25; i++) {
            assertEquals("" + i, pos[i], board.getPointAt(i));
        }
    }
}
