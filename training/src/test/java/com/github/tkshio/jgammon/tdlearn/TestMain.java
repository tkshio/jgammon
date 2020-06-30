package com.github.tkshio.jgammon.tdlearn;

import com.beust.jcommander.ParameterException;
import com.github.tkshio.jgammon.app.Main;
import org.junit.Test;

public class TestMain {
    @Test(expected = ParameterException.class)
    public void run_illegal_node() {
        Main.parseAndRun("train", "-n", "3");
    }

    @Test(expected = ParameterException.class)
    public void run_illegal_lambda() {
        Main.parseAndRun("train", "--lambda", "3.0");
    }

    @Test(expected = ParameterException.class)
    public void run_illegal_learning_rate() {
        Main.parseAndRun("train", "--learning_rate", "2.0");
    }

    @Test
    public void run_allows0_lambda() {
        Main.parseAndRun("train", "--lambda", "0.0", "1");
    }

    @Test
    public void run_allows0_learning_rate() {
        Main.parseAndRun("train", "--learning_rate", "0.0", "1");
    }

}
