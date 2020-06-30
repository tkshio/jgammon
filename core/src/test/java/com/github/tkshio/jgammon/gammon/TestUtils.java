package com.github.tkshio.jgammon.gammon;

import com.github.tkshio.jgammon.gammon.move.CheckerPlay;
import com.github.tkshio.jgammon.gammon.move.Move;
import com.github.tkshio.jgammon.gammon.utils.SimpleNotation;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public final class TestUtils {

    public static void compareMoves(String[] expected, CheckerPlay[] answer) {
        Arrays.sort(answer, TestUtils::compare);
        Arrays.stream(answer).forEach(System.out::println);

        Move[][] moves = new Move[expected.length][];
        for (int i = 0; i < expected.length; i++) {
            CheckerPlay p = SimpleNotation.parse(expected[i]);
            moves[i] = p.getMoves();
        }
        compareMoves(answer, moves);
    }

    static void compareMoves(CheckerPlay[] answer, Move[][] expected) {
        Arrays.sort(answer, TestUtils::compare);
        assertEquals(expected.length, answer.length);
        for (int i = 0; i < answer.length; i++) {
            assertEquals(expected[i].length, answer[i].getMoves().length);
            for (int j = 0; j < expected[i].length; j++) {
                assertEquals("" + i, expected[i][j].getFrom(), answer[i].getMoves()[j].getFrom());
                assertEquals("" + i, expected[i][j].getTo(), answer[i].getMoves()[j].getTo());
                assertEquals("" + i, expected[i][j].isHit(), answer[i].getMoves()[j].isHit());
            }
        }
    }

    public static int compare(CheckerPlay p1, CheckerPlay p2) {
        Move[] moves1 = p1.getMoves();
        Move[] moves2 = p2.getMoves();
        return Arrays.compare(moves1, moves2,
                (Move m1, Move m2) -> {
                    if (m1.getFrom() == m2.getFrom()) {
                        if (m1.getTo() == m2.getTo()) {
                            if (m1.isHit()) {
                                return 1;
                            } else if (m2.isHit()) {
                                return -1;
                            }
                            return 0;
                        }
                        return Integer.compare(m1.getTo(), m2.getTo());
                    }
                    return Integer.compare(m1.getFrom(), m2.getFrom());
                }
        );
    }

    public static int[] toIntArray(BackgammonBoard board) {
        int[] result = new int[board.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = board.getPointAt(i);
        }
        return result;
    }
}
