package com.github.tkshio.jgammon.app;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;

import java.util.ArrayList;
import java.util.List;

@Parameters(commandNames = "run")
class RunCommand {
    @ParametersDelegate
    final CommonArgs commonArgs = new CommonArgs();

    @ParametersDelegate
    final RunCommandArgs autoRunArgs = new RunCommandArgs();


    @Parameter(
            description = "NUM of Games [NUM ...]"
    )
    List<Integer> program = new ArrayList<>(List.of(10));
    // JCommander requires removable list

    public void validateArgs() {
        commonArgs.validate();
        autoRunArgs.validate();
    }
}
