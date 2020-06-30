package com.github.tkshio.jgammon.gammon.utils;

import com.github.tkshio.jgammon.gammon.BGType;
import com.github.tkshio.jgammon.gammon.move.CheckerPlay;
import com.github.tkshio.jgammon.gammon.move.Move;
import com.github.tkshio.jgammon.gammon.move.Roll;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * テキストで記載されたムーブをパースする
 */
public class SimpleNotation {

    /**
     * 文字列を解析して、指し手を示すCheckerPlayオブジェクトを生成する
     *
     * @param str [roll1][roll2]:[from|BAR]-[to|OFF]( [from]-[to])* e.g. "16:24/23, 15/9"
     * @return 解析された指し手
     * @throws IllegalArgumentException 解析できない文字列が与えられた
     *                                  <p>
     *                                  解析結果は内部表現に変換される。その際、{@literal from > to}の場合は反転した座標とみなす。
     *                                  したがって、24/23 は 1/2 と同じ内容のオブジェクトを生成する
     *
     *                                  <p>{@literal
     *                                  複数の同じ手を数値で表す記法（e.g. 1/3 1/3 -> 1/3(2) )、
     *                                  同一の駒を動かす手を一つにまとめる記法（e.g. 1/3 3/5 -> 1/3/5 or 1/5 )には
     *                                  （まだ）未対応}
     */
    public static CheckerPlay parse(String str) throws IllegalArgumentException {
        return parse(str, BGType.standard.getBearOffPos());
    }

    public static CheckerPlay parse(String str, int bearOffPos) throws IllegalArgumentException {
        String[] str_nodes = str.split(":");
        if (str_nodes.length != 2) {
            throw new IllegalArgumentException();
        }

        Roll roll = parseRoll(str_nodes[0].trim());
        Move[] moves = parseMoves(str_nodes[1].trim(), bearOffPos).toArray(new Move[]{});
        return CheckerPlay.of(roll, moves);

    }

    private static Collection<Move> parseMoves(String str, int bearOffPos) {
        final Pattern pattern = Pattern.compile("(\\d+|bar)/(\\d+|off)(\\*?)", Pattern.CASE_INSENSITIVE);

        String[] str_items = str.split(" ");
        if (str_items.length == 0) {
            throw new IllegalArgumentException();
        }
        Collection<Move> moves = new ArrayList<>(str_items.length);

        for (String s : str_items) {
            Matcher m = pattern.matcher(s.trim());
            if (m.matches()) {
                int from = m.group(1).equalsIgnoreCase("BAR") ? 0 : Integer.parseInt(m.group(1));
                int to = m.group(2).equalsIgnoreCase("OFF") ? -1 : Integer.parseInt(m.group(2));
                boolean isHit = !m.group(3).isEmpty();
                if (from == 0 && to > 6) {
                    to = bearOffPos - to;
                } else if (to == -1 && from < bearOffPos - 6) {
                    from = bearOffPos - from;
                } else if (to != -1 && from > to) {
                    from = bearOffPos - from;
                    to = bearOffPos - to;
                }
                Move move = Move.of(
                        from,
                        to,
                        isHit
                );
                moves.add(move);
            } else {
                throw new IllegalArgumentException("\"" + s + "\" is not in valid format");
            }
        }
        return moves;
    }

    private static Roll parseRoll(String str) {
        final Pattern pattern = Pattern.compile("(\\d)(\\d)");
        Matcher m = pattern.matcher(str);
        if (m.matches()) {
            return Roll.of(
                    Integer.parseInt(m.group(1)),
                    Integer.parseInt(m.group(2)));
        } else {
            throw new IllegalArgumentException("\"" + str + "\" is not in valid format");
        }
    }
}
