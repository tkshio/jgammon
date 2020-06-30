package com.github.tkshio.jgammon.tdlearn.td;

import com.github.tkshio.jgammon.gammon.BGType;
import com.github.tkshio.jgammon.gammon.BackgammonBoard;
import com.github.tkshio.jgammon.tdlearn.bg.BGInputCodecs;
import com.github.tkshio.jgammon.tdlearn.nntd.NNTDLFactory;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.assertArrayEquals;

public class TestTDTrainerWithGammon {

    BackgammonBoard init = BackgammonBoard.create(BGType.standard, new int[]{
            0,
            0, 0, 0, -1, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 1, 0, 0,
            0
    });
    BackgammonBoard whiteMayWin = BackgammonBoard.create(BGType.standard, new int[]{
            0,
            0, 0, 0, 0, -1, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 1, 0, 0, 0, 0,
            0
    });
    BackgammonBoard redMayWin = BackgammonBoard.create(BGType.standard, new int[]{
            0,
            0, -1, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 1, 0,
            0
    });
    BackgammonBoard whiteHasWon = BackgammonBoard.create(BGType.standard, new int[]{
            0,
            0, 0, 0, -1, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            3
    });
    BackgammonBoard redHasWon = BackgammonBoard.create(BGType.standard, new int[]{
            3,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 1, 0, 0,
            0
    });

    // 3手で完結する局面に対する学習のテスト
    @Test
    public void basicUsage() {
        var tdn = NNTDLFactory.buildFactory().create(198, 40, 4);
        TDTrainer td = TDTrainerBuilder.builder()
                .lambda(1.0)
                .build();
        runBasicUsage(tdn, td);
    }

    // 同上、λを指定した場合
    @Test
    public void basicUsage_lambda07() {
        var tdn = NNTDLFactory.buildFactory().create(198, 40, 4);
        TDTrainer td = TDTrainerBuilder.builder()
                .lambda(0.7)
                .build();
        runBasicUsage(tdn, td);
    }

    private double[] eval(TDLearner<?, ?> tdn, BackgammonBoard board) {
        var encoded = BGInputCodecs.encodeBG(board);
        return tdn.eval(encoded).getOutput();
    }

    <D, G extends TDGradient<D, G>> void runBasicUsage(TDLearner<D, G> tdn, TDTrainer td) {
        BackgammonBoard[][] games = setup();
        double[][][] encoded = new double[games.length][][];
        for (int i = 0; i < encoded.length; i++) {
            encoded[i] = new double[games[i].length][];
            BackgammonBoard[] game = games[i];
            Arrays.setAll(encoded[i], j -> BGInputCodecs.encodeBG(game[j]));
        }

        double[][] reward = new double[][]{
                {1.0, 0, 0, 0},
                {1.0, 0, 0, 0},
                {0.0, 0, 1.0, 0},
                {0.0, 0, 1.0, 0},
                {0.0, 0, 1.0, 0},
                {1.0, 0, 0.0, 0},
        };
        Consumer<TDTrainer> dumpCurrent = _td -> {
            System.out.println();
            System.out.println("init" + Arrays.toString(eval(tdn, init)));
            System.out.println("whiteMayWin" + Arrays.toString(eval(tdn, whiteMayWin)));
            System.out.println("whiteHasWon" + Arrays.toString(eval(tdn, whiteHasWon)));
            System.out.println("redMayWin" + Arrays.toString(eval(tdn, redMayWin)));
            System.out.println("redHasWon" + Arrays.toString(eval(tdn, redHasWon)));
            System.out.println();
        };
        List<Integer> rtable = Arrays.asList(0, 1, 2, 3, 4, 5);
        for (int k = 0; k < 10; k++) {
            System.out.println(k);
            dumpCurrent.accept(td);
            for (int i = 0; i < 504; i++) {
                // サンプルの偏りの影響が大きいので、均等に
                Collections.shuffle(rtable);
                for (int j = 0; j < games.length; j++) {
                    runLearning(tdn, td, encoded[j], reward[j]);
                }
            }
        }
        dumpCurrent.accept(td);
        assertArrayEquals(new double[]{0.67, 0.0, 0.34, 0.0}, eval(tdn, whiteMayWin), 0.1);
        assertArrayEquals(new double[]{1.0, 0.0, 0.0, 0.0}, eval(tdn, whiteHasWon), 0.1);
        assertArrayEquals(new double[]{0.34, 0.0, 0.67, 0.0}, eval(tdn, redMayWin), 0.1);
        assertArrayEquals(new double[]{0.0, 0.0, 1.0, 0.0}, eval(tdn, redHasWon), 0.1);

    }

    public <D, G extends TDGradient<D, G>> void runLearning(TDLearner<D, G> tdn, TDTrainer td, double[][] boards, double[] rewards) {
        double[] init = boards[0];

        TDLearningState<G> state = td.initialState(tdn, init);
        for (int i = 1; i < boards.length; i++) {
            double[] values = boards[i];
            state = td.learn(tdn, state, tdn.eval(values));
        }

        td.commitWithReward(tdn, state, rewards);
    }

    // 三手で終了する対局データを構成
    // 初期局面は50/50、次の局面は33/66、最後の局面は勝敗が確定しているようにデータを構成する
    private BackgammonBoard[][] setup() {
        var games = new BackgammonBoard[6][];


        games[0] = new BackgammonBoard[]{
                init, whiteMayWin, whiteHasWon
        };

        games[1] = new BackgammonBoard[]{
                init, whiteMayWin, whiteHasWon
        };

        games[2] = new BackgammonBoard[]{
                init, whiteMayWin, redHasWon
        };

        games[3] = new BackgammonBoard[]{
                init, redMayWin, redHasWon
        };

        games[4] = new BackgammonBoard[]{
                init, redMayWin, redHasWon
        };

        games[5] = new BackgammonBoard[]{
                init, redMayWin, whiteHasWon
        };

        return games;
    }
}
