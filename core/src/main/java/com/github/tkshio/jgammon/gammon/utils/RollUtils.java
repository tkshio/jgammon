package com.github.tkshio.jgammon.gammon.utils;

import com.github.tkshio.jgammon.gammon.move.Roll;

import java.util.ArrayList;
import java.util.Collection;

/**
 * さいころ２つのロールの組み合わせを列挙する
 */
public class RollUtils {
    /**
     * ゾロ目を含めて、さいころ２つのロールの組み合わせを列挙する
     *
     * @param m ロールの最大値
     * @return 列挙された組み合わせのコレクション
     */
    public static Collection<Roll> listupAllRolls(int m) {
        Collection<Roll> result = new ArrayList<>(m * (m + 1) / 2);
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= i; j++) {
                Roll roll = Roll.of(i, j);
                result.add(roll);
            }
        }
        return result;
    }

    /**
     * ゾロ目を含めずに、さいころ２つのロールの組み合わせを列挙する
     *
     * @param m ロールの最大値
     * @return 列挙された組み合わせのコレクション
     */
    public static Collection<Roll> listupOpeningRolls(int m) {
        Collection<Roll> result = new ArrayList<>(m * (m - 1) / 2);
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= i; j++) {
                if (i != j) {
                    Roll roll = Roll.of(i, j);
                    result.add(roll);
                }
            }
        }
        return result;
    }
}
