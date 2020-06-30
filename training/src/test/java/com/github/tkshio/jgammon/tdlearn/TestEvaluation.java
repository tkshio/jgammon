package com.github.tkshio.jgammon.tdlearn;

import com.github.tkshio.jgammon.DefaultTDReader;
import com.github.tkshio.jgammon.common.context.GameContextHandler;
import com.github.tkshio.jgammon.common.evaluator.*;
import com.github.tkshio.jgammon.common.node.IndexedStateNode;
import com.github.tkshio.jgammon.common.utils.SGTuple;
import com.github.tkshio.jgammon.gammon.*;
import com.github.tkshio.jgammon.gammon.move.Roll;
import com.github.tkshio.jgammon.gammon.utils.BackgammonBoardPrinter;
import com.github.tkshio.jgammon.gammon.utils.RollUtils;
import com.github.tkshio.jgammon.tdlearn.bg.BGConf;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

// 学習済評価器で任意の盤面を評価する・テストとしては機能しない
public class TestEvaluation {
    private static SGTuple<StateEvaluator<BackgammonState>> getDefaultEVS() throws IOException {
        try (BufferedReader reader = DefaultTDReader.getDefaultTDReader()) {
            var conf = BGConf.builder().build();
            return TDLearnEvaluatorReader.readAsStableEv(conf, reader);
        }
    }

    private static synchronized void outputResult(BackgammonState state, Eval eval) {
        BackgammonBoardPrinter.println(String.format("%s %s %f",
                state.getLastCheckerPlay().formatInDesc(),
                eval.asString(),
                eval.getScore()));
        BackgammonBoardPrinter.print(state.getAbsoluteBoard());
    }

    @Test
    public void evaluateBoard() throws IOException {

        // 開始局面の評価

        BackgammonBoard board = BackgammonBoard.create(BGType.standard);
        evaluate(board);
    }

    @Test
    public void evaluateOpeningRolls() throws IOException {

        // オープニングロールの比較（1ply読み）

        var rolls = RollUtils.listupOpeningRolls(6);
        for (Roll roll : rolls) {
            BackgammonBoard board = BackgammonBoard.create(BGType.standard);
            evaluate(board, roll);
        }
    }

    @Test
    public void evaluateOpeningRollsWith2Ply() throws IOException {

        // オープニングロールの比較（2ply読み）

        var rolls = RollUtils.listupOpeningRolls(6);
        for (Roll roll : rolls) {
            BackgammonBoard board = BackgammonBoard.create(BGType.standard);
            evaluateWith2PlyPlayer(board, roll);
        }
    }

    @Test
    public void evaluate2PlyWithBearingOffPosition() throws IOException {

        // 上がりか上がりでないかで勝敗を選択できる状況
        // 両方上げれば勝ち、あえて一つだけ上げると相手の勝ち

        Roll roll = Roll.of(1, 3);
        BackgammonBoard board = BackgammonBoard.create(BGType.standard, new int[]{
                0,
                -2, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 1, 0, 1,
                0
        });
        evaluateAndDumpWith2PlyPlayer(board, roll);
    }

    @Test
    public void evaluate2PlyWithGammonSave() throws IOException {

        // ギャモンセーブ

        Roll roll = Roll.of(1, 3);
        BackgammonBoard board = BackgammonBoard.create(BGType.standard, new int[]{
                0,
                -2, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 1, 0, 0,
                0, 0, 1, 0, 0, 13,
                0
        });
        evaluateAndDumpWith2PlyPlayer(board, roll);
    }

    @Test
    public void evaluate2PlyWithBackgammonSave() throws IOException {

        // バックギャモンセーブ

        Roll roll = Roll.of(1, 3);
        BackgammonBoard board = BackgammonBoard.create(BGType.standard, new int[]{
                0,
                -2, 0, 0, 1, 0, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 1, 0, 0, 13,
                0
        });
        evaluateAndDumpWith2PlyPlayer(board, roll);
    }

