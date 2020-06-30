package com.github.tkshio.jgammon.gammon;

import com.github.tkshio.jgammon.gammon.move.BGMovesSupplier;
import com.github.tkshio.jgammon.gammon.move.CheckerPlay;
import com.github.tkshio.jgammon.gammon.move.Roll;
import com.github.tkshio.jgammon.gammon.utils.BackgammonBoardPrinter;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;


public class TestRollApplier {

    @Test
    public void basicUsage() {
        BackgammonBoard board = BackgammonBoard.create(BGType.standard);
        new BackgammonBoardPrinter().print(AbsoluteBackgammonBoard.absolute(board));
        CheckerPlay[] moves = BackgammonBoardRollApplier.create().listupMoves(board, Roll.of(2, 1))
                .collect(Collectors.toList())
                .toArray(CheckerPlay[]::new);

        String[] answer = {
                "21:24/23 13/11",
                "21:24/23 8/6",
                "21:24/23 6/4",
                "21:24/22 24/23",
                "21:24/22 22/21",

                "21:24/22 8/7",
                "21:24/22 6/5",
                "21:13/11 11/10",
                "21:13/11 8/7",
                "21:13/11 6/5",

                "21:8/7 6/4",
                "21:8/6 8/7",
                "21:8/6 6/5",
                "21:6/4 6/5",
                "21:6/4 4/3",
        };
        TestUtils.compareMoves(answer, moves);
    }

    @Test
    public void generateMovesWithFrom() {
        BackgammonBoard board = BackgammonBoard.create(BGType.standard);
        new BackgammonBoardPrinter().print(AbsoluteBackgammonBoard.absolute(board));
        Roll roll = Roll.of(1, 2);
        {
            CheckerPlay[] moves = BGMovesSupplier.listupMoves(board, 0, 2)
                    .map(move -> CheckerPlay.of(roll, move))
                    .collect(Collectors.toList())
                    .toArray(CheckerPlay[]::new);
            String[] answer = {
                    "12:24/22",
                    "12:13/11",
                    "12:8/6",
                    "12:6/4",
            };
            TestUtils.compareMoves(answer, moves);
        }

        // fromパラメーターで指定した場所移行の候補だけが列挙対象になる

        int[] count = new int[]{4, // unused
                4, 3, 3, 3, 3, 3,
                3, 3, 3, 3, 3, 3,
                2, 2, 2, 2, 2, 1,
                1, 0, 0, 0, 0, 0
        };
        for (int from = 1; from < 25; from++) {
            var moves = BGMovesSupplier.listupMoves(board, from, 2)
                    .map(move -> CheckerPlay.of(roll, move))
                    .collect(Collectors.toList())
                    .toArray(CheckerPlay[]::new);
            String[] answer = {
                    "12:24/22",
                    "12:13/11",
                    "12:8/6",
                    "12:6/4",
            };
            TestUtils.compareMoves(Arrays.copyOfRange(answer, answer.length - count[from], answer.length), moves);
        }
    }


}

