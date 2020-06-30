package com.github.tkshio.jgammon.tdlearn.bg;

import com.github.tkshio.jgammon.common.utils.RWTuple;
import com.github.tkshio.jgammon.gammon.BackgammonResult;
import com.github.tkshio.jgammon.gammon.BackgammonState;
import com.github.tkshio.jgammon.tdlearn.codecs.DecodedEval;
import com.github.tkshio.jgammon.tdlearn.codecs.OutputCodec;
import com.github.tkshio.jgammon.tdlearn.codecs.OutputCodecs;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.github.tkshio.jgammon.tdlearn.codecs.OutputCodecs.toEval;

/**
 * TDGammon Ver1.0に準拠した出力結果の変換ロジック
 *
 * <p>4つある出力ノードを、それぞれ先手番・後手番のシングル率・ギャモン率に割り当てている。
 * バックギャモンはオリジナルの設計通り、稀なので無視する。ただしこのコードでは、バックギャモンの局面を3.0として評価している。
 */
public class BGOutputCodecs {
    /**
     * 基本手なる変換ロジック
     *
     * <p>{@code BGInputCodecs}とは異なり、盤面サイズに合わせて可変にする必要はない。
     */
    public static final OutputCodecs<BackgammonState> DEFAULT = new OutputCodecs<>() {

        private final OutputCodec<BackgammonState> SENTE = createCodec(
                new double[]{1.0, 0.0, 0.0, 0.0},
                new double[]{1.0, 1.0, 0.0, 0.0},
                BGOutputCodecs::evalAsSente,
                BGOutputCodecs::formatSente
        );
        private final OutputCodec<BackgammonState> GOTE = createCodec(
                new double[]{0.0, 0.0, 1.0, 0.0},
                new double[]{0.0, 0.0, 1.0, 1.0},
                BGOutputCodecs::evalAsGote,
                BGOutputCodecs::formatGote
        );

        public OutputCodec<BackgammonState> sente() {
            return SENTE;
        }

        public OutputCodec<BackgammonState> gote() {
            return GOTE;
        }

        public int getOutputSize() {
            return 4;
        }
    };

    private static DecodedEval<BackgammonState> evalAsSente(double[] values) {
        return toEval(values[0] + values[1] - values[2] - values[3],
                (BackgammonState state) -> formatSente(state, values));
    }

    private static DecodedEval<BackgammonState> evalAsGote(double[] values) {
        return toEval(-(values[0] + values[1] - values[2] - values[3]),
                (BackgammonState state) -> formatGote(state, values));
    }

    private static OutputCodec<BackgammonState> createCodec(double[] single,
                                                            double[] gammon,
                                                            Function<double[], DecodedEval<BackgammonState>> eval,
                                                            BiFunction<BackgammonState, double[], String> formatter

    ) {

        final DecodedEval<BackgammonState> SINGLE =
                OutputCodecs.toEval(1.0,
                        (state) -> formatter.apply(state, single));
        final DecodedEval<BackgammonState> GAMMON =
                OutputCodecs.toEval(2.0,
                        (state) -> formatter.apply(state, gammon));
        final DecodedEval<BackgammonState> BACKGAMMON =
                OutputCodecs.toEval(3.0,
                        (state) -> formatter.apply(state, gammon));

        return new OutputCodec<>() {
            @Override
            public DecodedEval<BackgammonState> decode(double[] values, BackgammonState state) {
                return state.getResult().convert(new BackgammonResult.Converter<>() {
                    @Override
                    public DecodedEval<BackgammonState> inGame() {
                        return eval.apply(values);
                    }

                    @Override
                    public DecodedEval<BackgammonState> single() {
                        return SINGLE;
                    }

                    @Override
                    public DecodedEval<BackgammonState> gammon() {
                        return GAMMON;
                    }

                    @Override
                    public DecodedEval<BackgammonState> backgammon() {
                        return BACKGAMMON;
                    }
                });
            }

            public double[] encode(BackgammonState state) {
                return state.getResult().convert(new BackgammonResult.Converter<>() {
                    @Override
                    public double[] inGame() {
                        throw new IllegalStateException();
                    }

                    @Override
                    public double[] single() {
                        return single;
                    }

                    @Override
                    public double[] gammon() {
                        return gammon;
                    }

                    @Override
                    public double[] backgammon() {
                        return gammon;
                    }
                });
            }
        };
    }

    /**
     * 先手の評価出力をテキスト化する
     *
     * @param state  局面を表すクラス
     * @param values 出力値
     * @return テキスト
     */
    public static String formatSente(BackgammonState state, double[] values) {

        // Stateの情報によって、先手番の評価値をred/whiteのどちら側に出力するかを決める
        RWTuple<Supplier<String>> formatters = RWTuple.of(
                () -> formatSenteAsRed(values), // 先手番が紅側の局面
                () -> formatSenteAsWhite(values) // 上記の逆
        );

        return state.acceptRWTuple(formatters).get();
    }

    /**
     * 後手の評価出力をテキスト化する
     *
     * @param state  局面を表すクラス
     * @param values 出力値
     * @return テキスト
     */
    public static String formatGote(BackgammonState state, double[] values) {

        // formatSente()と逆転している
        RWTuple<Supplier<String>> formatters = RWTuple.of(
                () -> formatGoteAsRed(values),
                () -> formatGoteAsWhite(values)
        );

        return state.acceptRWTuple(formatters).get();
    }

    private static String formatSenteAsRed(double[] values) {
        return String.format("red %2.3f %2.3f / white %2.3f %2.3f", values[0] * 100, values[1] * 100, values[2] * 100, values[3] * 100);
    }

    private static String formatSenteAsWhite(double[] values) {
        return String.format("red %2.3f %2.3f / white %2.3f %2.3f", values[2] * 100, values[3] * 100, values[0] * 100, values[1] * 100);
    }

    private static String formatGoteAsWhite(double[] values) {
        return formatSenteAsRed(values);
    }

    private static String formatGoteAsRed(double[] values) {
        return formatSenteAsWhite(values);
    }


}
