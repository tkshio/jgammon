package com.github.tkshio.jgammon.gammon.utils;

import com.github.tkshio.jgammon.gammon.move.CheckerPlay;
import com.github.tkshio.jgammon.gammon.move.Move;
import com.github.tkshio.jgammon.gammon.move.Roll;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CheckerPlayStringParserTest {
    @Test
    public void parse_valid_strings() {

        {
            CheckerPlay mv = SimpleNotation.parse("42: 1/2 1/5");
            Move[] moves = mv.getMoves();
            assertEquals(2, moves.length);
            assertEquals(1, moves[0].getFrom());
            assertEquals(2, moves[0].getTo());
            assertEquals(1, moves[1].getFrom());
            assertEquals(5, moves[1].getTo());
            Roll roll = mv.getRoll();
            assertEquals(4, roll.getHigherNumber());
            assertEquals(2, roll.getLowerNumber());
        }
        {
            CheckerPlay mv = SimpleNotation.parse("12: 1/2 2/4");
            Move[] moves = mv.getMoves();
            assertEquals(2, moves.length);
            assertEquals(1, moves[0].getFrom());
            assertEquals(2, moves[0].getTo());
            assertEquals(2, moves[1].getFrom());
            assertEquals(4, moves[1].getTo());
            Roll roll = mv.getRoll();
            assertEquals(2, roll.getHigherNumber());
            assertEquals(1, roll.getLowerNumber());
        }
        {
            CheckerPlay mv = SimpleNotation.parse("12: 24/23 23/21");
            Move[] moves = mv.getMoves();
            assertEquals(2, moves.length);
            assertEquals(1, moves[0].getFrom());
            assertEquals(2, moves[0].getTo());
            assertEquals(2, moves[1].getFrom());
            assertEquals(4, moves[1].getTo());
            Roll roll = mv.getRoll();
            assertEquals(2, roll.getHigherNumber());
            assertEquals(1, roll.getLowerNumber());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_invalid_strings() {

        CheckerPlay mv = SimpleNotation.parse("1-");
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_empty_strings() {

        CheckerPlay mv = SimpleNotation.parse("");
        fail();
    }
}
