package com.github.tkshio.jgammon.gammon.utils;

import com.github.tkshio.jgammon.gammon.AbsoluteBackgammonBoard;

import java.util.AbstractList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 盤面をテキスト出力する
 */
class BoardFormatter {
    private static final String RED_PIECE = " X ";
    private static final String WHITE_PIECE = " O ";

    /**
     * 指定された盤面をテキスト出力する
     * <p>テキストには、printer側で改行記号を追加する想定となっている
     *
     * @param printer 出力先にテキストを渡すメソッド
     * @param board   対象盤面
     */
    static void doPrint(Consumer<String> printer, AbsoluteBackgammonBoard board) {
        List<Integer> points = new AbstractList<>() {
            @Override
            public Integer get(int index) {
                return board.getPointAt(index);
            }

            @Override
            public int size() {
                return board.size();
            }
        };
        int hwidth = board.getPointsCount() / 2;
        Supplier<IntStream> linesUpper = () -> IntStream.range(0, 5);
        Supplier<IntStream> oBoard = () -> IntStream.range(1, hwidth + 1);
        Supplier<IntStream> iBoard = () -> oBoard.get().map(i -> hwidth * 2 + 1 - i);
        Supplier<IntStream> linesLower = () -> linesUpper.get().map(i -> 4 - i);
        String spaces = "                     ".substring(0, 3 * hwidth / 2);
        Stream<Consumer<Consumer<String>>> upper;
        {
            var pieceRendererStream
                    = linesUpper.get().mapToObj(
                    line -> doRenderPiece(line).apply(points));
            upper = Stream.of(
                    Stream.of(
                            doRenderRawVals(points),
                            doRenderLabel()
                    ),
                    pieceRendererStream
            )
                    .flatMap(s -> s)
                    .map(f -> f.apply(oBoard).apply(hwidth / 2)
                    );
        }

        Stream<Consumer<Consumer<String>>> bar = Stream.of(
                (Consumer<String> p) ->
                        p.accept(String.format("v|%s|BAR|%s|", spaces, spaces)));


        Stream<Consumer<Consumer<String>>> lower;
        {
            var pieceRendererStream = linesLower.get().mapToObj(line -> doRenderPiece(line).apply(points));
            lower =
                    Stream.of(
                            pieceRendererStream,
                            Stream.of(
                                    doRenderLabel(),
                                    doRenderRawVals(points)))
                            .flatMap(s -> s)
                            .map(f -> f.apply(iBoard).apply(hwidth * 2 + 1 - (hwidth / 2))
                            );
        }

        Stream<Consumer<Consumer<String>>> footer = Stream.of((Consumer<String> p) -> p.accept(""));

        Stream.of(upper, bar, lower, footer).flatMap(s -> s).forEach(f -> f.accept(s -> {
            BackgammonBoardPrinter.println(printer, s);
        }));

        if (board.getPointAt(0) != 0) {
            BackgammonBoardPrinter.println(printer, RED_PIECE + "on the BAR:" + board.getPointAt(0));
        }
        if (board.getPointAt(board.getPointsCount() + 1) != 0) {
            BackgammonBoardPrinter.println(printer, WHITE_PIECE + "on the BAR:" + (-board.getPointAt(board.getPointsCount() + 1)));
        }
    }

    private static Function<List<Integer>,
            Function<Supplier<IntStream>,
                    Function<Integer, Consumer<Consumer<String>>>>> doRenderPiece(Integer line) {
        return (List<Integer> points) ->
                (Supplier<IntStream> pointRange) ->
                        (Integer midPoint) ->
                                (Consumer<String> printer) -> {
                                    StringBuilder sb = new StringBuilder(" |");
                                    pointRange.get().forEach(p -> {
                                        int point = points.get(p);
                                        sb.append(generate_string(point, line));

                                        if (p == midPoint) {
                                            sb.append("|   |");
                                        }
                                    });
                                    sb.append("|");
                                    String s = sb.toString();
                                    printer.accept(s);
                                };
    }

    private static Function<Supplier<IntStream>, Function<Integer, Consumer<Consumer<String>>>> doRenderRawVals(List<Integer> points) {
        return (Supplier<IntStream> pointRange) ->
                (Integer midPoint) -> (Consumer<String> printer) -> {
                    StringBuilder sb = new StringBuilder(" ");
                    pointRange.get().forEach(p -> {
                        sb.append(String.format("%3d", points.get(p)));
                        if (p == midPoint) {
                            sb.append("     ");
                        }
                    });
                    printer.accept(sb.toString());
                };
    }

    private static Function<Supplier<IntStream>, Function<Integer, Consumer<Consumer<String>>>> doRenderLabel() {
        return (Supplier<IntStream> pointRange) ->
                (Integer midPoint) -> (Consumer<String> printer) -> {
                    StringBuilder sb = new StringBuilder(" +");
                    pointRange.get().forEach(p -> {
                                sb.append(String.format("%2d-", 25 - p).replace(' ', '-'));
                                if (p == midPoint) {
                                    sb.append("-----");
                                }
                            }
                    );
                    sb.append('+');
                    printer.accept(sb.toString());
                };
    }

    private static String generate_string(int tmp, int h) {
        final String s;
        final int count;
        if (tmp == 0) {
            s = "   ";
            count = 0;
        } else if (tmp > 0) {
            s = RED_PIECE;
            count = tmp;
        } else {
            s = WHITE_PIECE;
            count = -tmp;
        }

        String ret;
        if (h == 0 && count > 5) {
            ret = String.format("%2d ", count);
        } else if (count > h) {
            ret = s;
        } else {
            ret = "   ";
        }
        return ret;
    }

}
