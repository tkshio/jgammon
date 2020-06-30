package com.github.tkshio.jgammon.gammon;

import com.github.tkshio.jgammon.gammon.move.CheckerPlay;
import com.github.tkshio.jgammon.gammon.move.Move;
import com.github.tkshio.jgammon.gammon.move.Roll;
import org.junit.Test;

public class TestListupMoves {
    private BackgammonBoard buildBoard(int[] pos) {
        return BackgammonBoard.create(BGType.standard, pos);
    }

    private Roll buildRoll(int r1, int r2) {
        return Roll.of(r1, r2);
    }

    // 候補手を挙げる
    @Test
    public void listup() {
        int[] pos = {
                0,
                0, 0, 0, 0, 0, 0, /* bar */ 0, 0, 0, 0, 0, 0,
                1, 0, 1, 0, 0, 0, /* bar */ 0, 0, 0, 0, 0, 0,
                0
        };
        var board = buildBoard(pos);
        var roll = buildRoll(1, 2);

        CheckerPlay[] answer = listupMoves(board, roll);
        Move[][] expected = {
                {Move.of(13, 14), Move.of(15, 17)}, //
//                {MoveItem.of(13, 14,true), MoveItem.of(14, 17)}, // 結果が同じになるムーブは列挙しない
                {Move.of(13, 15), Move.of(15, 16)}, //
                {Move.of(15, 17), Move.of(17, 18)}, //
        };
        TestUtils.compareMoves(answer, expected);
    }

    private CheckerPlay[] listupMoves(BackgammonBoard board, Roll roll) {
        return BackgammonBoardRollApplier.create().listupMoves(board, roll).sorted(
                TestUtils::compare
        ).toArray(CheckerPlay[]::new);
    }

    // 両方使うのであれば、どちらを先に使ってもよい、ヒットしないムーブも可能
    @Test
    public void eitherOneMayGoFirst() {
        int[] pos = {
                0,
                0, 0, 0, 0, 0, 0, /* bar */ 0, 0, 0, 0, 0, 0,
                1, -1, 1, 0, 0, 0, /* bar */ 0, 0, 0, 0, 0, 0,
                0
        };
        var board = buildBoard(pos);
        var roll = buildRoll(1, 2);

        CheckerPlay[] answer = listupMoves(board, roll);
        Move[][] expected = {
                {Move.of(13, 14, true), Move.of(14, 16)}, //
                {Move.of(13, 14, true), Move.of(15, 17)}, // ヒットがあるので、結果が変わる
                {Move.of(13, 15), Move.of(15, 16)}, //
                {Move.of(15, 17), Move.of(17, 18)}, //
        };
        TestUtils.compareMoves(answer, expected);
    }

    // ゾロ目の場合は二回使える
    @Test
    public void doublet() {
        int[] pos = {
                0,
                0, 0, 0, 0, 0, 0, /* bar */ 0, 0, 0, 0, 0, 0,
                1, -1, 1, 0, -1, -1, /* bar */ 0, 0, 0, 0, 0, 0,
                0
        };
        var board = buildBoard(pos);
        var roll = buildRoll(1, 1);

        CheckerPlay[] answer = listupMoves(board, roll);
        Move[][] expected = {
                {Move.of(13, 14, true), Move.of(14, 15), Move.of(15, 16), Move.of(15, 16)}, //
                {Move.of(13, 14, true), Move.of(14, 15), Move.of(15, 16), Move.of(16, 17, true)}, //
                {Move.of(13, 14, true), Move.of(15, 16), Move.of(16, 17, true), Move.of(17, 18, true)}, //
                {Move.of(15, 16), Move.of(16, 17, true), Move.of(17, 18, true), Move.of(18, 19)}, //
        };
        TestUtils.compareMoves(answer, expected);

    }

    // どの目も使えないときは、ムーブしてはいけない
    @Test
    public void noCandidates() {
        int[] pos = {
                0,
                0, 0, 2, 0, 0, 0, /* bar */ -2, -2, 0, 0, 0, 0,
                2, -1, -1, 0, -2, -2, /* bar */ 0, 0, 0, 0, 0, 0,
                0
        };
        var board = buildBoard(pos);
        var roll = buildRoll(4, 5);

        CheckerPlay[] answer = listupMoves(board, roll);
        Move[][] expected = {
                {}
        };
        TestUtils.compareMoves(answer, expected);
    }

