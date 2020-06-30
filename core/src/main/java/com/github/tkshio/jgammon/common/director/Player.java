package com.github.tkshio.jgammon.common.director;

import com.github.tkshio.jgammon.common.evaluator.NodesEvaluator;
import com.github.tkshio.jgammon.common.utils.SGTuple;

/**
 * プレイヤーを表すクラス
 *
 * @param <STATE> 局面を表すクラス
 */
public interface Player<STATE> {
    /**
     * 先手担当・後手担当それぞれで使用するNodesEvaluatorの対を返す
     *
     * <p>{@link Director}は先手・後手を決定したのち、各プレイヤーがこのメソッド提供する
     * オブジェクトのいずれかを取得、ゲーム進行に使用する。
     *
     * @return NodesEvaluatorの対
     */
    SGTuple<NodesEvaluator<STATE>> createEvaluators();

    /**
     * 出力用の名前を返す
     *
     * @return 名前を表す文字列
     */
    String getName();
}
