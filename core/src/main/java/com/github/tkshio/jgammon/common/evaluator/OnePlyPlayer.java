package com.github.tkshio.jgammon.common.evaluator;

import com.github.tkshio.jgammon.common.context.GameContextHandler;
import com.github.tkshio.jgammon.common.director.Player;
import com.github.tkshio.jgammon.common.node.Node;
import com.github.tkshio.jgammon.common.utils.SGTuple;
import lombok.Builder;

import java.util.Collection;
import java.util.Comparator;

/**
 * 一手読みのプレイヤー
 *
 * <p>自分の手を適用した後の局面を評価して、最善の手を選ぶ。
 *
 * @param <STATE> 局面を表すオブジェクト
 */
@Builder
public class OnePlyPlayer<STATE> implements Player<STATE> {
    @Builder.Default
    private final String name = "NONAME";


    private final SGTuple<StateEvaluator<STATE>> evs;

    /**
     * ファクトリーメソッド
     *
     * @return 生成されたNodeEvaluatorのSGペア
     */
    public SGTuple<NodesEvaluator<STATE>> createEvaluators() {
        return evs.apply(this::createEvaluator);
    }

    private NodesEvaluator<STATE> createEvaluator(StateEvaluator<STATE> ev) {
        return new NodesEvaluator<>() {
            @Override
            public <T extends Node<STATE>>
            EvaluatedNode<T> evaluate(T stateNode,
                                      Collection<T> candidates,
                                      GameContextHandler.TebanContextHandler<STATE> contextHandler) {

                EvaluatedNode<T> evaluatedNode = candidates.stream()
                        .parallel()

                        // 各子局面を評価
                        .map(node -> {
                                    var state = node.getState();
                                    var eval = ev.eval(state);
                                    contextHandler.evaluateMove(state, eval);
                                    return new EvaluatedNode<>(node, eval);
                                }
                        )

                        // 自分の手なので、最大値を選ぶ
                        .max(Comparator.comparing(
                                EvaluatedNode::getEval))

                        // 可能な手がないときは空リストが渡されるので、例外は起こらない
                        .orElseThrow(IllegalStateException::new);

                // 選択した手についてコールバック
                evaluatedNode.getEval().markAsChoice();
                return evaluatedNode;
            }

            @Override
            public void initialState(STATE initialState) {
                ev.initialState(initialState);
            }

            @Override
            public String getLabel() {
                return name;
            }

            @Override
            public void won(STATE state) {
                ev.won(state);
            }

            @Override
            public void lost(STATE state) {
                ev.lost(state);
            }

            @Override
            public void draw(STATE state) {
                ev.draw(state);
            }

            @Override
            public void abort() {
                ev.abort();
            }

        };
    }

    @Override
    public String getName() {
        return name;
    }

}