package com.github.tkshio.jgammon.common.dice;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TestRandomDice {
    @Test
    public void testRandomValuesAndReset() {
        long seed = 1;
        Dice dice = RandomDice.create(10, seed);
        Random r = new Random(seed);
        Random rr = new Random(r.nextLong());

        int[] answer = new int[100];
        Arrays.setAll(answer, i -> {
            int v = dice.roll();
            if (v % 7 == 0) {
                dice.reset();
            }
            return v;
        });

        int[] expected = new int[100];
        AtomicInteger co = new AtomicInteger(0);
        Arrays.setAll(expected, i -> {
            int v = rr.nextInt(10) + 1;
            if (v % 7 == 0) {
                rr.setSeed(r.nextLong());
                co.incrementAndGet();
            }
            return v;
        });

        assertArrayEquals(expected, answer);
        assertEquals(co.get(), dice.resetCount());
    }
}
