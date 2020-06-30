package com.github.tkshio.jgammon.common.dice;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Supplier;

public class PresetDice implements Dice {
    private final int size;
    private Supplier<Integer> dice;


    private PresetDice(Dice fallbackDice, Integer... rolls) {
        Iterator<Integer> it = Arrays.asList(rolls).iterator();
        dice = () -> {
            if (it.hasNext()) {
                return it.next();
            }
            this.dice = fallbackDice::roll;
            return fallbackDice.roll();
        };

        size = fallbackDice.size();
    }

    public static PresetDice create(Dice fallbackDice, Integer... rolls) {
        return new PresetDice(fallbackDice, rolls);
    }

    @Override
    public int roll() {
        return dice.get();
    }

    @Override
    public int size() {
        return size;
    }
}
