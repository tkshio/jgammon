package com.github.tkshio.jgammon.tdlearn.td;

import com.github.tkshio.jgammon.tdlearn.nntd.NNTDDelta;
import com.github.tkshio.jgammon.tdlearn.nntd.NNTDGradient;
import com.github.tkshio.jgammon.tdlearn.nntd.NNTDLFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

public class TestTDTrainer {

    // 全シーケンスからの学習と比較して、差分学習でも同じ学習が行われることを確認する
    @Test
    public void compareCalcMethods() {
        double[][] seqDataSet;

        seqDataSet = new double[100][];
        Random r = new Random(2L);
        Arrays.setAll(seqDataSet, i -> new double[]{
                r.nextGaussian(), r.nextGaussian(), r.nextGaussian()});

        double[] answer = new double[]{0.7, 0.25};

        int in = seqDataSet[0].length;
        int hidden = 2;
        int out = answer.length;

        var tdn = NNTDLFactory.buildFactory().create(in, hidden, out);
        _compareCalcMethods(tdn, seqDataSet, answer);
    }

    private <T> void _compareCalcMethods(TDLearner<NNTDDelta<T>, NNTDGradient<T>> tdn, double[][] seqDataSet, double[] answer) {
        double learningRate = 0.1;

        double lambda = 1.0;


        TDTrainerBuilder builder = TDTrainerBuilder.builder()
                .lambda(lambda)
                .learning_rate(learningRate);

        TDTrainerImpl td = builder.build();

        TDTrainerImpl tdByDiff = builder.build();

        {
            double[] output = tdn.eval(seqDataSet[0]).getOutput();
            System.out.println("BEFORE:" + Arrays.toString(output));
            double[] outputByDiff = tdn.eval(seqDataSet[0]).getOutput();
            // 初期値では、どちらも同じ結果を返さないといけない
            assertArrayEquals(output, outputByDiff, 0.1);
        }

        // 途中経過の各状態について、最終結果を指定して学習を行い、更新量の合計を得る
        NNTDDelta<T> sum = null;
        {
            for (double[] seqData : seqDataSet) {
                NNTDDelta<T> delta = td.calcDelta(tdn, seqData, answer);
                sum = (sum == null) ? delta : sum.add(delta);
            }
        }
        // 直前の状態から更新量を算出して、合計を得る
        NNTDDelta<T> sumByDiff = null;
        {
            var state = tdByDiff.initialState(tdn, seqDataSet[0]);
            for (int i = 1; i < seqDataSet.length; i++) {
                double[] seqData = seqDataSet[i];
                var eval = tdn.eval(seqData);
                state = tdByDiff.calcNextState(state, eval);
                var lastDelta = tdByDiff.calcDelta(state, eval.getOutput());
                sumByDiff = (sumByDiff == null) ? lastDelta : sumByDiff.add(lastDelta);
            }
            assertNotNull(sumByDiff);
            var delta = tdByDiff.calcDelta(state, answer);
            sumByDiff = sumByDiff.add(delta);
        }
        // データセットの先頭10個を使って、両者を比較
        for (int i = 0; i < 10; i++) {
            double[] output = tdn.eval(seqDataSet[i]).getOutput();
            System.out.println("AFTER(base  ):" + Arrays.toString(output));
            double[] outputByDiff = tdn.eval(seqDataSet[i]).getOutput();
            System.out.println("AFTER(byDiff):" + Arrays.toString(outputByDiff));

            // 大体一緒ならよい
            assertArrayEquals(output, outputByDiff, 0.1);
        }

        // ネットワークの更新量について、両者を比較する
        Assert.assertArrayEquals(sum.getOutput().getWeight().getData()[0], sumByDiff.getOutput().getWeight().getData()[0], 0.001);
        Assert.assertArrayEquals(sum.getHidden().getWeight().getData()[0], sumByDiff.getHidden().getWeight().getData()[0], 0.001);
        Assert.assertArrayEquals(sum.getOutput().getBias().getData()[0], sumByDiff.getOutput().getBias().getData()[0], 0.1);
        Assert.assertArrayEquals(sum.getHidden().getBias().getData()[0], sumByDiff.getHidden().getBias().getData()[0], 0.1);
    }


    // どんな入力に対しても、固定値に近づくように学習させる
    @Test
    public void learnEverythingAsFixedValue() {
        int len = 10;
        var tdn = NNTDLFactory.buildFactory().create(10, 40, 4);
        TDTrainer td = TDTrainerBuilder.builder().build();
        double[] reward = new double[]{0.1, 0.2, 0.3, 0.4};
        Random random = new Random();
        for (int n = 0; n < 5000; n++) {
            double[][] game = new double[10][];
            Arrays.setAll(game, i -> {
                double[] points = new double[len];
                Arrays.setAll(points, j -> random.nextGaussian());
                return points;
            });
            runLearning(tdn, td, game, reward);
        }
        double[] points = new double[len];
        Arrays.setAll(points, j -> random.nextGaussian());
        System.out.println(Arrays.toString(tdn.eval(points).getOutput()));
        Assert.assertArrayEquals(reward, tdn.eval(points).getOutput(), 0.1);
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

}
