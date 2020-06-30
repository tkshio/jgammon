package com.github.tkshio.jgammon.gammon.move;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 駒１つの動きを示す
 */
@Data
@EqualsAndHashCode
public class Move {
    private final int from;
    private final int to;
    private final boolean isHit;

    /**
     * ファクトリーメソッド
     * <p>
     * {@literal from < to} でなくてはいけない（ベアリングオフを除く）
     *
     * @param from 駒の移動元
     * @param to   駒の移動先
     * @return 生成されたMoveオブジェクト
     */
    public static Move of(int from, int to) {
        if (to != -1 && from >= to) {
            throw new IllegalArgumentException();
        }
        return Move.of(from, to, false);
    }

    /**
     * ヒットの有無を指定できるファクトリーメソッド
     *
     * @param from  駒の移動元
     * @param to    駒の移動先
     * @param isHit ヒットの場合はtrue
     * @return 生成されたMoveオブジェクト
     */
    public static Move of(int from, int to, boolean isHit) {
        return new Move(from, to, isHit);
    }

    /**
     * ベアリングオフのファクトリーメソッド
     *
     * @param pos 駒の移動元
     * @return 生成されたMoveオブジェクト
     */
    public static Move bearOff(int pos) {
        return new Move(pos, -1, false);
    }

    /**
     * ベアリングオフかどうかを返す
     *
     * @return ベアリングオフの場合は真
     */
    public boolean isBearOff() {
        return to == -1;
    }

    @Override
    public String toString() {
        return formatInAsc();
    }

    /**
     * {@literal 24->0}の向きのムーブとしてテキスト化する
     *
     * @return 内容を示すテキスト
     */
    public String formatInDesc() {
        return String.format("%s/%s%s",
                ((from == 0) ? "BAR" : String.valueOf(25 - from)),
                ((to == 0) ? "BAR" : (to == -1) ? "OFF" : String.valueOf(25 - to)),
                ((isHit) ? "*" : ""));

    }

    /**
     * {@literal 0->24}の向きのムーブとしてテキスト化する
     *
     * @return 内容を示すテキスト
     */
    public String formatInAsc() {
        return String.format("%s/%s%s",
                ((from == 0) ? "BAR" : String.valueOf(from)),
                ((to == 0) ? "BAR" : (to == -1) ? "OFF" : String.valueOf(to)),
                ((isHit) ? "*" : ""));
    }

}