    // 両方使える手があるときは、両方使わなくてはいけない
    @Test
    public void mustUseBothRollsIfPossible() {
        int[] pos = {
                0,
                0, 0, 0, 0, 0, 0, /* bar */ 0, 0, 0, 0, 0, 0,
                1, -2, 0, 1, -2, 0, /* bar */ -2, 0, -2, -2, 0, 0,
                0
        };
        var board = buildBoard(pos);
        var roll = buildRoll(6, 2);

        CheckerPlay[] answer = listupMoves(board, roll);
        Move[][] expected = {
                {Move.of(16, 18), Move.of(18, 24)},
        };
        TestUtils.compareMoves(answer, expected);
    }


    // 片方しか使えないときは、大きい目を使わなくてはいけない
    @Test
    public void mustUseLargerRoll() {
        int[] pos = {
                0,
                0, 0, 0, 0, 0, 0, /* bar */ 0, 0, 0, 0, 0, 0,
                1, -2, -2, 0, -2, 0, /* bar */ 0, 0, -2, 0, 0, 0,
                0
        };
        var board = buildBoard(pos);
        var roll = buildRoll(5, 3);

        CheckerPlay[] answer = listupMoves(board, roll);
        Move[][] expected = {
                {Move.of(13, 18)},
        };
        TestUtils.compareMoves(answer, expected);
    }

    // オンザバーのときは、そちらを先に上げなくてはいけない
    // オンザバーが解消されれば、もう一つは自由に使ってよい
    @Test
    public void onTheBar() {
        int[] pos = {
                1,
                0, 0, 0, 0, 0, 0, /* bar */ 0, 0, 0, 0, 0, 0,
                1, -2, -2, 0, -2, 0, /* bar */ 0, 0, -2, 0, 0, 0,
                0
        };
        var board = buildBoard(pos);
        var roll = buildRoll(5, 3);

        CheckerPlay[] answer = listupMoves(board, roll);
        Move[][] expected = {
                {Move.of(0, 3), Move.of(13, 18)},
                {Move.of(0, 5), Move.of(5, 8)},
                {Move.of(0, 5), Move.of(13, 16)},
        };
        TestUtils.compareMoves(answer, expected);
    }

    // すべてオンザバー
    @Test
    public void onTheBar2Pieces() {
        int[] pos = {
                2,
                0, 0, 0, 0, 0, 0, /* bar */ 0, 0, 0, 0, 0, 0,
                1, -2, -2, 0, -2, 0, /* bar */ 0, 0, -2, 0, 0, 0,
                0
        };
        var board = buildBoard(pos);
        var roll = buildRoll(5, 3);

        CheckerPlay[] answer = listupMoves(board, roll);
        Move[][] expected = {
                {Move.of(0, 5), Move.of(0, 3)},
        };
        TestUtils.compareMoves(answer, expected);
    }

    // ゾロ目 x オンザバ―
    @Test
    public void onTheBarAndDoublet() {
        int[] pos = {
                3,
                0, 0, 0, 0, 0, 0, /* bar */ 0, 0, 0, 0, 0, 0,
                1, -2, -2, 0, -2, 0, /* bar */ 0, 0, -2, 0, 0, 0,
                0
        };
        var board = buildBoard(pos);
        var roll = buildRoll(3, 3);

        CheckerPlay[] answer = listupMoves(board, roll);
        Move[][] expected = {
                {Move.of(0, 3), Move.of(0, 3), Move.of(0, 3), Move.of(3, 6)},
                {Move.of(0, 3), Move.of(0, 3), Move.of(0, 3), Move.of(13, 16)},
        };
        TestUtils.compareMoves(answer, expected);
    }

    // ゾロ目 x すべてオンザバーで消費
    @Test
    public void onTheBar4piecesAndAndDoublet() {
        int[] pos = {
                5,
                0, 0, 0, 0, 0, 0, /* bar */ 0, 0, 0, 0, 0, 0,
                1, -2, -2, 0, -2, 0, /* bar */ 0, 0, -2, 0, 0, 0,
                0
        };
        var board = buildBoard(pos);
        var roll = buildRoll(3, 3);

        CheckerPlay[] answer = listupMoves(board, roll);
        Move[][] expected = {
                {Move.of(0, 3), Move.of(0, 3), Move.of(0, 3), Move.of(0, 3)},
        };
        TestUtils.compareMoves(answer, expected);
    }

    // オンザバーの時も片方のみ使える場合は大きい目から
    @Test
    public void mustUseLargerRollFromTheBar() {
        int[] pos = {
                1,
                -2, -2, 0, -2, 0, -2, /* bar */ 0, -2, -2, 0, -2, 0,
                0, 0, 0, 0, 0, 0, /* bar */ 0, 0, 0, 0, 0, 1,
                0
        };
        var board = buildBoard(pos);
        var roll = buildRoll(3, 5);

        CheckerPlay[] answer = listupMoves(board, roll);
        Move[][] expected = {
                {Move.of(0, 5)},
        };
        TestUtils.compareMoves(answer, expected);
    }

