package com.github.tkshio.jgammon.gammon.director;

import com.github.tkshio.jgammon.common.context.GameContextHandler;
import com.github.tkshio.jgammon.common.dice.PresetDice;
import com.github.tkshio.jgammon.common.dice.RandomDice;
import com.github.tkshio.jgammon.common.director.GameRunner;
import com.github.tkshio.jgammon.common.evaluator.EvaluatedNode;
import com.github.tkshio.jgammon.common.evaluator.NodesEvaluator;
import com.github.tkshio.jgammon.common.node.Node;
import com.github.tkshio.jgammon.common.utils.SGTuple;
import com.github.tkshio.jgammon.gammon.*;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class TestBackgammonNodeDirector {
    private static final NodesEvaluator<BackgammonState> ev = new NodesEvaluator<>() {
        @Override
        public <T extends Node<BackgammonState>>
        EvaluatedNode<T> evaluate(T stateNode,
                                  Collection<T> candidates,
                                  GameContextHandler.TebanContextHandler<BackgammonState> contextHandler) {
            var node = candidates.stream().findAny().orElseThrow();
            return new EvaluatedNode<>(node, () -> 0) {
            };
        }

        @Override
        public String getLabel() {
            return "forTEST";
        }
    };
    public static final SGTuple<NodesEvaluator<BackgammonState>> evs = SGTuple.of(ev, ev);

    @Test
    public void runFromWhiteEoG() {
        int[] initialArrangements = new int[]{
                0,
                -1, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 1,
                0
        };


        BackgammonBoard board = BackgammonBoard.create(BGType.standard, initialArrangements);
        var dice = BackgammonDice.create(
                PresetDice.create(
                        RandomDice.create(6), 1, 2, 3, 4, 5, 6));
        var director = BackgammonDirectorConf.builder(board, dice)
                .nodesEvaluators(evs)
                .build();

        var runner = GameRunner.create(director);
        var op = runner.startGame();
        var redPly = runner.doOpening(op).getNode();

        assertEquals(BackgammonResult.Single, redPly.getState().getResult());
    }

    @Test
    public void runFromBeforeWhiteEoG() {
        int[] initialArrangements = new int[]{
                0,
                -3, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 3,
                0
        };
        BackgammonBoard board = BackgammonBoard.create(BGType.standard, initialArrangements);
        var dice = BackgammonDice.create(
                PresetDice.create(
                        RandomDice.create(6), 1, 2, 3, 4, 5, 6));
        var director = BackgammonDirectorConf.builder(board, dice)
                .nodesEvaluators(evs)
                .build();

        var runner = GameRunner.create(director);
        var op = runner.startGame();
        var redPly = runner.doOpening(op).getNode();


        assertEquals(BackgammonResult.InGame, redPly.getState().getResult());

        // red's move
        var whitePly = runner.doGoteBan(redPly);
        assertEquals(BackgammonResult.InGame, whitePly.getNode().getState().getResult());

        // white's move again
        var redPly2 = runner.doSenteBan(whitePly.getNode());
        assertEquals(BackgammonResult.Single, redPly2.getNode().getState().getResult());


    }

    @Test
    public void runFromRedEoG() {
        int[] initialArrangements = new int[]{
                0,
                -3, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 1,
                0
        };

        BackgammonBoard board = BackgammonBoard.create(BGType.standard, initialArrangements);
        var dice = BackgammonDice.create(
                PresetDice.create(
                        RandomDice.create(6), 1, 2, 3, 4, 5, 6));
        var director = BackgammonDirectorConf.builder(board, dice)
                .nodesEvaluators(evs)
                .build();

        var runner = GameRunner.create(director);
        var op = runner.startGame();
        var redPly = runner.doOpening(op).getNode();


        assertEquals(BackgammonResult.InGame, redPly.getState().getResult());

        // red's move
        var whitePly = runner.doGoteBan(redPly).getNode();
        assertEquals(BackgammonResult.Single, whitePly.getState().getResult());

    }
}