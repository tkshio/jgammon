package com.github.tkshio.jgammon.gammon;

import com.github.tkshio.jgammon.common.utils.RWTuple;
import com.github.tkshio.jgammon.gammon.move.CheckerPlay;
import com.github.tkshio.jgammon.gammon.move.Roll;
import com.github.tkshio.jgammon.gammon.move.RollApplier;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * {@link BackgammonState}を操作するクラス
 * <p>
 * BackgammonStateを生成する処理はここに集約されており、ゲームの進行も先読みもこのクラスを通じて必要な局面生成を行う。
 */
public class BackgammonStateOperator {
    private final BackgammonDice bgDice;
    private final RollApplier<BackgammonBoard> rollApplier = BackgammonBoardRollApplier.create();

    private BackgammonStateOperator(BackgammonDice bgDice) {
        this.bgDice = bgDice;
    }

    /**
     * ファクトリーメソッド
     *
     * @return 生成されたオブジェクト
     */
    public static BackgammonStateOperator create() {
        return create(BackgammonDice.create());
    }

    /**
     * ダイスが指定可能なファクトリーメソッド
     *
     * @param bgDice 使用するダイス
     * @return 生成されたオブジェクト
     */
    public static BackgammonStateOperator create(BackgammonDice bgDice) {
        return new BackgammonStateOperator(bgDice);
    }

    private static BackgammonStateRed asRed(BackgammonBoard board, BackgammonBoard reverted, CheckerPlay checkerPlay, int ply) {
        return new BackgammonStateRed(board, reverted, checkerPlay, ply);
    }

    private static BackgammonStateWhite asWhite(BackgammonBoard board, BackgammonBoard reverted, CheckerPlay checkerPlay, int ply) {
        return new BackgammonStateWhite(board, reverted, checkerPlay, ply);
    }

    /**
     * 指定の局面にプレイを適用し、新しい状態を生成する
     *
     * <p>生成された局面は、Red/Whiteが入れ替わり、plyが1加算される。またここで渡したCheckerPlayオブジェクトが、{@link BackgammonState#getLastCheckerPlay()} の返値となる。
     *
     * @param state       対象となる局面
     * @param checkerPlay 適用するプレイ
     * @return 生成された状態
     */
    public BackgammonState withCheckerPlay(BackgammonState state, CheckerPlay checkerPlay) {
        return state.withCheckerPlay(checkerPlay);
    }

    /**
     * ロールを行う
     *
     * @return 生成されたロール
     */
    public Roll generateRoll() {
        return bgDice.roll();
    }

    /**
     * ありうるロールを列挙する
     *
     * <p> これは、可能手の生成ではなく、先読みに使用される
     *
     * @return 列挙されたロールのコレクション
     * @see com.github.tkshio.jgammon.common.evaluator.TwoPlyPlayer TwoPlyPlayer
     */
    public Collection<Roll> listupRolls() {
        return bgDice.listupRolls();
    }

    /**
     * オープニングロールを行う
     *
     * @return 生成されたオープニングロール
     */
    public BackgammonDice.OpeningRoll generateOpeningRoll() {
        return bgDice.rollOpening();
    }

    /**
     * 与えられた局面とロールに対して、可能な局面をすべて列挙する
     *
     * <p> 局面が終了状態にある場合、空のコレクションが返る
     *
     * @param state 対象局面
     * @param roll  ロール
     * @return 次の手番の局面のコレクション
     */
    public Collection<BackgammonState> buildNextState(BackgammonState state, Roll roll) {
        if (state.getResult().isOver()) {
            return Collections.emptyList();
        }

        var board = state.getBoard();
        return rollApplier
                .listupMoves(board, roll)
                .map(checkerPlay -> withCheckerPlay(state, checkerPlay))
                .collect(Collectors.toList());
    }

    /**
     * 与えられた盤面について、Red側先攻として初期局面を生成する
     *
     * @param board 初期盤面
     * @return 生成された初期局面
     */
    public BackgammonStateWhite redGoesFirst(BackgammonBoard board) {
        // Redがオープニングムーブを実施した後の状態がRedなので、Whiteで返す
        return asWhite(board, board.revert(), CheckerPlay.EMPTY, 0);
    }

    /**
     * 与えられた盤面について、White側先攻として初期局面を生成する
     *
     * @param board 初期盤面
     * @return 生成された初期局面
     */
    public BackgammonStateRed whiteGoesFirst(BackgammonBoard board) {
        // redGoesFirstの逆
        return asRed(board, board.revert(), CheckerPlay.EMPTY, 0);
    }

    private static class BackgammonStateWhite extends BackgammonState {
        private final BackgammonBoard board;

        public BackgammonStateWhite(BackgammonBoard board, BackgammonBoard reverted, CheckerPlay checkerPlay, int ply) {
            super(board, reverted, checkerPlay, ply);
            this.board = board;
        }

        @Override
        public AbsoluteBackgammonBoard getAbsoluteBoard() {
            return AbsoluteBackgammonBoard.absolute(board);
        }

        @Override
        public BackgammonState withCheckerPlay(CheckerPlay checkerPlay) {
            var afterMove = getBoard().withCheckerPlay(checkerPlay);
            return BackgammonStateOperator.asRed(afterMove.revert(), afterMove, checkerPlay, getPly() + 1);
        }

        @Override
        public <T> T acceptRWTuple(RWTuple<T> rwTuple) {
            return rwTuple.white();
        }

    }

    private static class BackgammonStateRed extends BackgammonState {
        private final BackgammonBoard reverted;

        public BackgammonStateRed(BackgammonBoard board, BackgammonBoard reverted, CheckerPlay checkerPlay, int ply) {
            super(board, reverted, checkerPlay, ply);
            this.reverted = reverted;
        }

        @Override
        public AbsoluteBackgammonBoard getAbsoluteBoard() {
            return AbsoluteBackgammonBoard.absolute(reverted);
        }

        @Override
        public BackgammonState withCheckerPlay(CheckerPlay checkerPlay) {
            var afterMove = getBoard().withCheckerPlay(checkerPlay);
            return BackgammonStateOperator.asWhite(afterMove.revert(), afterMove, checkerPlay, getPly() + 1);
        }

        @Override
        public <T> T acceptRWTuple(RWTuple<T> rwTuple) {
            return rwTuple.red();
        }
    }
}
