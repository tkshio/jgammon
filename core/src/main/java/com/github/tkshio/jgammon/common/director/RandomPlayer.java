package com.github.tkshio.jgammon.common.director;

import com.github.tkshio.jgammon.common.evaluator.NodesEvaluator;
import com.github.tkshio.jgammon.common.evaluator.RandomEvaluator;
import com.github.tkshio.jgammon.common.utils.SGTuple;

/**
 * ランダムに手を選択するダミーのプレイヤー
 */
public class RandomPlayer {
    /**
     * ファクトリーメソッド
     *
     * @param name    出力用の名前
     * @param <STATE> 局面を表すクラス
     * @return 構築されたプレイヤー
     */
    public static <STATE> Player<STATE> create(String name) {
        return new Player<>() {
            @Override
            public SGTuple<NodesEvaluator<STATE>> createEvaluators() {
                // 候補手をランダムな値で評価して最大値を選ぶのではなく、
                // 単純に候補手のどれかを(ランダムに）選ぶ
                return SGTuple.of(RandomEvaluator.nodesEvaluator());
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }

}
