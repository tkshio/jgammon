package com.github.tkshio.jgammon.tdlearn.td;

import com.github.tkshio.jgammon.tdlearn.TDConf;

/**
 * {@link TDTrainer}のビルダークラス
 */
public class TDTrainerBuilder {
    private double learning_rate = 0.01;
    private double lambda = 0.7;

    public static TDTrainerBuilder builder() {
        return new TDTrainerBuilder();
    }

    public TDTrainerImpl build() {
        return new TDTrainerImpl(learning_rate, lambda);
    }

    /**
     * {@link TDConf}参照
     *
     * @param learning_rate 学習係数
     * @return ビルダー
     */
    public TDTrainerBuilder learning_rate(double learning_rate) {
        this.learning_rate = learning_rate;
        return this;
    }

    /**
     * {@link TDConf}参照
     *
     * @param lambda 減衰係数
     * @return ビルダー
     */
    public TDTrainerBuilder lambda(double lambda) {
        this.lambda = lambda;
        return this;
    }
}
