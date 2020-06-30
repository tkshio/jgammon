package com.github.tkshio.jgammon.gammon.utils;

import com.github.tkshio.jgammon.gammon.AbsoluteBackgammonBoard;

import java.util.function.Consumer;

/**
 * 指定された盤面をテキスト出力する
 *
 * <p>整形作業は{@link BoardFormatter}が担当する
 */
public class BackgammonBoardPrinter {

    /**
     * 任意のテキストを指定のメソッドに引き渡す
     *
     * @param printer 引き渡し先
     * @param strings 対象テキスト
     */
    public static void println(Consumer<String> printer, String... strings) {
        for (var s : strings) {
            printer.accept(s);
        }
    }

    /**
     * 任意のテキストを標準出力に送る
     *
     * @param strings 対象テキスト
     */
    public static void println(String... strings) {
        println(System.out::println, strings);
    }

    /**
     * 指定された盤面をテキストとして整形し、指定のメソッドに渡す
     * <p>テキストには、printer側で改行記号を追加する想定となっている
     *
     * @param printer 出力先にテキストを渡すメソッド
     * @param board   対象盤面
     */
    public static void print(Consumer<String> printer, AbsoluteBackgammonBoard board) {
        BoardFormatter.doPrint(printer, board);
    }

    /**
     * 指定された盤面をテキスト化して標準出力に出力する
     *
     * @param board 対象盤面
     */
    public static void print(AbsoluteBackgammonBoard board) {
        print(System.out::println, board);
    }


}
