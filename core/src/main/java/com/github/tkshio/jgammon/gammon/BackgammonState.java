package com.github.tkshio.jgammon.gammon;

import com.github.tkshio.jgammon.common.utils.Memoizer;
import com.github.tkshio.jgammon.common.utils.RWTuple;
import com.github.tkshio.jgammon.gammon.move.CheckerPlay;

import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * 対局中の状態（局面）を表す抽象クラス
 *
 * <p>{@link BackgammonBoard}が盤と駒の関係のみを表すのに対し、こちらは手番、終了状態かどうかの管理など、ゲーム中の流れを含めた情報を管理する
 * <p> BackgammonBoard同様、このクラスもimmutableで、プレイを行う都度新しいオブジェクトが生成される
 * <p>これを継承した{@code BackgammonStateOperator.BackgammonStateRed}と{@code BackgammonStateOperator.BackgammonStateWhite} を交互に切り替えて使用している
 */
public abstract class BackgammonState {
    private final BackgammonBoard board;
    private final BackgammonBoard reverted;
    private final Supplier<AbsoluteBackgammonBoard> absolute;

    private final CheckerPlay lastCheckerPlay;
    private final int ply;
    private final Supplier<BackgammonResult> result;

    protected BackgammonState(BackgammonBoard board, BackgammonBoard reverted, BackgammonBoard absolute, CheckerPlay checkerPlay, int ply) {
        this.board = board;
        this.reverted = reverted;
        this.absolute = Memoizer.memoize(() -> AbsoluteBackgammonBoard.absolute(absolute));
        this.lastCheckerPlay = checkerPlay;
        this.ply = ply;
        this.result = Memoizer.memoize(() -> isEndOfGame(this));
    }

    // 終了判定を行う
    private static BackgammonResult isEndOfGame(BackgammonState state) {

        // 相手が手番を終えた状態の盤面を使用する
        var reverted = state.getRevertedBoard();

        // 相手が手番を終えた状態で、まだ相手駒が一個でも残っていれば終了ではない
        if (IntStream.range(0, reverted.getBearOffPos()).anyMatch(i -> reverted.getPointAt(i) > 0)) {
            return BackgammonResult.InGame;
        }

        // 相手側の駒がない＝相手の勝利

        var board = state.getBoard();
        // 自駒の数を数えて、ギャモンの判定を行う
        long count = IntStream.range(0, board.getBearOffPos()).map(board::getPointAt).sum();

        if (count == board.getInitialPieces()) {
            // 敵のインナーボードに自分の駒があるかどうか
            int innerPos = reverted.getInnerPos();
            int bearOffPos = reverted.getBearOffPos();
            if (IntStream.range(innerPos, bearOffPos)
                    .anyMatch(i -> reverted.getPointAt(i) < 0)
            ) {
                return BackgammonResult.Backgammon;
            }
            // バーポイントにある場合もバックギャモン
            if (board.getPointAt(0) > 0) {
                return BackgammonResult.Backgammon;
            }
            return BackgammonResult.Gammon;
        }

        return BackgammonResult.Single;
    }

    /**
     * 絶対座標のボードを得る
     *
     * <p> Redは配列の内容をそのまま絶対座標として、Whiteは反転させて返す。
     * したがって絶対座標のボードでは、Redの駒の数は正の値で示され、atPoint(0)がRedのバーポイントとなる。 </p>
     * <p>座標は内部的な定義であり、BoardFormatterなどの出力系では（XGとの対応の都合により）Redが24->１の方向に進む、逆方向の表記になっていることに注意</p>
     *
     * @return 絶対座標のボード
     */
    public AbsoluteBackgammonBoard getAbsoluteBoard() {
        return absolute.get();
    }

    /**
     * プレイを適用し、相手の手番の状態を返す
     *
     * <p> BackgammonBoardもこの時反転する
     *
     * @param checkerPlay 適用するプレイ
     * @return 相手の手番のBackgammonStateオブジェクト
     */
    public abstract BackgammonState withCheckerPlay(CheckerPlay checkerPlay);

    /**
     * 盤面を返す
     *
     * <p>座標はプレイするプレイヤーの観点となる
     *
     * @return 盤面を表すオブジェクト
     */
    public BackgammonBoard getBoard() {
        return board;
    }

    /**
     * 相手観点の盤面を返す
     * <p>
     * {@code getBoard()}と同内容、逆転座標の盤面を返す
     *
     * @return 盤面を表すオブジェクト
     */
    public BackgammonBoard getRevertedBoard() {
        return reverted;
    }

    /**
     * ゲームの状態を返す
     *
     * <p> ゲームが終了状態の場合、直前の相手の手番によって終了（＝すべての駒が上がった）したことになる。したがって、相手の勝利である。
     *
     * @return ゲーム状態
     */
    public BackgammonResult getResult() {
        return result.get();
    }

    /**
     * この局面になる直前のプレイを返す
     *
     * <p> 直前のプレイは相手方の向きで格納されている。また初期局面の場合、0-0のロールと空のムーブからなるプレイが返る。
     *
     * <p>ここで返る値は、初期局面を除いて{@link BackgammonStateOperator#withCheckerPlay} で渡された値となる。
     *
     * @return 直前のプレイ
     */
    public CheckerPlay getLastCheckerPlay() {
        return lastCheckerPlay;
    }

    /**
     * 現在が何手番目かを返す。
     *
     * @return 現在が何手番目か。初期盤面が0
     */
    public int getPly() {
        return ply;
    }

    /**
     * 受け取ったRWTupleについて、自局面が赤ならred()、白ならwhite()を返す
     *
     * @param rwTuple 対象となるRWTuple
     * @param <T>     返されるオブジェクトの型
     * @return red()またはwhite()のいずれか
     */
    public abstract <T> T acceptRWTuple(RWTuple<T> rwTuple);
}
