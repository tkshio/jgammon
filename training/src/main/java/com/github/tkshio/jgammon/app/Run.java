package com.github.tkshio.jgammon.app;

import com.github.tkshio.jgammon.common.director.Player;
import com.github.tkshio.jgammon.gammon.BackgammonState;
import com.github.tkshio.jgammon.gammon.director.BackgammonAutoPlay;
import com.github.tkshio.jgammon.gammon.director.BackgammonDirectorConf;

import java.io.IOException;
import java.util.List;

public class Run {
    private final List<Integer> program;
    private final BackgammonAutoPlay autoPlay;

    public Run(RunCommand cmd) throws IOException {
        Player<BackgammonState> white = cmd.autoRunArgs.getWhitePlayer();
        Player<BackgammonState> red = cmd.autoRunArgs.getRedPlayer();


        var dConf = cmd.autoRunArgs.is2PlyMode() ?
                BackgammonDirectorConf.twoPlyDirectorConf()
                : BackgammonDirectorConf.onePlyDirectorConf();

        autoPlay = cmd.commonArgs.autoPlayBuilder()
                .dConf(dConf)
                .white(white)
                .red(red)
                .build();

        program = cmd.program;
    }

    public static void execute(RunCommand cmd) throws IOException {
        Run run = new Run(cmd);
        run.execute();
    }

    public void execute() {
        int no = 1;
        for (int n : program) {
            autoPlay.run(no, n);
            no += n;
        }
    }
}
