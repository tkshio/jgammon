package com.github.tkshio.jgammon.gammon;

/**
 * Redは{@literal 24->1}、Whiteは{@literal 1->24}へと駒を進める座標系のBackgammonBoard
 *
 * <p> 型を明確に区別するため、BackgammonBoardにはあえてこのインターフェースを実装させていない
 */
public interface AbsoluteBackgammonBoard {

    /**
     * {@code BackgammonBoard}のラッパーメソッド
     *
     * @param board 対象となるBackgammonオブジェクト
     * @return ラップされたオブジェクト
     */
    static AbsoluteBackgammonBoard absolute(BackgammonBoard board) {
        return new AbsoluteBackgammonBoard() {
            @Override
            public int size() {
                return board.size();
            }

            @Override
            public int getPointAt(int pos) {
                return board.getPointAt(pos);
            }

            @Override
            public int getPointsCount() {
                return board.getPointsCount();
            }
        };
    }

    /**
     * バーポイントを含めた、ポイントの総数。すなわち、getPointAtの引数として指定可能な値+１を返す
     *
     * @return 26、またはボードサイズに応じた値
     */
    int size();

    /**
     * 指定のポイントの駒数を得る
     *
     * @param pos 取得したい場所
     * @return 指定された場所のコマ配置、白側は正整数、赤側は負整数で返る。
     */
    int getPointAt(int pos);

    /**
     * 表示するポイントの数を返す
     *
     * @return 24、またはボードサイズに応じた値
     */
    int getPointsCount();

}
