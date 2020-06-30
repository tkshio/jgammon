package com.github.tkshio.jgammon.app;

import com.github.tkshio.jgammon.common.evaluator.OnePlyPlayer;
import com.github.tkshio.jgammon.gammon.BackgammonState;
import com.github.tkshio.jgammon.gammon.director.BackgammonAutoPlay;
import com.github.tkshio.jgammon.tdlearn.TDLearnEvaluator;
import com.github.tkshio.jgammon.tdlearn.TDNetworkWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static com.github.tkshio.jgammon.tdlearn.TDLearnEvaluatorReader.readAsTrainableEv;

public class Train {

    private final List<Integer> program;
    private final BackgammonAutoPlay autoPlay;
    private final BackgammonAutoPlay sampleAutoPlay;
    private final TDNetworkWriter writer;
    private final File outFile;

    Train(TrainCommand cmd) throws IOException {

        this.program = cmd.program;

        final OnePlyPlayer.OnePlyPlayerBuilder<BackgammonState> playerBuilder;

        // 訓練されるプレイヤーの準備
        {
            // 学習パラメーターの設定
            int hiddenNodes = cmd.trainingArgs.node;
            double lambda = cmd.trainingArgs.lambda;
            double learning_rate = cmd.trainingArgs.learning_rate;
            long seed = cmd.commonArgs.seed;
            Random r = new Random(seed);
            var conf = cmd.commonArgs.tdConfBuilder()
                    .hiddenNodes(hiddenNodes)
                    .lambda(lambda)
                    .learning_rate(learning_rate)
                    .initializer(() -> r.nextGaussian() * 0.01)
                    .build();

            // TDLearnの準備
            TDLearnEvaluator<BackgammonState> evaluators;
            {
                if (cmd.trainingArgs.tdIn != null) {
                    File file = cmd.trainingArgs.tdIn;

                    try (BufferedReader reader =
                                 new BufferedReader(
                                         new FileReader(file))) {
                        evaluators = readAsTrainableEv(conf, reader);
                    }
                } else {
                    evaluators = TDLearnEvaluator.create(conf);
                }
            }

            // プレイヤーの作成
            var evs = evaluators.getEvs();
            playerBuilder = OnePlyPlayer.<BackgammonState>builder()
                    .evs(evs);

            // 出力系の作成
            writer = TDNetworkWriter.create(evaluators.getTDNetworkInternal());
            outFile = cmd.trainingArgs.tdOut;
        }

        // 実行オブジェクトの構築
        {
            String nameWhite = cmd.commonArgs.getWhitePlayerName();
            String nameRed = cmd.commonArgs.getRedPlayerName();

            var whitePlayer = playerBuilder.name(nameWhite).build();
            var redPlayer = playerBuilder.name(nameRed).build();

            var minimalLogger = cmd.commonArgs.getMinimalBGLogger();

            autoPlay = cmd.commonArgs.autoPlayBuilder()
                    .white(whitePlayer)
                    .red(redPlayer)
                    .contextHandler(minimalLogger)
                    .build();


            sampleAutoPlay = cmd.commonArgs.autoPlayBuilder()
                    .white(whitePlayer)
                    .red(redPlayer)
                    .build();
        }


    }

    static void execute(TrainCommand cmd) throws IOException {
        Train train = new Train(cmd);
        train.execute();
    }

    public static File rename(File file, int no) {
        return new File(file.getParent(),
                file.getName().replaceFirst("\\.(.*$)",
                        String.format("_%d.$1", no)));
    }

    void execute() throws IOException {
        Iterator<Integer> is = program.iterator();

        int sessionNo = 0, no = 1, sampleNo = 1;
        while (is.hasNext()) {
            int n = is.next();
            autoPlay.run(no, n);
            no += n;
            if (outFile != null) {
                File file;
                if (sessionNo != 0) {
                    file = rename(outFile, sessionNo);
                } else {
                    file = outFile;
                }
                writer.write(file);
            }
            if (is.hasNext()) {
                int sample = is.next();
                sampleAutoPlay.run(sampleNo, sample);
                sampleNo += sample;
            } else {
                break;
            }

            sessionNo++;
        }
    }
}
