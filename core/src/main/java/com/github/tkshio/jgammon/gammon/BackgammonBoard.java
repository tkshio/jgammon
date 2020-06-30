package com.github.tkshio.jgammon.gammon;

import com.github.tkshio.jgammon.common.utils.Memoizer;
import com.github.tkshio.jgammon.gammon.move.BGBoard;
import com.github.tkshio.jgammon.gammon.move.CheckerPlay;
import com.github.tkshio.jgammon.gammon.move.Move;

import java.util.function.Supplier;

/**
 * バックギャモンの盤面を表す
 *
 * <p> BackgammonStateがゲームの流れの中での状況（局面）を示すのに対し、このクラスは
 * 純粋に盤上の駒の配置（盤面）を表す。
 * <p>
 * 座標系は常に主観的観点をとり、ムーブを行うプレイヤーの開始位置が1、相手の開始位置が24となる。ムーブは、数値が増える方向に進行することになる。
 *
 * <p>また、immutableなオブジェクトであるため、盤面の状態を直接変更する手段は提供されていない。ムーブを適用すると、新たなオブジェクトが生成される。
 */
public class BackgammonBoard implements BGBoard {


    private final IntArrayBoard board;
    private final int size;
    private final int initialPieces;
    private final int bearOffPos;
    private final int highestInner;
    private final Supplier<Boolean> getIsInBearOff = Memoizer.memoize(this::_isInBearingOff);

    private BackgammonBoard(IntArrayBoard board,
                            int size, int initialPieces, int bearOffPos, int highestInner) {
        if (size <= bearOffPos ||
                highestInner >= bearOffPos) {
            throw new IllegalArgumentException();
        }

        this.board = board;
        this.size = size;
        this.bearOffPos = bearOffPos;
        this.highestInner = highestInner;
        this.initialPieces = initialPieces;
    }

    /**
     * ファクトリーメソッド
     *
     * @param bgType プレイするギャモンのタイプ
     * @return 生成されたBackgammonBoardオブジェクト
     */
    public static BackgammonBoard create(BGType bgType) {

        return create(bgType, bgType.initialArrangement());
    }

    /**
     * 配置を指定可能なファクトリーメソッド
     *
     * @param bgType             プレイするギャモンのタイプ
     * @param initialArrangement 初期配置
     * @return 生成されたBackgammonBoardオブジェクト
     */
    public static BackgammonBoard create(BGType bgType, int[] initialArrangement) {

        return create(initialArrangement,
                bgType.getInitialPieces(),
                bgType.getBearOffPos(),
                bgType.getInnerPos()
        );
    }

    private static BackgammonBoard create(int[] initialArrangement,
                                          int initialPieces,
                                          int bearOffPos,
                                          int highestInner) {
        return new BackgammonBoard(
                IntArrayBoard.create(initialArrangement),
                initialArrangement.length, initialPieces, bearOffPos, highestInner);
    }

    /**
     * ムーブを適用した後、すなわち駒を一つ動かした後の盤面を返す
     *
     * @param move 適用するムーブ
     * @return 適用後の盤面
     */
    BackgammonBoard withMove(Move move) {
        return duplicateWith(IntArrayBoard.dupWithMoves(board, move));
    }

    /**
     * チェッカープレイ、すなわち一連のムーブを適用した後の盤面を返す
     *
     * <p>プレイ適用後は手番が相手方に移ることになるが、それはBackgammonState側で管理するため、このメソッドが返す盤面の向きは適用前と同じまま。
     *
     * @param checkerPlay 適用するプレイ
     * @return 適用後の盤面
     */
    BackgammonBoard withCheckerPlay(CheckerPlay checkerPlay) {
        return duplicateWith(IntArrayBoard.dupWithMoves(board, checkerPlay.getMoves()));
    }

    /**
     * 座標系を反転させ、相手観点からの盤面を返す
     *
     * <p>{@code getPointAt}などの返値も正負が反転するようになる。
     *
     * @return 反転させた盤面
     */
    BackgammonBoard revert() {
        return duplicateWith(IntArrayBoard.revert(board));
    }

    private BackgammonBoard duplicateWith(IntArrayBoard intArrayBoard) {
        return new BackgammonBoard(intArrayBoard,
                size,
                initialPieces,
                bearOffPos,
                highestInner);
    }

    @Override
    public int getPointAt(int pos) {
        return board.getPointAt(pos);
    }


    /**
     * 1プレイヤーの駒数を返す。ベアリングオフの終了判定に使用。
     *
     * @return 15, 10, 8のいずれか
     */
    public int getInitialPieces() {
        return initialPieces;
    }


    /**
     * 表示するポイントの数を返す
     *
     * @return 24、またはボードサイズに応じた値
     */
    public int getPointsCount() {
        return board.size() - 2;
    }

    @Override
    public int getBearOffPos() {
        return bearOffPos;
    }

    /**
     * インナーボードの開始点を返す
     *
     * @return 19, またはボードサイズに応じた値
     */
    public int getInnerPos() {
        return highestInner;
    }


    /**
     * バーポイントを含めた、ポイントの総数。すなわち、getPointAtの引数として指定可能な最大値+1を返す。
     *
     * @return 26、またはボードサイズに応じた値
     */
    public int size() {
        return size;
    }

    @Override
    public boolean isInBearingOff() {
        return getIsInBearOff.get();
    }

    private boolean _isInBearingOff() {
        for (int i = 0; i < getInnerPos(); i++) {
            if (getPointAt(i) > 0) {
                return false;
            }
        }
        return true;
    }

}
