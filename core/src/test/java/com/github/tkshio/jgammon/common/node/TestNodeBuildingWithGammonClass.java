package com.github.tkshio.jgammon.common.node;

import com.github.tkshio.jgammon.gammon.*;
import com.github.tkshio.jgammon.gammon.move.Roll;
import com.github.tkshio.jgammon.gammon.utils.BackgammonBoardPrinter;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class TestNodeBuildingWithGammonClass {


    @Test
    public void runNode() {

        SimpleStateNode<BackgammonState> initialNode = SimpleStateNode.initialNode(BackgammonStateOperator.create().whiteGoesFirst(BackgammonBoard.create(BGType.standard)));
        var node1stPlyDepth2 = initialNode.grow(this::buildNextState);

        dump(node1stPlyDepth2);
        assertEquals(15, node1stPlyDepth2.getChildren().size());

        var node1stPlyDepth3 = node1stPlyDepth2.grow(this::buildNextState);
        AtomicInteger co = new AtomicInteger(0);
        node1stPlyDepth3.getChildren().forEach(state -> {
            BackgammonBoardPrinter.println("---- " + co.addAndGet(1));
            dump(state);
        });

    }

    private void dump(Node<BackgammonState> node) {
        node.getChildNodesEntries().stream().flatMap(nodes -> nodes.getNodes().stream()
                .map(Node::getState))
                .forEach(bgState -> {
                    BackgammonBoardPrinter.print(bgState.getAbsoluteBoard());
                    BackgammonBoardPrinter.println();
                });
    }


    Collection<BackgammonState> buildNextState(BackgammonState state) {
        BackgammonStateOperator stateOperator = BackgammonStateOperator.create();
        Roll roll = Roll.of(1, 2);
        return stateOperator.buildNextState(state, roll);
    }

    @Test
    public void runNodeWithNDepth() {
        BackgammonStateOperator stateOperator = BackgammonStateOperator.create();

        // 初期盤面から、開始状態を構成
        Roll rolled = Roll.of(1, 2);
        var board = BackgammonBoard.create(BGType.standard);
        var initialState = BackgammonStateOperator.create()
                .whiteGoesFirst(board);

        // オープニングロールはあらかじめ確定させた上でツリーを構築
        IndexedStateNode<BackgammonState, Roll> initialNode =
                IndexedStateNode.initialNode(initialState, () -> rolled);


        BackgammonDice bgDice = BackgammonDice.create();
        var node1stPlyDepth1 = initialNode.grow(
                // ロールは確定しているので、ツリーを延ばす対象も一つ
                Collections.singleton(rolled),

                // オープニング以降は普通にロールする
                bgDice::roll,
                stateOperator::buildNextState);

        // 1-2を選択したので、ルート直下の子ノードは1つだけ
        assertEquals(1, node1stPlyDepth1.getKeys().size());

        // キーは 1-2
        assertEquals(rolled, node1stPlyDepth1.getKeys().iterator().next());

        // dump(node1stPlyDepth1);

        // さらに普通のロールでツリーを延ばし、1-2に対応する子ノードのリストを得る
        var node1stPlyDepth2 = node1stPlyDepth1
                .grow(bgDice.listupRolls(),
                        bgDice::roll,
                        stateOperator::buildNextState)
                .getNextStates();

        // 初期配置に対する1-2のムーブは15通りある（オープニングロールの目の15通りとは無関係）
        assertEquals(15, node1stPlyDepth2.size());

        // 各孫ノードは、普通のロールで状態を生成したので21種類の状態を持っている
        node1stPlyDepth2.forEach(node -> {
            var rolls = node.getKeys();
            assertEquals(21, rolls.size());
        });
    }
}

