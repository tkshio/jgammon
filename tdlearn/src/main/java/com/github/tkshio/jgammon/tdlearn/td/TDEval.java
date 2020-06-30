package com.github.tkshio.jgammon.tdlearn.td;

/**
 * {@link TDLearner} の評価結果を表すインターフェース
 */
public interface TDEval<GRADIENT> {

    /**
     * 出力層からの出力値
     *
     * @return 出力値
     */
    double[] getOutput();

    /**
     * 出力値から算出される、勾配値
     *
     * <p>勾配値は、更新量の算出に使用する。単純に出力値を得るだけの場合には算出する必要がない。
     *
     * @return 勾配値
     */
    GRADIENT getGradient();
}
