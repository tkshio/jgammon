package com.github.tkshio.jgammon.app;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import java.io.File;

class TrainCommandArgs {
    @Parameter(
            names = "--in",
            description = "file for reading pre-trained TDNetwork"
    )
    File tdIn;


    @Parameter(
            names = "--out",
            description = "file for writing trained TDNetwork"
    )
    File tdOut;


    @Parameter(
            names = "--node",
            description = "node size for hidden layer"
    )
    int node = 80;


    @Parameter(
            names = "--lambda",
            description = "lambda value for TD-learning(0.0-1.0)",
            converter = com.beust.jcommander.converters.DoubleConverter.class
    )
    double lambda = 0.7;


    @Parameter(
            names = "--learning_rate",
            description = "learning_rate for TD-learning (0.0-1.0)",
            converter = com.beust.jcommander.converters.DoubleConverter.class
    )
    double learning_rate = 0.01;

    public void validate() {
        if (lambda < 0.0
                || 1.0 < lambda) {
            throw new ParameterException("lambda must be between 0.0 - 1.0");
        }
        if (learning_rate < 0.0
                || 1.0 < learning_rate) {
            throw new ParameterException("learning rate must be between 0.0 - 1.0");
        }
        if (node < 4) {
            throw new ParameterException("hidden node must be > 4");
        }
    }
}