    // オンザバーで動かせる目がない
    @Test
    public void dance() {
        int[] pos = {
                1,
                -2, -2, -2, -2, -2, -2, /* bar */ 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, /* bar */ 0, 0, 0, 0, 0, 1,
                0
        };
        var board = buildBoard(pos);
        var roll = buildRoll(3, 5);

        CheckerPlay[] answer = listupMoves(board, roll);
        Move[][] expected = {{}
        };

        TestUtils.compareMoves(answer, expected);
    }

    // ベアリングオフは、すべての駒がインナーに入ってから
    @Test
    public void bearingOff() {
        int[] pos = {
                0,
                0, 0, 0, -2, -2, -2, /* bar */ 0, 0, 0, 0, 0, 0,
                0, 0, 1, 0, 0, 0, /* bar */ 0, 1, 0, 1, 1, 1,
                0
        };
        var board = buildBoard(pos);
        var roll = buildRoll(3, 5);

        CheckerPlay[] answer = listupMoves(board, roll);
        Move[][] expected = {
                {Move.of(15, 20), Move.of(20, 23)},
                {Move.of(15, 20), Move.of(22, -1)},
        };

        TestUtils.compareMoves(answer, expected);
    }

    // 対応するロールがない場合、最後尾の駒をあげてよい
    @Test
    public void lastPieceMayOverrun() {
        int[] pos = {
                0,
                0, 0, 0, -2, -2, -2, /* bar */ 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, /* bar */ 0, 1, 0, 1, 1, 1,
                0
        };
        var board = buildBoard(pos);
        var roll = buildRoll(2, 6);

        CheckerPlay[] answer = listupMoves(board, roll);
        Move[][] expected = {
                {Move.of(20, -1), Move.of(22, 24)},
                {Move.of(20, -1), Move.of(23, -1)},
                {Move.of(20, 22), Move.of(22, -1)},
        };

        TestUtils.compareMoves(answer, expected);
    }

    // オンザバーの間はベアオフできない
    @Test
    public void bearingOffAndOnTheBar() {
        int[] pos = {
                1,
                0, 0, 0, -2, -2, -2, /* bar */ 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, /* bar */ 1, 1, 0, 1, 1, 1,
                0
        };
        var board = buildBoard(pos);
        var roll = buildRoll(2, 6);

        CheckerPlay[] answer = listupMoves(board, roll);
        Move[][] expected = {
                {Move.of(0, 2), Move.of(2, 8)},
        };
        TestUtils.compareMoves(answer, expected);
    }

    // ベアオフでも重複排除
    @Test
    public void dupRemovedInbearingOff() {
        int[] pos = {
                0,
                0, 0, 0, -2, -2, -2, /* bar */ 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, /* bar */ 0, 0, 0, 0, 1, 1,
                0
        };
        var board = buildBoard(pos);
        var roll = buildRoll(2, 6);

        CheckerPlay[] answer = listupMoves(board, roll);
        Move[][] expected = {
                {Move.bearOff(23), Move.bearOff(24)},
        };
        TestUtils.compareMoves(answer, expected);
    }

    // ベアオフでも重複排除（どちらもオーバーラン）
    @Test
    public void dupRemovedInOverrunBearingOff() {
        int[] pos = {
                0,
                0, 0, 0, -2, -2, -2, /* bar */ 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, /* bar */ 0, 0, 0, 0, 1, 1,
                0
        };
        var board = buildBoard(pos);
        var roll = buildRoll(5, 6);

        CheckerPlay[] answer = listupMoves(board, roll);
        Move[][] expected = {
                {Move.bearOff(23), Move.bearOff(24)},
        };
        TestUtils.compareMoves(answer, expected);
    }

    // ベアオフで順序があるので重複ではない
    @Test
    public void noDupInbearingOff() {
        int[] pos = {
                0,
                0, 0, 0, -2, -2, -2, /* bar */ 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, /* bar */ 0, 0, 0, 1, 0, 1,
                0
        };
        var board = buildBoard(pos);
        var roll = buildRoll(2, 3);

        CheckerPlay[] answer = listupMoves(board, roll);
        Move[][] expected = {
                {Move.bearOff(22), Move.bearOff(24)},
                {Move.of(22, 24), Move.bearOff(24)},
        };
        TestUtils.compareMoves(answer, expected);
    }
}
