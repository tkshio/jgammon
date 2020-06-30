package com.github.tkshio.jgammon.tdlearn.nn;

import com.github.tkshio.jgammon.tdlearn.matrix.Matrix;

import java.util.ArrayList;
import java.util.List;

/**
 * 層の勾配を表すクラス
 *
 * @param <MATRIX> 行列を表現するクラス
 */
public class NNLayerGradient<MATRIX> {
    private final NNLayerAmount<MATRIX> amount;
    private final NNLayer<MATRIX> layer;

    /**
     * コンストラクター
     *
     * @param amount 勾配量
     * @param layer  この勾配が対象としている層
     */
    public NNLayerGradient(NNLayerAmount<MATRIX> amount, NNLayer<MATRIX> layer) {
        this.amount = amount;
        this.layer = layer;
    }

    /**
     * 勾配量を返す
     *
     * @return 勾配量
     */
    public NNLayerAmount<MATRIX> getAmount() {
        return amount;
    }

    /**
     * この層への入力の勾配を、出力ノード別に求める
     *
     * @return 各出力ノードについて求めた勾配のリスト
     */
    Iterable<Matrix<MATRIX>> getGradients() {

        // 結果のリストの要素数は、出力ノード数と同じ
        List<Matrix<MATRIX>> gradients = new ArrayList<>(amount.getBias().columns());
        {
            // 活性化関数の勾配（バイアスの勾配に等しいので、それを使用する）
            double[] activatorsGradiates = amount.getBias().getData()[0];

            // ネットワークの重みづけ
            var followingWeightTranspose = layer.getWeight().transpose();

            // この層への入力の勾配を、ノード別に算出する
            for (int i = 0; i < activatorsGradiates.length; i++) {
                // 1 x (入力ノード数） の行列が、この層での勾配
                var w_i = followingWeightTranspose.getRowMatrix(i);

                // この層以降での勾配
                double g_i = activatorsGradiates[i];

                // 両者の積によって、この層の入力ノードから逆伝播させる勾配が得られる
                gradients.add(w_i.scalarMultiply(g_i));
            }
        }
        return gradients;
    }
}
