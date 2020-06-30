package com.github.tkshio.jgammon.app;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.github.tkshio.jgammon.DefaultTDReader;
import com.github.tkshio.jgammon.common.director.Player;
import com.github.tkshio.jgammon.common.evaluator.OnePlyPlayer;
import com.github.tkshio.jgammon.common.evaluator.TwoPlyPlayer;
import com.github.tkshio.jgammon.common.utils.SGTuple;
import com.github.tkshio.jgammon.gammon.BackgammonState;
import com.github.tkshio.jgammon.tdlearn.TDLearnEvaluatorReader;
import com.github.tkshio.jgammon.tdlearn.bg.BGConf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RunCommandArgs {
    @Parameter(names = "--alg",
            arity = 2,
            description = "Algorithms for player \"O\" and \"X\"(\"td(:filename)\"or \"random\""
    )
    List<String> evs = new ArrayList<>(List.of("td", "td"));
    // JCommander requires removable list

    @Parameter(names = "--depth",
            arity = 2,
            description = "Set 1 for 1ply evaluation or 2 for 2ply evaluation"
    )
    List<String> depthStr = new ArrayList<>(List.of("1", "1"));


    Player<BackgammonState> getWhitePlayer() throws IOException {
        return buildEv(evs.get(0), depthStr.get(0));
    }

    Player<BackgammonState> getRedPlayer() throws IOException {
        return buildEv(evs.get(1), depthStr.get(1));
    }

    boolean is2PlyMode() {
        return depthStr.get(0).equalsIgnoreCase("2")
                || depthStr.get(1).equalsIgnoreCase("2");
    }

    private Player<BackgammonState> buildEv(String name, String d) throws IOException {
        if (name.toLowerCase().startsWith("random")) {
            return buildRandomPlayer();
        } else if (name.toLowerCase().startsWith("td")) {

            String filename = name.length() < 3 ?
                    "" : name.substring(3);

            int depth = Integer.parseInt(d);
            if (filename.isEmpty()) {
                try (
                        BufferedReader bufferedReader =
                                DefaultTDReader.getDefaultTDReader()) {
                    return buildPlayerFromFile(bufferedReader, depth);
                }

            } else {
                File file = new File(filename);
                if (!file.exists()) {
                    throw new IOException(MessageFormat.format(
                            "File {0} not found.", file.toString()));
                }

                try (BufferedReader bufferedReader =
                             new BufferedReader(new FileReader(file))) {
                    return buildPlayerFromFile(bufferedReader, depth);
                }
            }
        } else {
            // 引数はチェック済なので
            throw new IllegalStateException();
        }

    }

    private Player<BackgammonState> buildPlayerFromFile(BufferedReader reader,
                                                        int depth) throws IOException {
        var conf = BGConf.builder().build();

        var evs = TDLearnEvaluatorReader.readAsStableEv(conf, reader);

        Player<BackgammonState> player;
        if (depth == 1) {
            player = OnePlyPlayer.<BackgammonState>builder()
                    .evs(evs)
                    .name("TDLearn(1ply)")
                    .build();
        } else {
            player = TwoPlyPlayer.<BackgammonState>builder()
                    .evs(evs)
                    .name("TDLearn(2ply)")
                    .build();
        }
        return player;


    }

    private Player<BackgammonState> buildRandomPlayer() {
        Random r = new Random();
        return OnePlyPlayer.<BackgammonState>builder()
                .evs(SGTuple.of(backgammonState -> r::nextGaussian))
                .name("Random")
                .build();
    }


    void validate() {
        for (String evName : evs) {
            if (!evName.toLowerCase().startsWith("random")
                    && !evName.toLowerCase().startsWith("td")
            ) {
                throw new ParameterException(MessageFormat.format("Unexpected value \"{0}\" in Algorithms(--alg)", evName));
            }
        }

        for (String dStr : depthStr) {
            var d = Integer.parseInt(dStr);
            if (d != 1 && d != 2) {
                throw new ParameterException("Depth must be 1 or 2");
            }
        }
    }
}
