package com.github.tkshio.jgammon.gammon.director;

import com.github.tkshio.jgammon.common.evaluator.NodesEvaluator;
import com.github.tkshio.jgammon.common.utils.SGTuple;
import com.github.tkshio.jgammon.gammon.BackgammonState;
import org.junit.Test;

public class TestRunSelfPlayNTimes {
    SGTuple<NodesEvaluator<BackgammonState>> evs = TestBackgammonNodeDirector.evs;


    @Test
    public void run1PlyOnce() {

        BackgammonAutoPlay.builder().build().run(1);
    }

    @Test
    public void run1PlyThrice() {
        BackgammonAutoPlay.builder().build().run(3);
    }


}
