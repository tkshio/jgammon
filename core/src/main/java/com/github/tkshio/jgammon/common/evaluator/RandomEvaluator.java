package com.github.tkshio.jgammon.common.evaluator;

import com.github.tkshio.jgammon.common.context.GameContextHandler.TebanContextHandler;
import com.github.tkshio.jgammon.common.node.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * ランダムに手を選択する、ダミーの評価ロジック
 */
public class RandomEvaluator {
    private final static Random r = new Random();

    /**
     * ランダムに手を選択するNodesEvaluatorを返す
     *
     * @param <STATE> 局面を表すクラス
     * @return ランダムに手を選択するNodesEvaluator
     */
    public static <STATE> NodesEvaluator<STATE> nodesEvaluator() {
        return new NodesEvaluator<>() {
            @Override
            public <T extends Node<STATE>> EvaluatedNode<T> evaluate(T stateNode, Collection<T> candidates, TebanContextHandler<STATE> contextHandler) {
                int idx = r.nextInt(candidates.size());
                return new EvaluatedNode<>(
                        new ArrayList<>(candidates).get(idx),
                        () -> idx);
            }

            @Override
            public String getLabel() {
                return "(RANDOM)";
            }
        };
    }
}
