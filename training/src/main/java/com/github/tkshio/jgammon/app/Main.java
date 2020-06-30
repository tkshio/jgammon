package com.github.tkshio.jgammon.app;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import java.io.IOException;

public class Main {
    private final RunCommand runCmd;
    private final TrainCommand trainCmd;
    private final JCommander cmd;

    Main() {
        runCmd = new RunCommand();
        trainCmd = new TrainCommand();
        cmd = JCommander.newBuilder()
                .addCommand(trainCmd)
                .addCommand(runCmd)
                .build();
    }

    public static void main(String... args) {
        try {
            parseAndRun(args);
        } catch (ParameterException e) {
            System.err.println(e.getMessage());
            new Main().cmd.usage();
        }
    }


    public static void parseAndRun(String... args) {
        Main main = new Main();
        main.parse(args);
        main.run();
    }

    private static void runAutoGame(RunCommand cmd) {
        try {
            Run.execute(cmd);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void runTraining(TrainCommand cmd) {
        try {
            Train.execute(cmd);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parse(String... args) {
        cmd.parse(args);
    }

    void run() {
        var parsedCmdStr = cmd.getParsedCommand();
        if (parsedCmdStr == null || parsedCmdStr.equals("run")) {
            runCmd.validateArgs();
            runAutoGame(runCmd);
        } else if (parsedCmdStr.equals("train")) {
            trainCmd.validate();
            runTraining(trainCmd);
        } else {
            cmd.usage();
        }
    }

}
