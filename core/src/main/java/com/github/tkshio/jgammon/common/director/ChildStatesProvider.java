package com.github.tkshio.jgammon.common.director;

import com.github.tkshio.jgammon.common.node.Node;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 局面を表すノードに対して、次の局面の候補を提供するインターフェース
 *
 * @param <T> ノードを表すクラス
 */
public interface ChildStatesProvider<T extends Node<?>> {

    /**
     * 子局面のコレクションをあらかじめ生成しておくChildStatesProviderを構築する
     *
     * <p>次の局面候補を返す際、同時に末端ノードに新たな局面を生成している。
     *
     * @param childStatesGetter 子局面を生成する関数
     * @param nodeGrower        末端ノードを延ばす関数
     * @param <NODE>            ノードを表すクラス
     * @return 生成されたChildStatesProviderオブジェクト
     */
    static <NODE extends Node<?>> ChildStatesProvider<NODE> createBuildAheadProvider(
            Function<NODE, Collection<NODE>> childStatesGetter,
            Function<NODE, NODE> nodeGrower
    ) {
        return node -> {
            // 与えられたノードから、（すでに生成済みの）子ノード群を得る
            var childStates = childStatesGetter.apply(node);

            // 各子ノードに、新たに先読みした子局面をぶら下げてから返す
            return childStates.stream()
                    .map(nodeGrower)
                    .collect(Collectors.toUnmodifiableList());
        };
    }

    /**
     * 子局面のコレクションを動的に生成するChildStatesProviderを構築する
     *
     * @param firstChildStatesFactory 開始局面の時に使う子局面生成関数
     * @param childStatesFactory      通常局面につかう、子局面生成関数
     * @param nodeBuilder             ノード構築関数
     * @param <NODE>                  ノードを表すクラス
     * @param <STATE>                 局面を表すクラス
     * @return 構築されたChildStatesProvider
     */
    static <NODE extends Node<STATE>, STATE> ChildStatesProvider<NODE> createOndemandProvider(
            Function<STATE, Collection<STATE>> firstChildStatesFactory,
            Function<STATE, Collection<STATE>> childStatesFactory,
            Function<Collection<STATE>, Collection<NODE>> nodeBuilder
    ) {
        return new ChildStatesProvider<>() {
            @Override
            public Collection<NODE> childStates(NODE node) {
                // 現局面から次の局面を生成する
                var generatedNextStates = childStatesFactory.apply(node.getState());

                // 生成した局面をノードにラップして返す
                return nodeBuilder.apply(generatedNextStates);
            }

            @Override
            public Collection<NODE> firstChildStates(NODE node) {

                // 初期状態では、それ用の生成関数を適用する
                var generatedNextStates = firstChildStatesFactory.apply(node.getState());
                return nodeBuilder.apply(generatedNextStates);
            }

        };
    }

    /**
     * 初期状態（開始局面を持つノード）から、初手の候補を格納したノードのコレクションを返す
     *
     * @param node 初期状態のノード
     * @return 子局面のコレクション
     */
    default Collection<T> firstChildStates(T node) {
        return childStates(node);
    }

    /**
     * ある対局状態を表すノードから、次の手の候補を格納したノードのコレクションを返す
     *
     * @param node 対局状態を表すノード
     * @return 子局面のコレクション
     */
    Collection<T> childStates(T node);

}
