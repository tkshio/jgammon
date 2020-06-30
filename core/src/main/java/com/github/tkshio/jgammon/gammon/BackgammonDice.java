package com.github.tkshio.jgammon.gammon;

import com.github.tkshio.jgammon.common.dice.Dice;
import com.github.tkshio.jgammon.common.dice.RandomDice;
import com.github.tkshio.jgammon.gammon.move.Roll;
import com.github.tkshio.jgammon.gammon.utils.RollUtils;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * バックギャモンに固有のダイス関連機能を提供する
 */
public interface BackgammonDice {

    /**
     * ファクトリーメソッド
     *
     * <p> {@link com.github.tkshio.jgammon.common.dice.RandomDice RandomDice}による、普通の６面ダイスを使用する
     *
     * @return 生成されたBackgammonDiceオブジェクト
     */
    static BackgammonDice create() {
        Dice aDice = RandomDice.create(6);
        return BackgammonDice.create(aDice);
    }

    /**
     * ファクトリーメソッド
     *
     * <p>ダイスを指定することにより、出目を決定するアルゴリズムを任意に定めることができる。
     *
     * @param dice 使用するダイス
     * @return 生成されたBackgammonDice
     */
    static BackgammonDice create(Dice dice) {
        Collection<Roll> listedUp = RollUtils.listupAllRolls(dice.size());
        return new BackgammonDice() {
            @Override
            public OpeningRoll rollOpening() {
                dice.reset();
                return BackgammonDice.rollOpeningWithDice(dice::roll);
            }

            @Override
            public Collection<Roll> listupRolls() {
                return listedUp;
            }

            @Override
            public Roll roll() {
                return Roll.of(dice.roll(), dice.roll());
            }
        };
    }

    /**
     * オープニングロールを生成する
     *
     * @param dice ロール目を生成するメソッド
     * @return 生成されたオープニングロール
     */
    static OpeningRoll rollOpeningWithDice(Supplier<Integer> dice) {
        int _r1, _r2;
        int count = 0;
        do {
            _r1 = dice.get();
            _r2 = dice.get();
            count++;
            if (count > 1000) {
                throw new IllegalStateException();
            }
        } while (_r1 == _r2);

        int r1 = _r1;
        int r2 = _r2;

        return new OpeningRoll() {
            final Roll roll = Roll.of(r1, r2);
            final boolean firstDiceIsHigher = r1 > r2;

            @Override
            public Roll asRoll() {
                return roll;
            }

            @Override
            public boolean firstDiceIsHigher() {
                return firstDiceIsHigher;
            }
        };
    }

    /**
     * オープニングロールを生成する
     *
     * @return 生成されたOpeningRollオブジェクト
     */
    OpeningRoll rollOpening();

    /**
     * このダイスが出しうる、可能な目のリストを提供する
     *
     * @return 可能な目のリスト
     */
    Collection<Roll> listupRolls();

    /**
     * ロールする
     *
     * @return 得られたロール
     */
    Roll roll();

    /**
     * オープニングロールのインターフェース
     */
    interface OpeningRoll {
        /**
         * Rollオブジェクトとしてロールを得る
         *
         * @return Rollオブジェクト
         */
        Roll asRoll();

        /**
         * １つ目のダイスが大きいかどうかを返す
         *
         * @return １つ目のダイスが大きければ真
         */
        boolean firstDiceIsHigher();
    }
}
