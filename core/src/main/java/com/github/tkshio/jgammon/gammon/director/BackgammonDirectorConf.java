package com.github.tkshio.jgammon.gammon.director;

import com.github.tkshio.jgammon.common.context.GameContextHandler.TebanContextHandler;
import com.github.tkshio.jgammon.common.director.ChildStatesProvider;
import com.github.tkshio.jgammon.common.director.Director;
import com.github.tkshio.jgammon.common.evaluator.RandomEvaluator;
import com.github.tkshio.jgammon.common.node.IndexedStateNode;
import com.github.tkshio.jgammon.common.utils.SGTuple;
import com.github.tkshio.jgammon.gammon.BackgammonBoard;
import com.github.tkshio.jgammon.gammon.BackgammonDice;
import com.github.tkshio.jgammon.gammon.BackgammonState;
import com.github.tkshio.jgammon.gammon.BackgammonStateOperator;
import com.github.tkshio.jgammon.gammon.move.Roll;

import java.util.Collections;
import java.util.function.Function;

/**
 * バックギャモン固有の設定を行い、{@link Director}を構成するための情報を保持し、Directorを生成する
 */
public interface BackgammonDirectorConf {
    /**
     * 1Ply、すなわち、ロールを見て、自分が可能な手のリストだけを
     * プレイヤーに提供するDirectorを構成するConfを生成する
     *
     * @return 生成されたBackgammonDirectorConf
     */
    static BackgammonDirectorConf onePlyDirectorConf() {
        return (stateOperator, openingRoll, initialState) -> {
            var nsp = onePlyNextStateProvider(stateOperator, openingRoll);

            return builder(nsp,
                    node -> node,
                    openingRoll,
                    initialState);
        };

    }

    /**
     * 2Ply、自分が可能な手に加え、それに対応する相手の応手も
     * プレイヤーに提供するDirectorを構成するConfを生成する
     *
     * @return 生成されたBackgammonDirectorConf
     */
    static BackgammonDirectorConf twoPlyDirectorConf() {
        return (stateOperator, openingRoll, initialState) -> {
            var nsp = twoPlyNextStateProvider(stateOperator);

            return builder(nsp,
                    node -> node.grow(
                            Collections.singleton(openingRoll),
                            stateOperator::generateRoll,
                            stateOperator::buildNextState),

                    openingRoll,
                    initialState);
        };
    }

    /**
     * 指定の初期盤面と使用するダイスだけでDirectorBuilderを準備する、テスト用メソッド
     *
     * @param board 初期盤面
     * @param dice  使用するダイス
     * @return 生成されたDirectorBuilder(1ply)
     */
    static Director.DirectorBuilder<BackgammonState, IndexedStateNode<BackgammonState, Roll>>
    builder(BackgammonBoard board, BackgammonDice dice) {
        var op = BackgammonStateOperator.create(dice);
        var initialState = op.whiteGoesFirst(board);
        return onePlyDirectorConf().configuredBuilder(
                op,
                dice.rollOpening().asRoll(),
                initialState
        )
                .contextHandlers(SGTuple.of(new TebanContextHandler<>() {
                }))
                .nodesEvaluators(SGTuple.of(RandomEvaluator.nodesEvaluator()));
    }

    private static Director.DirectorBuilder<BackgammonState,
            IndexedStateNode<BackgammonState, Roll>>
    builder(ChildStatesProvider<IndexedStateNode<BackgammonState, Roll>> nsp,
            Function<IndexedStateNode<BackgammonState, Roll>, IndexedStateNode<BackgammonState, Roll>> initialNodeDecorator,
            Roll openingRoll,
            BackgammonState initialState) {

        var _initialNode = IndexedStateNode.initialNode(
                initialState,
                () -> openingRoll);
        var initialNode = initialNodeDecorator.apply(_initialNode);

        return Director.<BackgammonState, IndexedStateNode<BackgammonState, Roll>>builder()
                .childStatesProvider(nsp)
                .eog(state -> state.getResult().isOver())
                .initialNode(initialNode)
                ;
    }

    private static ChildStatesProvider<IndexedStateNode<BackgammonState, Roll>>
    onePlyNextStateProvider(BackgammonStateOperator stateOperator, Roll openingRoll) {
        // OnDemandProviderは、あらかじめ先読みした局面を用意せず、
        // 手番を相手に渡して次の局面を生成するときに次の局面を構成する

        // ここでは、手番を渡すときにロールを行うことを指定している
        return ChildStatesProvider.createOndemandProvider(
                // 最初の局面ではオープニングロールを使う
                state -> stateOperator.buildNextState(
                        state, openingRoll),
                // それ以降の局面では、ロールを行って次の局面を列挙する
                state -> stateOperator.buildNextState(
                        state, stateOperator.generateRoll()),
                // 新たに生成された局面に、ロール処理を紐づけてIndexedStateNodeとする
                stateCollection -> IndexedStateNode.wrapAsNodes(
                        stateCollection, () -> {
                            throw new UnsupportedOperationException();
                        }
                        // 先読みをしないので、このgenerateRollは実際には使用されない
                        // ここでは単にnullの代わりに形式的に渡しているに過ぎない
                )
        );
    }

    private static ChildStatesProvider<IndexedStateNode<BackgammonState, Roll>>
    twoPlyNextStateProvider(BackgammonStateOperator stateOperator) {
        // BuildAheadProviderでは、次局面の取得方法と、
        // ノードの成長のさせ方を指定する

        return ChildStatesProvider.createBuildAheadProvider(
                // 次局面のノードを提供するメソッド
                IndexedStateNode::getNextStates,

                // 末端ノードを延ばすときの処理
                _node -> _node.grow(
                        // ありうるロール（21通り）を列挙
                        stateOperator.listupRolls(),
                        // 次の局面を選ぶ際にはロールを行う
                        stateOperator::generateRoll,
                        // 実際に次の局面を生成するメソッド
                        stateOperator::buildNextState)
        );
    }

    /**
     * 設定された情報をもとに、追加要素を受け取ってDirectorBuilderを準備する
     *
     * @param stateOperator 局面を管理するオブジェクト
     * @param openingRoll   オープニングロール
     * @param initialState  初期局面
     * @return 生成されたDirectorBuilder
     */
    Director.DirectorBuilder<BackgammonState,
            IndexedStateNode<BackgammonState, Roll>> configuredBuilder(
            BackgammonStateOperator stateOperator,
            Roll openingRoll,
            BackgammonState initialState
    );
}
