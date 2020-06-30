package com.github.tkshio.jgammon.common.evaluator;

import com.github.tkshio.jgammon.common.context.GameContextHandler;
import com.github.tkshio.jgammon.common.director.Player;
import com.github.tkshio.jgammon.common.node.Node;
import com.github.tkshio.jgammon.common.utils.SGTuple;
import lombok.Builder;

import java.util.Collection;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.stream.Collectors;

/**
 * 二手読みのプレイヤー
 *
 * <p>自分の手を適用した後の局面について、そこから可能な相手の手をすべて列挙、評価し、
 * 最も自分に有利な手を選ぶ
 *
 * @param <STATE> 局面を表すオブジェクト
 */
@Builder
public class TwoPlyPlayer<STATE> implements Player<STATE> {
    @Builder.Default
    private final String name = "NONAME";

    private final SGTuple<StateEvaluator<STATE>> evs;

    /**
     * ファクトリーメソッド
     *
     * @return 生成されたNodeEvaluatorのSGペア
     */
    public SGTuple<NodesEvaluator<STATE>> createEvaluators() {
        return SGTuple.of(
                // TwoPlyでは、相手の観点で盤面を評価する
                createEvaluator(evs.gote()),
                createEvaluator(evs.sente()));
    }


    private NodesEvaluator<STATE> createEvaluator(StateEvaluator<STATE> ev) {
        return new NodesEvaluator<>() {
            @Override
            public <T extends Node<STATE>>
            EvaluatedNode<T> evaluate(T stateNode,
                                      Collection<T> candidates,
                                      GameContextHandler.TebanContextHandler<STATE> contextHandler) {

                return candidates.stream()
                        .parallel()
                        .map(node -> {
                                    var state = node.getState();


                                    // 子ノードを評価する
                                    Eval eval = evalChildNodes(node, ev);
                                    contextHandler.evaluateMove(state, eval);
                                    return new EvaluatedNode<>(node, eval);
                                }
                        )

                        // 最大値を選ぶのはOnePlyPlayerと同じ
                        .max(Comparator.comparing(
                                EvaluatedNode::getEval))
                        .orElseThrow(IllegalStateException::new);

                // 2plyでは評価器の評価結果を平均して最終評価とするので、
                // フィードバックはない

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
                ev.lost(state);
            }

            @Override
            public void lost(STATE state) {
                ev.won(state);
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


    /**
     * 子局面について、相手の観点での最善手を選び、その評価値を正負反転させて自評価とする
     *
     * @param node    評価対象
     * @param tebanEv 評価を行うオブジェクト
     * @param <T>     ノードの型
     * @return 評価値
     */
    private <T extends Node<STATE>>
    Eval evalChildNodes(T node, StateEvaluator<STATE> tebanEv) {
        // ロール別に分けられた次局面
        var childNodesEntries = node.getChildNodesEntries();

        if (childNodesEntries.size() == 0) {
            // 次局面のグループがない（設定不備により、Director側が次の局面を生成していない）なら、
            // 継続できない
            throw new IllegalStateException();
        }

        DoubleSummaryStatistics wSum = new DoubleSummaryStatistics();
        double sum = childNodesEntries.stream()
                .parallel()
                // 各グループ（ロール）について、相手方の最善の手の評価値を得る
                .collect(Collectors.summarizingDouble(
                        childNodesEntry -> {
                            wSum.accept(childNodesEntry.getWeight());
                            return childNodesEntry.getNodes().stream().map(
                                    // あるロールに対するプレイを評価
                                    n -> {
                                        var state = n.getState();
                                        return tebanEv.eval(state);
                                    })

                                    // 相手にとって最善手を選ぶ
                                    .max(Eval::compareTo)
                                    .orElseGet(
                                            // ※
                                            () -> () -> Double.NEGATIVE_INFINITY
                                    )
                                    // ロールの出現率に応じて重みづけ
                                    .getScore() * childNodesEntry.getWeight();
                        })
                )
                .getSum();

        // ※
        // 次局面がない場合、すでに対局は終了している
        // バックギャモンの場合、対局の終了は最後にプレイした側の勝利で、獲得点数は
        // その時の手の選択に依存しない（相手の駒の配置で決まる）ので、
        // ここでは一律に最大値を返している

        // TwoPlyに限らず、ゲーム進行の処理には引き分けがない・パス可能・
        // 常にプレイ側が勝ち終わりなどバックギャモン固有の作りこみが
        // 残ってしまっている


        // 相手方の手番として評価しているので、正負反転させて自分の評価値とする
        // （呼び出し元がmaxで選べるようにする）
        return () -> -sum / wSum.getSum();
    }

    @Override
    public String getName() {
        return name;
    }
}
