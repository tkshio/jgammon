package com.github.tkshio.jgammon.common.node;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 子ノードをキーでグループ分けして管理するツリー
 *
 * <p> 各ノードは、管理するオブジェクトとKEY別に管理された子ノードに加え、
 * KEYのSupplierを備えている。
 *
 * <p>バックギャモンの場合、ロールがKEYとなり、{@code getNextStates()}で次の子ノードに
 * 移るときにSupplierが呼ばれることで実際のロールが行われ、選択可能なノードが絞り込まれる。
 *
 * @param <STATE> 管理対象となるオブジェクトのクラス
 * @param <KEY>   キーとなるクラス
 */
public class IndexedStateNode<STATE, KEY extends IndexedStateNode.HasWeight> implements Node<STATE> {

    protected final STATE state;
    protected final Supplier<KEY> keySupplier;
    private final Map<KEY, Collection<IndexedStateNode<STATE, KEY>>> index;

    protected IndexedStateNode(STATE state, Map<KEY, Collection<IndexedStateNode<STATE, KEY>>> index, Supplier<KEY> keySupplier) {
        this.state = state;
        this.index = index;
        this.keySupplier = keySupplier;
    }

    /**
     * ファクトリーメソッド
     *
     * @param <STATE>     管理対象となるオブジェクトのクラス
     * @param <KEY>       キーとなるクラス
     * @param state       ルートノードとなるオブジェクト
     * @param keySupplier ルートノードの子ノードを選ぶときに使用するキー
     * @return ルートノードだけからなるツリー
     */
    public static <STATE, KEY extends HasWeight> IndexedStateNode<STATE, KEY> initialNode(STATE state, Supplier<KEY> keySupplier) {
        return new LeafIndexedStateNode<>(state, keySupplier);
    }

    /**
     * 子局面のコレクションに、それぞれ共通のキー生成源を紐づけて
     * 末端ノードを生成する
     *
     * @param stateCollection 子局面のコレクション
     * @param keySupplier     キー生成源
     * @param <STATE>         管理対象となるオブジェクトのクラス
     * @param <KEY>           キーとなるクラス
     * @return 生成されたノードのコレクション
     */
    public static <STATE, KEY extends HasWeight>
    Collection<IndexedStateNode<STATE, KEY>> wrapAsNodes(
            Collection<STATE> stateCollection,
            Supplier<KEY> keySupplier) {

        return stateCollection.stream()
                .map(state -> new LeafIndexedStateNode<>(state, keySupplier))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public STATE getState() {
        return state;
    }

    @Override
    public Collection<ChildNodesEntry<STATE>> getChildNodesEntries() {
        return index.entrySet().stream().map(
                (entry) -> new ChildNodesEntry<STATE>() {
                    @Override
                    public double getWeight() {
                        return entry.getKey().getWeight();
                    }

                    @Override
                    public Collection<? extends Node<STATE>> getNodes() {
                        return entry.getValue();
                    }
                }
        ).collect(Collectors.toList());
    }

    /**
     * 自身の末端のノード全てに対し、leafStatesFactory を適用して子ノードを追加し、
     * 新しいツリーとして再構成したツリーを返す
     *
     * @param keys              KEYをすべて列挙したコレクション
     * @param newKeySupplier    新たに生成するノードに保持させるキーの生成源
     * @param leafStatesFactory 子ノードを生成する関数
     * @return 新しいノードが追加されたツリー
     */
    public IndexedStateNode<STATE, KEY> grow(
            Collection<KEY> keys,
            Supplier<KEY> newKeySupplier,
            LeafStatesFactory<STATE, KEY> leafStatesFactory) {

        // SimpleStateNode同様、末端ノード以外では子ノードに丸投げするだけ
        Map<KEY, Collection<IndexedStateNode<STATE, KEY>>> updatedIndex =
                new HashMap<>();

        index.forEach((key, children) -> {
            Collection<IndexedStateNode<STATE, KEY>> updatedChildren = children.stream()
                    .map(child -> child.grow(keys, newKeySupplier, leafStatesFactory))
                    .collect(Collectors.toList());
            updatedIndex.put(key, updatedChildren);
        });

        return new IndexedStateNode<>(state, updatedIndex, this.keySupplier);
    }

    /**
     * キーのコレクションを取得する
     *
     * @return キーのコレクション
     */
    Collection<KEY> getKeys() {
        return index.keySet();
    }

    /**
     * 次の局面の候補をキーで絞りこみ、提供する
     *
     * @return 次の局面の候補のコレクション
     */
    public Collection<IndexedStateNode<STATE, KEY>> getNextStates() {
        // バックギャモンでは、ここが ChildStatesProvider#childStates()で呼ばれることにより、
        // ロールが行われることになる
        KEY key = keySupplier.get();

        return index.computeIfAbsent(key, unknownKey -> {
            var msg = MessageFormat.format(
                    "Unknown key{0} specified", unknownKey.toString());
            throw new IllegalStateException(msg);
        });
    }

    /**
     * {@link com.github.tkshio.jgammon.common.node.Node.ChildNodesEntry ChildNodesEntry} に対応させるためのインターフェース
     */
    public interface HasWeight {
        /**
         * 重みを返す
         *
         * @return 重み
         * @see com.github.tkshio.jgammon.common.node.Node.ChildNodesEntry
         */
        double getWeight();
    }

    /**
     * 子ノードのファクトリーメソッドのインターフェース
     *
     * @param <STATE> 管理対象となるオブジェクトのクラス
     * @param <KEY>   キーとなるクラス
     */
    public interface LeafStatesFactory<STATE, KEY extends HasWeight> {
        default Collection<STATE> create(IndexedStateNode<STATE, KEY> node, KEY key) {
            return create(node.getState(), key);
        }

        Collection<STATE> create(STATE state, KEY key);
    }
}

/**
 * 末端ノードを表すクラス
 *
 * @param <STATE> 管理対象のオブジェクトのクラス
 * @param <KEY>   キーとなるクラス
 */
class LeafIndexedStateNode<STATE, KEY extends IndexedStateNode.HasWeight> extends IndexedStateNode<STATE, KEY> {
    LeafIndexedStateNode(STATE state, Supplier<KEY> keySupplier) {
        super(state, Collections.emptyMap(), keySupplier);
    }

    @Override
    public IndexedStateNode<STATE, KEY> grow(
            Collection<KEY> keys,
            Supplier<KEY> newKeySupplier,
            LeafStatesFactory<STATE, KEY> leafStatesFactory) {

        HashMap<KEY, Collection<IndexedStateNode<STATE, KEY>>> leaves =
                new HashMap<>();

        // 列挙されたキーのそれぞれについて、子ノードを生成する
        keys.forEach(key -> {
            var leaveStates = leafStatesFactory.create(this, key);

            // 生成された子ノードに、上位から渡されたキー生成源を紐付ける
            leaves.put(key, wrapAsNodes(leaveStates, newKeySupplier));
        });

        // 新たに生成された子ノードをまとめて、自分自身を作り直すので
        // ここではnewKeySupplierではなく、自分自身がそれまで持っていたkeySupplierを
        // そのまま渡す
        return new IndexedStateNode<>(this.getState(), leaves, this.keySupplier);
    }

}

