package com.github.tkshio.jgammon.gammon.move;

import com.github.tkshio.jgammon.common.node.IndexedStateNode;

public class Roll implements Comparable<Roll>, IndexedStateNode.HasWeight {
    private final int higher;
    private final int lower;
    private final double weight;

    private Roll(int r1, int r2) {
        if (r1 > r2) {
            this.higher = r1;
            this.lower = r2;
        } else {
            this.higher = r2;
            this.lower = r1;
        }

        if (r1 == r2) {
            this.weight = 1.0;
        } else {
            this.weight = 2.0;
        }
    }

    public static Roll of(int r1, int r2) {
        return new Roll(r1, r2);
    }

    public static Roll of(int r1, int r2, String desc) {
        return new Roll(r1, r2) {
            @Override
            public String toString() {
                return "";
            }
        };
    }

    public static int compare(Roll roll1, Roll roll2) {
        if (roll1.getHigherNumber() == roll2.getHigherNumber()) {
            return Integer.compare(roll1.getLowerNumber(), roll2.getLowerNumber());
        }
        return Integer.compare(roll1.getHigherNumber(), roll2.getHigherNumber());
    }

    public int getHigherNumber() {
        return higher;
    }

    public int getLowerNumber() {
        return lower;
    }

    public boolean isDoublet() {
        return higher == lower;
    }

    public String toString() {
        return String.format("%d%d",
                getHigherNumber(),
                getLowerNumber());
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Roll) {
            return ((Roll) o).getHigherNumber() == getHigherNumber()
                    && ((Roll) o).getLowerNumber() == getLowerNumber();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getHigherNumber() * 6 + getLowerNumber();
    }

    @Override
    public int compareTo(Roll roll) {
        return compare(this, roll);
    }

}
