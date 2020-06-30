package com.github.tkshio.jgammon.app;

import com.beust.jcommander.Parameter;
import com.github.tkshio.jgammon.common.context.BundledGameContextHandler;
import com.github.tkshio.jgammon.common.dice.RandomDice;
import com.github.tkshio.jgammon.common.utils.Memoizer;
import com.github.tkshio.jgammon.gammon.BackgammonDice;
import com.github.tkshio.jgammon.gammon.BackgammonState;
import com.github.tkshio.jgammon.gammon.context.MinimalBGLogger;
import com.github.tkshio.jgammon.gammon.context.SimpleBGLogger;
import com.github.tkshio.jgammon.gammon.context.XGExportHandler;
import com.github.tkshio.jgammon.gammon.director.BackgammonAutoPlay;
import com.github.tkshio.jgammon.tdlearn.TDConf;
import com.github.tkshio.jgammon.tdlearn.bg.BGConf;
import com.github.tkshio.jgammon.tdlearn.matrix.ApacheMath3MatrixFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

class CommonArgs {
    private static final long DEFAULT_SEED = -1;
    private final static Logger logger = Logger.getGlobal();
    // JCommander requires removable list

    static {
        try {
            LogManager.getLogManager().readConfiguration(
                    Train.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Parameter(
            names = "--names",
            arity = 2,
            description = "names for player \"O\" and \"X\""
    )
    List<String> names = new ArrayList<>(List.of("white", "red"));
    @Parameter(
            names = "--xg",
            description = "output xg file for sample games"
    )
    File xgfile;

    @Parameter(
            names = "--max",
            description = "max turns(1turn = 2ply) for each game"
    )
    int maxTurns = 200;

    /*
    @Parameter(
            names = "--nd4j",
            description = "use ND4J library for matrix calculation"
    )
    boolean useND4J = false;
     */
    @Parameter(
            names = "--seed",
            description = "random seed value for Dice and Matrix initialization"
    )
    long seed = DEFAULT_SEED;

    @Parameter(
            names = {"-l", "--log"},
            description = "log file"
    )
    File logfile;

    @Parameter(
            names = {"-v", "--v"},
            description = "verbose (show result of each game)"
    )
    boolean verbose;

    Supplier<MinimalBGLogger> minimalLogger = Memoizer.memoize(
            () -> {
                MinimalBGLogger minimalLogger;
                if (verbose) {
                    minimalLogger = MinimalBGLogger.create(System.out::println, System.err::println);
                } else {
                    minimalLogger = MinimalBGLogger.create(System.out::println);
                }

                return minimalLogger;
            }
    );
    Supplier<BundledGameContextHandler.BundledGameContextHandlerBuilder<BackgammonState>> bgLogger =
            Memoizer.memoize(() -> {
                try {
                    var loggerBuilder = BundledGameContextHandler
                            .<BackgammonState>builder();
                    {
                        var simpleLogger = SimpleBGLogger.create(logger::info);
                        if (logfile != null) {
                            setLogfile(logfile);
                        }
                        loggerBuilder.handler(simpleLogger);

                        XGExportHandler xgExporter;
                        if (xgfile != null) {
                            var xgOut = new PrintStream(xgfile);
                            xgExporter = XGExportHandler.create(xgOut::println);
                            loggerBuilder.handler(xgExporter)
                            ;
                        }
                    }

                    loggerBuilder.handler(minimalLogger.get());
                    return loggerBuilder;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

    static void setLogfile(File file) throws IOException {
        FileHandler fileHandler = new FileHandler(file.getAbsolutePath(), false);
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);
        logger.setUseParentHandlers(false);
    }

    public String getWhitePlayerName() {
        return names.get(0);
    }

    public String getRedPlayerName() {
        return names.get(1);
    }

    MinimalBGLogger getMinimalBGLogger() {
        return minimalLogger.get();
    }

    public void validate() {
    }

    public BackgammonAutoPlay.BackgammonAutoPlayBuilder autoPlayBuilder() {
        if (seed == DEFAULT_SEED) { // デフォルト値なら、適当な値に差し替える
            seed = new Random().nextLong();
        }
        logger.info("Seed = " + seed);
        return BackgammonAutoPlay.builder()
                .maxTurn(maxTurns)
                .bgDice(BackgammonDice.create(RandomDice.create(6, seed)))
                .contextHandler(bgLogger.get().build())
                ;
    }

    public TDConf.TDConfBuilder<BackgammonState> tdConfBuilder() {
        return BGConf.builder()
                .matrixFactory(/*
                        useND4J ?
                        new ND4JMatrixFactory() :*/
                        new ApacheMath3MatrixFactory());
    }
}
