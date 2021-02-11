package com.github.tkshio.jgammon.common.evaluator;

import com.github.tkshio.jgammon.common.director.Player;

public interface PlayerBuilder<STATE> {
    Player<STATE> build();

    PlayerBuilder<STATE> name(String name);
}
