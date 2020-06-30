package com.github.tkshio.jgammon.gammon.utils;

import com.github.tkshio.jgammon.gammon.move.Roll;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class TestListupRolls {
    @Test
    /*
     * 可能なロールをすべて列挙する
     */
    public void listupAllRolls() {
        Roll[] rolls = RollUtils.listupAllRolls(6).toArray(new Roll[]{});
        // 確認のためソート
        Arrays.sort(rolls, (r1, r2) -> Integer.signum((r2.getHigherNumber() * 6 + r2.getLowerNumber()) - (r1.getHigherNumber() * 6 + r1.getLowerNumber())));
        assertEquals(21, rolls.length);
        int[][] answer = {
                {6, 6}, {6, 5}, {6, 4}, {6, 3}, {6, 2}, {6, 1},
                {5, 5}, {5, 4}, {5, 3}, {5, 2}, {5, 1},
                {4, 4}, {4, 3}, {4, 2}, {4, 1},
                {3, 3}, {3, 2}, {3, 1},
                {2, 2}, {2, 1},
                {1, 1}};
        for (int i = 0; i < 21; i++) {
            assertEquals("#" + i, answer[i][0], rolls[i].getHigherNumber());
            assertEquals("#" + i, answer[i][1], rolls[i].getLowerNumber());
        }
    }

    /*
     * ミニギャモンの場合
     */
    @Test
    public void listupAllRollsForMiniGammon() {
        Roll[] rolls = RollUtils.listupAllRolls(3).toArray(new Roll[]{});

        Arrays.sort(rolls, (r1, r2) -> Integer.signum(r2.getHigherNumber() * 3 + r2.getLowerNumber() - (r1.getHigherNumber() * 3 + r1.getLowerNumber())));
        assertEquals(6, rolls.length);
        int[][] answer = {
                {3, 3}, {3, 2}, {3, 1},
                {2, 2}, {2, 1},
                {1, 1}};
        for (int i = 0; i < 6; i++) {
            assertEquals("#" + i, answer[i][0], rolls[i].getHigherNumber());
            assertEquals("#" + i, answer[i][1], rolls[i].getLowerNumber());
        }
    }

    @Test
    public void listupOpenings() {
        Roll[] rolls = RollUtils.listupOpeningRolls(3).toArray(new Roll[]{});

        Arrays.sort(rolls, (r1, r2) -> Integer.signum(r2.getHigherNumber() * 3 + r2.getLowerNumber() - (r1.getHigherNumber() * 3 + r1.getLowerNumber())));
        assertEquals(3, rolls.length);
        int[][] answer = {
                {3, 2}, {3, 1},
                {2, 1},
        };
        for (int i = 0; i < 3; i++) {
            assertEquals("#" + i, answer[i][0], rolls[i].getHigherNumber());
            assertEquals("#" + i, answer[i][1], rolls[i].getLowerNumber());
        }

    }
}
