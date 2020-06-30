package com.github.tkshio.jgammon.gammon.move;


/**
 * RollApplierで可能なムーブを列挙する上での、必要最小限なメソッドの定義
 */
public interface BGBoard {

    /**
     * 任意の場所のコマ数を取得する。
     *
     * @param pos 取得したい場所、0はバーポイント、getBearOffPos()の返り値の場所は相手のバーポイント
     * @return 指定された場所のコマ数
     */
    int getPointAt(int pos);


    /**
     * 任意の場所の相手のコマ数を取得する。
     *
     * @param pos getPointAt()と同様、ただしバーポイント（自分・相手問わず）に対しては未定義でよい
     * @return 指定された場所の相手の駒数
     */

    default int getOpponentPointAt(int pos) {
        return -getPointAt(pos);
    }

    /**
     * ベアリングオフの行き先の場所を返す
     *
     * @return 25、またはボードサイズに応じた値
     */
    int getBearOffPos();


    /**
     * ベアリングオフ可能な状態かどうか
     *
     * @return true ベアリングオフが可能
     * false ベアリングオフはできない
     */
    boolean isInBearingOff();
}