    private void evaluate(BackgammonBoard board) throws IOException {
        BackgammonStateOperator stateOperator;
        BackgammonState state;
        {
            BackgammonDice dice = BackgammonDice.create();

            stateOperator = BackgammonStateOperator.create(dice);
            state = stateOperator.redGoesFirst(board);
        }

        // 先読みをしない場合は、直接評価エンジンを使って評価すればよい
        StateEvaluator<BackgammonState> ev = getDefaultEVS().sente();

        Eval eval = ev.eval(state);
        outputResult(state, eval);

    }

    private void evaluate(BackgammonBoard board, Roll roll) throws IOException {
        BackgammonStateOperator stateOperator;
        BackgammonState initialState;
        {
            BackgammonDice dice = BackgammonDice.create();

            stateOperator = BackgammonStateOperator.create(dice);
            initialState = stateOperator.redGoesFirst(board);
        }

        // 先読みをしない場合は、直接評価エンジンを使って評価すればよい
        StateEvaluator<BackgammonState> ev = getDefaultEVS().sente();

        // 最善手を選ぼうとすると、OnePlyPlayerとほぼ同じ実装になる
        Collection<BackgammonState> states = stateOperator.buildNextState(initialState, roll);
        EvaluatedNode<BackgammonState> bestPlay = states.stream()
                .map(state1 -> {
                    return new EvaluatedNode<>(state1,
                            ev.eval(state1));
                })
                .max(Comparator.comparing(EvaluatedNode::getEval))
                .orElseThrow();

        var state = bestPlay.getStateNode();
        var eval = bestPlay.getEval();

        outputResult(state, eval);
    }

    void evaluateWith2PlyPlayer(BackgammonBoard board, Roll roll) throws IOException {
        evaluateWith2PlyPlayer(board, roll, new GameContextHandler.TebanContextHandler<>() {
        });
    }

    void evaluateAndDumpWith2PlyPlayer(BackgammonBoard board, Roll roll) throws IOException {
        evaluateWith2PlyPlayer(board, roll, new GameContextHandler.TebanContextHandler<>() {
            @Override
            public void evaluateMove(BackgammonState state, Eval e) {
                outputResult(state, e);
            }
        });
    }

    void evaluateWith2PlyPlayer(BackgammonBoard board, Roll roll, GameContextHandler.TebanContextHandler<BackgammonState> handler) throws IOException {

        BackgammonStateOperator stateOperator;
        BackgammonState initialState;
        {
            BackgammonDice dice = BackgammonDice.create();

            stateOperator = BackgammonStateOperator.create(dice);
            initialState = stateOperator.redGoesFirst(board);
        }

        // 2plyで評価するため、プレイヤーの準備から行う
        TwoPlyPlayer<BackgammonState> player;
        {
            SGTuple<StateEvaluator<BackgammonState>> evs = getDefaultEVS();

            player = TwoPlyPlayer.<BackgammonState>builder()
                    .evs(evs)
                    .build();
        }

        // 初期局面の準備
        IndexedStateNode<BackgammonState, Roll> stateNode;
        {
            var initialNode = IndexedStateNode.initialNode(initialState, () -> roll);

            // initialNodeDecoratorの適用： growの対象となる子局面を生成しておく
            // （上記の()->rollの目を使って）
            stateNode = initialNode.grow(stateOperator.listupRolls(),
                    stateOperator::generateRoll,
                    stateOperator::buildNextState
            );
        }
        // BuildAheadなChildStatesProviderの適用： 各子局面について相手のレスポンスムーブを生成する
        Collection<IndexedStateNode<BackgammonState, Roll>> candidates = stateNode.getNextStates().stream()
                .map(node -> node.grow(
                        stateOperator.listupRolls(),
                        stateOperator::generateRoll,
                        stateOperator::buildNextState
                ))
                .collect(Collectors.toList());

        // 評価を行う
        NodesEvaluator<BackgammonState> ev = player.createEvaluators().gote();
        EvaluatedNode<IndexedStateNode<BackgammonState, Roll>> e = ev.evaluate(
                stateNode,
                candidates, handler);

        outputResult(e.getStateNode().getState(), e.getEval());

    }
}
