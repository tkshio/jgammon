package com.github.tkshio.jgammon.app;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Parameters(
        commandNames = {"train"},
        commandDescription = "Train Temporal-Difference gammon Network"
)
class TrainCommand {
    @ParametersDelegate
    CommonArgs commonArgs = new CommonArgs();
    @ParametersDelegate
    TrainCommandArgs trainingArgs = new TrainCommandArgs();

    @Parameter(
            description = "[num of games for TRAINING] [num of games for DETAILED output] (TRAINING DETAILED ...)"
    )
    List<Integer> program = new ArrayList<>(Arrays.asList(0, 3));

    public void validate() {
        commonArgs.validate();
        trainingArgs.validate();
    }
}
