package com.github.tkshio.jgammon.gammon.move;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * プレイ（チェッカープレイ）、すなわち１手番（1ply）でのムーブ全体を表す
 *
 * <p>ロールと、ムーブのリストで構成される。可能な手がない場合は、ムーブのリストは空リストになる。
 */
@Data
@EqualsAndHashCode
public class CheckerPlay {
    private final static Roll ZERO = Roll.of(0, 0, ""); // 初期盤面を生成するときのみに使用
    public static final CheckerPlay EMPTY = CheckerPlay.of(ZERO); // 同上
    private final Roll roll;
    private final Move[] moves;

    /**
     * コンストラクター
     *
     * @param roll  ロール
     * @param moves ムーブの配列（省略可）
     * @return CheckerPlayオブジェクト
     */
    public static CheckerPlay of(Roll roll, Move... moves) {
        return new CheckerPlay(roll, moves.clone());
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
        return format(Move::formatInDesc);
    }

    /**
     * {@literal 0->24}の向きのムーブとしてテキスト化する
     *
     * @return 内容を示すテキスト
     */
    public String formatInAsc() {
        return format(Move::formatInAsc);
    }


    private String format(Function<Move, String> moveFormatter) {
        String moves_str = Stream.of(moves).map(moveFormatter).collect(Collectors.joining(" "));
        return String.format("%s: %s",
                roll.toString(),
                moves_str);
    }
}
