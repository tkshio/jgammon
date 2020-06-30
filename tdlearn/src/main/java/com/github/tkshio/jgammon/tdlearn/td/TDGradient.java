package com.github.tkshio.jgammon.tdlearn.td;

/**
 * 勾配およびその累積を表すインターフェース
 *
 * @param <DELTA>    更新量を表す
 * @param <GRADIENT> このインターフェースの実装クラスの型
 */
public interface TDGradient<DELTA, GRADIENT extends TDGradient<DELTA, GRADIENT>> {
    /**
     * 現在の勾配の累積量に減衰係数λ(0-1)を掛け、新しい勾配と合算した結果を新しい勾配の累積量として返す
     *
     * <p>λに1未満の値を指定することで、過去の局面（最終結果からより遠い局面）による
     * 影響を弱める作用がある。
     *
     * @param gradientToAdd 追加する勾配
     * @param lambda        減衰係数。特に値の範囲は検査しない
     * @return 勾配の累積量
     */
    GRADIENT accum(GRADIENT gradientToAdd, double lambda);

    /**
     * 与えられた誤差値から更新量を得る
     *
     * @param amount 出力ノードでの誤差（想定値 - 出力値）
     * @return 更新量
     */
    DELTA delta(double[] amount);

}
