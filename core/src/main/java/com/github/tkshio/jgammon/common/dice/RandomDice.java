package com.github.tkshio.jgammon.common.dice;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link java.util.Random}を使用した{@link Dice}の実装
 */
public class RandomDice implements Dice {
    private final int size;
    private final Random seedGen;
    private final Random r;
    private final AtomicInteger counter = new AtomicInteger(0);

    private RandomDice(int size, Random seedGen) {
        this.seedGen = seedGen;
        this.r = new Random(seedGen.nextLong());
        this.size = size;
    }

    /**
     * ファクトリーメソッド
     *
     * <p>シード値をランダムに決定して {@link #create(int, long)}を呼ぶ
     *
     * @param size 最大の目（最小の目は常に１）
     * @return 構築されたダイス
     */
    public static Dice create(int size) {
        return new RandomDice(size, new Random());
    }

    /**
     * ファクトリーメソッド
     *
     * <p>シード値は直接使用するのではなく、シード値生成用のRandomオブジェクトの初期化に使用される。
     *
     * <p>{@code reset()}が呼ばれるたびに、シード値生成用のRandomオブジェクトが
     * 生成したシード値が別のRandomオブジェクトにセットされ、これが乱数生成を行う。
     *
     * <p> この機構により、各ゲームの手番数が不定であっても、それぞれのゲームでの乱数系列を
     * 引数のシードで一意に決められるようになっている。
     *
     * @param size 最大の目（最小の目は常に１）
     * @param seed Randomに渡すシード値
     * @return 構築されたダイス
     */
    public static Dice create(int size, long seed) {
        return new RandomDice(size, new Random(seed));
    }

    @Override
    public int roll() {
        return r.nextInt(size) + 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void reset() {
        counter.incrementAndGet();
        r.setSeed(seedGen.nextLong());
    }

    @Override
    public int resetCount() {
        return counter.get();
    }
}
