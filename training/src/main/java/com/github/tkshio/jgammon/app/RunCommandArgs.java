package com.github.tkshio.jgammon.app;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.github.tkshio.jgammon.TDPlayerBuilder;
import com.github.tkshio.jgammon.common.director.Player;
import com.github.tkshio.jgammon.common.evaluator.OnePlyPlayer;
import com.github.tkshio.jgammon.common.utils.SGTuple;
import com.github.tkshio.jgammon.gammon.BackgammonState;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
                return TDPlayerBuilder.defaultPlayerBuilder(depth).build();
            } else {
                File file = new File(filename);
                if (!file.exists()) {
                    throw new IOException(MessageFormat.format(
                            "File {0} not found.", file.toString()));
                }

                try (InputStream is = new FileInputStream(file)) {
                    return TDPlayerBuilder.playerBuilderWithInputStream(is, depth).build();
                }
            }
        } else {
            // 引数はチェック済なので
            throw new IllegalStateException();
        }

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
