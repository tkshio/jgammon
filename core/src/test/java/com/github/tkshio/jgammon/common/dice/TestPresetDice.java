package com.github.tkshio.jgammon.common.dice;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.function.BiConsumer;

import static org.junit.Assert.assertArrayEquals;

public class TestPresetDice {


    @Test
    public void returnsSpecifiedValues() {
        BiConsumer<Integer, Long> tester = (size, seed) -> {
            Integer[] expected;
            Integer[] presetValues;
            {
                Random r = new Random();
                presetValues = new Integer[r.nextInt(100) + 10];

                expected = new Integer[presetValues.length + r.nextInt(20)];
                Arrays.setAll(presetValues, i -> r.nextInt(size) + 1);
            }
            Dice dice = PresetDice.create(RandomDice.create(size, seed), presetValues);

            Random r = new Random(new Random(seed).nextLong());
            Arrays.setAll(expected, i ->
                    (i < presetValues.length) ?
                            presetValues[i]
                            : r.nextInt(size) + 1);

            var answer = new Integer[expected.length];
            Arrays.setAll(answer, i -> dice.roll());
            assertArrayEquals(expected, answer);
        };

        Random r = new Random(1L);
        int sz = 6;

        for (int i = 0; i < 100; i++) {
            tester.accept(sz, r.nextLong());
        }
    }
}
