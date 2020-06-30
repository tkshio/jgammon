package com.github.tkshio.jgammon.common.node;

import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestSimpleStateNode {
    @Test
    public void runSimpleStateNode() {
        // Φ
        SimpleStateNode<MockState> initialNode = SimpleStateNode.initialNode(new MockState());

        // Φ -> S1, S2, S3
        SimpleStateNode<MockState> added1Ply = initialNode.grow(
                TestSimpleStateNode.this::buildActionsForState3);

        // Φ -> (S1 -> S11, S12), (S2 -> S21, S22), (S3 -> S31,S32 )
        SimpleStateNode<MockState> added2ply = added1Ply.grow(
                this::buildActionsForState2);

        // StateNodeから、選択可能な手の一覧を得られる
        Collection<SimpleStateNode<MockState>> ply1Candidates
                = added2ply.getChildren();
        assertEquals(3, ply1Candidates.size());

        // もともとのノードはgrowの影響を受けない
        assertEquals(0, initialNode.getChildren().size());

        // added2plyの時に生成した末端なので、2個ずつ子ノードがある
        SimpleStateNode<MockState> ply1_with1plyAdded = ply1Candidates.iterator().next();
        assertEquals(2, ply1_with1plyAdded.getChildren().size());

        // ply1の末端に新たに4ノードずつ加えたツリーを構成する
        SimpleStateNode<MockState> ply1_added2ply = ply1_with1plyAdded.grow(
                this::buildActionsForState4);
        Collection<SimpleStateNode<MockState>> ply2_candidates
                = ply1_added2ply.getChildren();
        //
        var ply2 = ply2_candidates.iterator().next();
        assertEquals(4, ply2.getChildren().size());
    }

    private Collection<MockState> buildActionsForState2(MockState state) {
        return List.of(new MockState(), new MockState());
    }

    private Collection<MockState> buildActionsForState3(MockState state) {
        return List.of(new MockState(), new MockState(), new MockState());

    }

    private Collection<MockState> buildActionsForState4(MockState state) {
        return List.of(
                new MockState(), new MockState(),
                new MockState(), new MockState());

    }
}

class MockState {
}

