package com.github.tkshio.jgammon.tdlearn.nn;

import com.github.tkshio.jgammon.tdlearn.matrix.Matrix;

import java.util.List;

/**
 * ニューラルネットワークを構成する層を、重みとバイアスの対として表すインターフェース
 *
 * @param <MATRIX> 行列を表現するクラス
 */
public interface NNLayer<MATRIX> {

    /**
     * この層に入力を与え、出力（重みを掛けてバイアスを足した結果）を得る
     *
     * @param input 入力
     * @return 出力
     */
    Matrix<MATRIX> apply(Matrix<MATRIX> input);


    /**
     * 重みとバイアスを指定の更新量で更新した、新しいオブジェクトを返す
     *
     * <p>このメソッドの呼び出しは、元のオブジェクトには影響を与えない。
     *
     * @param delta 更新量
     * @return 更新された行列
     */
    NNLayer<MATRIX> update(NNLayerAmount<MATRIX> delta);

    /**
     * 重みを得る
     *
     * @return 重み
     */
    Matrix<MATRIX> getWeight();

    /**
     * バイアスを得る
     *
     * @return バイアス
     */
    Matrix<MATRIX> getBias();

    /**
     * この層に対する入力と出力（通常は{@code apply}で求める）から勾配を得る
     *
     * @param input  入力
     * @param output 出力
     * @return 勾配
     */
    NNLayerGradient<MATRIX> calcGradient(Matrix<MATRIX> input, Matrix<MATRIX> output);

    /**
     * 出力ノード別に、この層に対する勾配を求める
     * <p>
     * Temporal-Differenceでは最終的な結果が得られない状態で学習を進めていくため、この処理が必要となる。
     *
     * @param input    入力
     * @param output   出力
     * @param gradient 逆伝播されてきた、この層の直後の層の勾配
     * @return 勾配のリスト
     */
    List<NNLayerAmount<MATRIX>> calcGradients(Matrix<MATRIX> input, Matrix<MATRIX> output, NNLayerGradient<MATRIX> gradient);


}
