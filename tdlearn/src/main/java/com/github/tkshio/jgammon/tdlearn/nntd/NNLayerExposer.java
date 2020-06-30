package com.github.tkshio.jgammon.tdlearn.nntd;

import com.github.tkshio.jgammon.tdlearn.nn.NNLayer;

/**
 * {@link NNTDLearner}の内部構造を、出力用に参照させるインターフェース
 */
public interface NNLayerExposer {

    /**
     * 隠れ層（入力ノード-隠れノードのネットワーク）を返す
     *
     * @return 隠れ層
     */
    NNLayer<?> getHiddenLayer();

    /**
     * 出力層（隠れノード-入力ノードのネットワーク）を返す
     *
     * @return 出力層
     */
    NNLayer<?> getOutputLayer();
}
