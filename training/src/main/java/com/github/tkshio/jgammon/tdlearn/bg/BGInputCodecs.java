package com.github.tkshio.jgammon.tdlearn.bg;

import com.github.tkshio.jgammon.gammon.BackgammonBoard;
import com.github.tkshio.jgammon.gammon.BackgammonState;
import com.github.tkshio.jgammon.tdlearn.codecs.InputCodec;
import com.github.tkshio.jgammon.tdlearn.codecs.InputCodecs;

/**
 * TDGammon Ver1.0に準拠した直接的な変換ロジック
 * <p>
 * 各ポイントの駒数、オンザバー、ベアオフ済、手番を入力として使用する
 */
public class BGInputCodecs {
    /**
     * 標準サイズのバックギャモン用の{@code InputCodecs}
     */
    public static final InputCodecs<BackgammonState> DEFAULT = codec(24);

    /**
     * 盤面のサイズに応じた{@code InputCodecs}を生成する
     *
     * @param boardSize 盤面のサイズ
     * @return 生成されたInputCodecs
     */
    public static InputCodecs<BackgammonState> codec(int boardSize) {
        final int INPUT = inputSize(boardSize);
        return new InputCodecs<>() {
            final InputCodec<BackgammonState> SENTE = state -> {
                var board = state.getBoard();
                return encodeBG(board, boardSize, INPUT);
            };
            final InputCodec<BackgammonState> GOTE = state -> {
                // BackgammonBoardは相対座標なので、後手番では反転させてから変換する
                double[] input = encodeBG(state.getRevertedBoard(), boardSize, INPUT);

                // 手番は盤面とは無関係に設定されているので、改めて指定
                input[boardSize * 8 + 4] = 0;
                input[boardSize * 8 + 5] = 1;

                return input;
            };

            @Override
            public InputCodec<BackgammonState> sente() {
                return SENTE;
            }

            @Override
            public InputCodec<BackgammonState> gote() {
                return GOTE;
            }

            @Override
            public int getInputSize() {
                return INPUT;
            }
        };
    }

    private static int inputSize(int size) {
        return size * 8 + 6;
    }

    /**
     * バックギャモンの盤面を変換する
     *
     * @param board 変換対象となる盤面
     * @return 評価結果
     */
    public static double[] encodeBG(BackgammonBoard board) {
        int size = board.getPointsCount();
        return encodeBG(board, size, inputSize(size));
    }

    private static double[] encodeBG(BackgammonBoard board, int boardSize, int inputSize) {
        double[] input = new double[inputSize];
        int white = 0, red = 0;
        for (int i = 0; i < boardSize; i++) {
            int p = board.getPointAt(i + 1);
            if (p > 0) {
                input[i * 8] = 1.0; // p > 0
                input[i * 8 + 1] = (p > 1) ? 1.0 : 0.0;
                input[i * 8 + 2] = (p > 2) ? 1.0 : 0.0;
                input[i * 8 + 3] = (double) (p - 3) / 2.0;
                white += p;
            } else if (p < 0) {
                input[i * 8 + 4] = 1.0;
                input[i * 8 + 5] = (-p > 1) ? 1.0 : 0.0;
                input[i * 8 + 6] = (-p > 2) ? 1.0 : 0.0;
                input[i * 8 + 7] = (double) (-p - 3) / 2.0;
                red -= p;
            }
        }
        int pieces = board.getInitialPieces();
        // オンザバー
        input[boardSize * 8] = (double) board.getPointAt(0) / 2.0;
        input[boardSize * 8 + 1] = (double) (-board.getPointAt(board.getBearOffPos())) / 2.0;

        // ベアオフ済
        input[boardSize * 8 + 2] = (double) (pieces - white) / pieces;
        input[boardSize * 8 + 3] = (double) (pieces - red) / pieces;

        // 手番（先手）
        input[boardSize * 8 + 4] = 1.0;
        input[boardSize * 8 + 5] = 0.0;

        return input;
    }
}
