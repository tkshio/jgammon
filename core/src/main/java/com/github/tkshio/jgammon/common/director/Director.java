package com.github.tkshio.jgammon.common.director;

import com.github.tkshio.jgammon.common.context.GameContextHandler;
import com.github.tkshio.jgammon.common.director.GameRunner.GameState;
import com.github.tkshio.jgammon.common.evaluator.EvaluatedNode;
import com.github.tkshio.jgammon.common.evaluator.NodesEvaluator;
import com.github.tkshio.jgammon.common.node.Node;
import com.github.tkshio.jgammon.common.utils.SGTuple;
import lombok.Builder;

import java.util.Collection;
import java.util.function.Function;


/**
 * 対局の進行を行うクラス
 *
 * <p> このフレームワークでは対局はNodeオブジェクトによってツリー構造で保持された局面を、
 * 手番ごとに1ノードずつ辿っていくことで進行する。
 *
 * <p>Directorはツリーの生成、プレイヤーへの局面の引き渡し、終了判定を繰り返す役割を担っている。
 *
 * @param <STATE> 局面を表すクラス
 * @param <NODE>  ノードを表すクラス
 */
@Builder
public class Director<STATE, NODE extends Node<STATE>> {

    private final NODE initialNode;
    private final SGTuple<NodesEvaluator<STATE>> nodesEvaluators;
    private final SGTuple<GameContextHandler.TebanContextHandler<STATE>> contextHandlers;
    private final ChildStatesProvider<NODE> childStatesProvider;
    private final Function<STATE, Boolean> eog;

    /**
     * 手番プレイヤーを生成する、すなわち各プレイヤーに先手・後手を割り当てる
     *
     * @return 生成された手番プレイヤーの対
     */
    SGTuple<TebanPlayer<STATE, NODE>> createTebanPlayers() {
        var sente = buildTebanPlayer(nodesEvaluators.sente(),
                contextHandlers.sente(),
                nodesEvaluators.gote());

        var gote = buildTebanPlayer(nodesEvaluators.gote(),
                contextHandlers.gote(),
                nodesEvaluators.sente());
        return SGTuple.of(sente, gote);
    }

    // 先手・後手それぞれに対応したプレイヤーの構築
    private TebanPlayer<STATE, NODE> buildTebanPlayer(
            NodesEvaluator<STATE> self,
            GameContextHandler.TebanContextHandler<STATE> contextHandler,
            NodesEvaluator<STATE> opponent

    ) {
        return new TebanPlayer<>() {
            @Override
            public GameState<STATE, NODE> playOpening(NODE openingNode) {
                // 子の処理の呼び分けだけのためにインターフェースが分かれている
                return doPlay(openingNode,
                        childStatesProvider.firstChildStates(openingNode));
            }

            @Override
            public GameState<STATE, NODE> play(NODE stateNode) {
                return doPlay(stateNode,
                        childStatesProvider.childStates(stateNode));
            }

            private GameState<STATE, NODE> doPlay(NODE stateNode, Collection<NODE> candidates) {
                // ContexTHandlerに手番開始・評価開始を告げる
                {
                    var state = stateNode.getState();
                    contextHandler.beginPly(self.getLabel(), state);
                    contextHandler.startEvaluation(
                            self.getLabel(),
                            state,
                            () -> candidates.stream().map(Node::getState));
                }

                // 各候補手の評価を行い、次の局面を選択する
                EvaluatedNode<NODE> selectedNode =
                        self.evaluate(stateNode, candidates, contextHandler);

                // 終了判定を行う
                boolean isEOG;
                {
                    var nextState = selectedNode.getStateNode().getState();
                    contextHandler.endPly(self.getLabel(), nextState);

                    isEOG = eog.apply(nextState);
                    if (isEOG) {
                        self.won(nextState);
                        opponent.lost(nextState);
                    }
                    contextHandler.endEvaluation(self.getLabel(), nextState, selectedNode.getEval());
                }

                // 終了・継続いすれにせよ、ゲーム進行状態として返す
                return new GameState<>(selectedNode.getStateNode(), isEOG);
            }
        };
    }

    /**
     * 対局を開始する
     *
     * @return 初期局面のノード
     */
    NODE startGame() {
        var state = initialNode.getState();
        nodesEvaluators.applyEach(nEv -> nEv.initialState(state));
        return initialNode;
    }

    /**
     * 対局を中断する
     */
    void abortGame() {
        nodesEvaluators.applyEach(NodesEvaluator::abort);
    }

    /**
     * 先手・後手が手番で行うアクションを抽象化したインターフェース
     *
     * @param <STATE> 局面を表すクラス
     * @param <T>     ノードを表すクラス
     */
    interface TebanPlayer<STATE, T extends Node<STATE>> {

        /**
         * 1手番をプレイする
         *
         * @param node 現局面（プレイ直前の局面）を表すノード
         * @return プレイ後のゲーム進行状態
         */
        GameState<STATE, T> play(T node);

        /**
         * 初手をプレイする
         *
         * @param openingNode 開始局面を表すノード
         * @return プレイ後のゲーム進行状態
         */
        default GameState<STATE, T> playOpening(T openingNode) {
            return play(openingNode);
        }
    }

    /**
     * ビルダークラス
     *
     * @param <STATE> 局面を表すクラス
     * @param <NODE>  ノードを表すクラス
     */
    public static class DirectorBuilder<STATE, NODE extends Node<STATE>> {

    }

}

