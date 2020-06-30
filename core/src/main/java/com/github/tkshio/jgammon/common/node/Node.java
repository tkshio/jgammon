package com.github.tkshio.jgammon.common.node;

import java.util.Collection;

/**
 * 局面と、その次にありうる局面（子局面）を管理する
 *
 * @param <STATE> 管理対象となるオブジェクトのクラス
 */
public interface Node<STATE> {
    /**
     * 保持している局面を返す
     *
     * @return 局面を表すオブジェクト
     */
    STATE getState();

    /**
     * 子局面をコレクションのコレクションとして返す
     *
     * <p>バックギャモンではロール別に局面を管理するため、二重のコレクションになっている
     *
     * @return 子局面を重み付きのキーでグループ化したコレクション
     */
    Collection<ChildNodesEntry<STATE>> getChildNodesEntries();


    /**
     * グループ別に分けて管理されている子局面の、各グループを表す
     *
     * @param <STATE> 管理対象となるオブジェクトのクラス
     */
    interface ChildNodesEntry<STATE> {
        /**
         * このグループの、ほかのグループに対する相対的な重み
         *
         * <p>重みはグループ間で相対的な関係が定まっていればよく、値の範囲は任意でよい。
         * バックギャモンの場合、これによりロールの出現率に対応した重み付けを行っている。
         *
         * @return 重み
         */
        default double getWeight() {
            return 1.0;
        }

        /**
         * このグループに所属する子局面のコレクション
         *
         * @return 子局面のコレクション
         */
        Collection<? extends Node<STATE>> getNodes();
    }
}
