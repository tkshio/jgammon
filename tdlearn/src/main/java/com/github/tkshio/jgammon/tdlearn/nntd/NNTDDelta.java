package com.github.tkshio.jgammon.tdlearn.nntd;

import com.github.tkshio.jgammon.tdlearn.nn.NNLayerAmount;
import lombok.Value;

/**
 * {@link NNTDLearner}に対する、各層の更新量を表す
 *
 * @param <MATRIX> 行列を表現するクラス
 */
@Value
public class NNTDDelta<MATRIX> {

    /**
     * 出力層の更新量
     */
    NNLayerAmount<MATRIX> output;

    /**
     * 隠れ層の更新量
     */
    NNLayerAmount<MATRIX> hidden;

    /**
     * 与えられた更新量を自身に加算した結果を返す
     *
     * <p>最終結果が得られてからまとめて更新する場合に使用する。
     * 現在は評価の都度ネットワークを更新しているため、動作検証にのみ使用。
     *
     * @param dw 加算する更新量
     * @return 加算された結果
     */
    public NNTDDelta<MATRIX> add(NNTDDelta<MATRIX> dw) {
        return new NNTDDelta<>(
                output.add(dw.getOutput()),
                hidden.add(dw.getHidden()));
    }
}
