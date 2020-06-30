package com.github.tkshio.jgammon.gammon;

import java.util.function.Consumer;

/**
 * １ゲームの結果をシングル、ギャモン、バックギャモンで示す
 *
 * <p>どちらが勝ったかの情報は、含まれない
 */
public enum BackgammonResult {
    /**
     * ゲームは進行中である
     */
    InGame(0, false, Converter::inGame, Acceptor::inGame),
    /**
     * シングル勝ち
     */
    Single(1, true, Converter::single, Acceptor::single),
    /**
     * ギャモン勝ち
     */
    Gammon(2, true, Converter::gammon, Acceptor::gammon),
    /**
     * バックギャモン勝ち
     */
    Backgammon(3, true, Converter::backgammon, Acceptor::backgammon);

    private final int pt;
    private final boolean isOver;
    private final Consumer<Acceptor> switcher;
    private final Convert converter;

    BackgammonResult(int pt, boolean isOver, Convert converter, Consumer<Acceptor> switcher) {
        this.pt = pt;
        this.isOver = isOver;
        this.switcher = switcher;
        this.converter = converter;
    }


    /**
     * 勝ち点を得る
     *
     * @return 勝ち点
     */
    public int getPoint() {
        return pt;
    }

    /**
     * ゲームが終了状態かどうかを返す
     *
     * @return 終了状態であれば真
     */
    public boolean isOver() {
        return isOver;
    }

    /**
     * 結果に応じたオブジェクトを得る
     *
     * @param <T>       生成するオブジェクトのクラス
     * @param converter 生成するオブジェクトの定義
     * @return 生成されたオブジェクト
     */
    public <T> T convert(Converter<T> converter) {
        return this.converter.convert(converter);
    }

    /**
     * 結果に応じた処理を実行する
     *
     * @param acceptor 実行する処理の定義
     */
    public void accept(Acceptor acceptor) {
        switcher.accept(acceptor);
    }

    // Converterを呼び出すインターフェース
    // Converterの型変数<T>を扱うための定義
    private interface Convert {
        <T> T convert(Converter<T> f);
    }

    /**
     * {@link BackgammonResult}に応じたオブジェクトを生成させるためのインターフェース
     *
     * @param <T> 生成させたいオブジェクトの型
     */
    public interface Converter<T> {
        T inGame();

        T single();

        T gammon();

        T backgammon();
    }

    /**
     * {@link BackgammonResult}に応じた処理を実行させるためのインターフェース
     */
    public interface Acceptor {
        default void inGame() {
        }

        default void single() {
        }

        default void gammon() {
        }

        default void backgammon() {
        }
    }
}
