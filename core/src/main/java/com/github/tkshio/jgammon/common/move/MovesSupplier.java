package com.github.tkshio.jgammon.common.move;

import java.util.Collection;

/**
 * 可能なムーブを列挙する機能と、その実装を提供する
 *
 * @param <BOARD> 盤面を表すクラス
 * @param <MOVE>  ムーブを表すクラス
 * @param <KEY>   ムーブの列挙にあたって、引数となるクラス
 */
public interface MovesSupplier<BOARD, MOVE, KEY> {

    /**
     * 与えられた盤面の、指定位置以降（含む）の駒について可能なムーブを列挙する
     *
     * <p>fromは、可能なムーブを列挙する際、同内容のムーブが重複して
     * 列挙されるのを防止するために使用される。
     *
     * @param board 対象となる盤面
     * @param from  対象となる位置（この位置の駒も列挙対象に含む）
     * @param key   列挙にあたって、引数となるオブジェクト（バックギャモンであればロールの目）
     * @return 可能なムーブのリスト、ない場合は空リストが返る
     */

    Collection<MOVE> listupPossibleMoves(BOARD board, int from, KEY key);

}
