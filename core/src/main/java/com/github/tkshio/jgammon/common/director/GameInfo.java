package com.github.tkshio.jgammon.common.director;

import com.github.tkshio.jgammon.common.utils.RWTuple;
import lombok.Data;

/**
 * 対局者及び獲得ポイントの累計
 */
@Data
public class GameInfo {
    private final int gameNo;
    private final RWTuple<Integer> points;
    private final RWTuple<String> players;
}
