package com.github.tkshio.jgammon.common.evaluator;

import com.github.tkshio.jgammon.common.context.GameContextHandler.TebanContextHandler;
import com.github.tkshio.jgammon.common.node.Node;

import java.util.Collection;

/**
 * 与えられたノードを評価するインターフェース
 *
 * <p>先読みした局面が構築されていれば、ノードから子局面（および存在すれば
 * それ以降の局面も）を取得して、評価対象として使用できる
 *
 * @param <STATE> 局面を表すオブジェクトのクラス
 */
public interface NodesEvaluator<STATE> {

    /**
     * 与えられたノードを評価する
     *
     * @param stateNode      評価対象のノード
     * @param candidates     評価対象のノードの子局面を保持するノード
     * @param contextHandler コンテキストハンドラ
     * @param <T>            ノードの型
     * @return candidatesの中から選択されたノードと、その評価値で構成したEvalオブジェクト
     */
    <T extends Node<STATE>>
    EvaluatedNode<T> evaluate(T stateNode,
                              Collection<T> candidates,
                              TebanContextHandler<STATE> contextHandler);

    /**
     * ゲームの初期局面を受け取る
     *
     * @param initialState 初期局面（先手観点）
     */
    default void initialState(STATE initialState) {
    }


    /**
     * 勝利した
     *
     * @param state 勝利局面（勝者観点）
     */
    default void won(STATE state) {
    }

    /**
     * 敗北した
     *
     * @param state 敗北局面（勝者観点）
     */
    default void lost(STATE state) {
    }

    /**
     * 引き分けとなった
     *
     * @param state 引き分けの局面（引き分け決定時のプレイヤーの観点）
     */
    default void draw(STATE state) {

    }

    /**
     * 手番が多くなりすぎた場合など、対局が中断した
     */
    default void abort() {
    }

    /**
     * ContextHandlerに渡す出力用の文字列、通常はプレイヤー名を返す
     *
     * @return 出力文字列
     */
    String getLabel();
}
