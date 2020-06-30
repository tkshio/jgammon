package com.github.tkshio.jgammon.gammon;

import com.github.tkshio.jgammon.gammon.move.Move;

import java.util.Arrays;

/**
 * int配列により盤面状態を保持する、{@link BackgammonBoard}の下請けクラス
 */
class IntArrayBoard {
    private final int[] points;
    private final int BAR;

    private IntArrayBoard(int[] points) {
        this.points = points.clone();
        this.BAR = points.length - 1;
    }

    /**
     * ファクトリーメソッド
     *
     * @param initialArrangement 配置
     * @return 生成されたオブジェクト
     */
    static IntArrayBoard create(int[] initialArrangement) {
        return new IntArrayBoard(initialArrangement);
    }

    /**
     * 反転させた盤面を生成する
     *
     * @param base 対象盤面
     * @return 反転させた盤面
     */
    static IntArrayBoard revert(IntArrayBoard base) {
        int[] reverted = new int[base.points.length];
        Arrays.setAll(reverted,
                i -> -base.points[base.points.length - 1 - i]);
        return new IntArrayBoard(reverted);
    }

    /**
     * ムーブを順次適用した盤面を生成する
     *
     * @param base  対象盤面
     * @param moves 適用するムーブ、省略時は単純に同一盤面の複製となる
     * @return 適用後の盤面
     */
    static IntArrayBoard dupWithMoves(IntArrayBoard base, Move... moves) {
        int[] dupPoints = base.points.clone();
        var board = new IntArrayBoard(dupPoints);
        for (Move move : moves) {
            board.applyMove(move);
        }
        return board;
    }


    private void applyMove(Move move) {
        int fromPos = move.getFrom();
        int toPos = move.getTo();
        if (move.isBearOff()) {
            bearOff(fromPos);
        } else {
            if (move.isHit()) {
                hit(toPos);
            }
            move(fromPos, toPos);
        }

    }

    /**
     * 盤面を管理する配列のサイズを返す
     *
     * @return 26またはボードサイズに応じた値
     */
    int size() {
        return points.length;
    }

    /**
     * ベアオフを行う
     *
     * @param from 移動元
     */
    void bearOff(int from) {
        points[from]--;
    }

    /**
     * 駒を移動する
     *
     * @param from 移動元
     * @param to   移動先
     */
    void move(int from, int to) {
        points[from]--;
        points[to]++;

    }

    /**
     * 駒をヒットによりバーに移動させる
     *
     * @param pos ヒットされる駒の位置
     */
    void hit(int pos) {
        points[pos]++;
        points[BAR]--;

    }

    /**
     * 指定位置の駒数を返す
     *
     * @param pos 位置、0からsize()-1（いずれも含む）
     * @return 駒数、自駒は正整数で、相手駒は負整数となる
     */
    int getPointAt(int pos) {
        return points[pos];
    }
}
