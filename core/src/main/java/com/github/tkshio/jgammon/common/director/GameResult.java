package com.github.tkshio.jgammon.common.director;


import java.util.function.Consumer;

/**
 * 対局結果、すなわち先手・後手のいずれが勝ったか（または引き分け・中断）を表す
 */
public enum GameResult {
    /**
     * 先手勝利
     */
    SENTE_WIN(Converter::senteWon, Acceptor::senteWon),
    /**
     * 後手勝利
     */
    GOTE_WIN(Converter::goteWon, Acceptor::goteWon),
    /**
     * 引き分け
     */
    DRAW(Converter::draw, Acceptor::draw),
    /**
     * 中断
     */
    ABORT(Converter::abort, Acceptor::abort);

    private final Convert converter;
    private final Consumer<Acceptor> accept;

    GameResult(Convert converter, Consumer<Acceptor> accept) {
        this.converter = converter;
        this.accept = accept;
    }

    /**
     * 対局結果に応じた任意のオブジェクトを生成する
     *
     * @param converter オブジェクトを生成する関数
     * @param <T>       生成されるオブジェクトのクラス
     * @return 生成されたオブジェクト
     */
    public <T> T convert(Converter<T> converter) {
        return this.converter.convert(converter);
    }

    /**
     * 対局結果に応じた任意の処理を実行する
     *
     * @param acceptor 実行する処理の定義
     */
    public void accept(Acceptor acceptor) {
        accept.accept(acceptor);
    }


    // Converterを呼び出すインターフェース
    // Converterの型変数<T>を扱うための定義
    private interface Convert {
        <T> T convert(Converter<T> f);
    }

    /**
     * 対局結果に応じたオブジェクトを生成するためのインターフェース
     */
    public interface Converter<T> {
        T senteWon();

        T goteWon();

        T draw();

        T abort();
    }

    /**
     * 対局結果に応じた処理を実行させるためのインターフェース
     */
    public interface Acceptor {
        default void senteWon() {
        }

        default void goteWon() {
        }

        default void draw() {
        }

        default void abort() {
        }
    }
}
