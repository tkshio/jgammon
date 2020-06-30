package com.github.tkshio.jgammon.gammon;

public enum BGType {
    standard(15, 25, 19,
            new int[]{
                    0,
                    2, 0, 0, 0, 0, -5,
                    0, -3, 0, 0, 0, 5,
                    -5, 0, 0, 0, 3, 0,
                    5, 0, 0, 0, 0, -2,
                    0
            }

    ),
    half(10, 17, 13,
            new int[]{
                    0,
                    2, 0, 0, -3,
                    0, -2, 0, 3,
                    -3, 0, 2, 0,
                    3, 0, 0, -2,
                    0
            }),
    mini(8, 13, 10,
            new int[]{
                    0,
                    2, 0, -3,
                    0, 0, 3,
                    -3, 0, 0,
                    3, 0, -2,
                    0
            });
    private final int initialPieces;
    private final int bearOffPos;
    private final int innerPos;
    private final int[] initialArrangement;

    BGType(int initialPieces, int bearOffPos, int innerPos, int[] initialArrangement) {
        this.initialPieces = initialPieces;
        this.bearOffPos = bearOffPos;
        this.innerPos = innerPos;
        this.initialArrangement = initialArrangement;
    }

    public int getBearOffPos() {
        return bearOffPos;
    }

    public int getInitialPieces() {
        return initialPieces;
    }

    public int getInnerPos() {
        return innerPos;
    }

    public int[] initialArrangement() {
        return initialArrangement;
    }
}
