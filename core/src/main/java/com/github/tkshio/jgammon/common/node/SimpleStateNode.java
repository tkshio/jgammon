package com.github.tkshio.jgammon.common.node;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * 子ノードを単一のグループで管理するNodeの実装
 *
 * @param <STATE> 管理対象のオブジェクトのクラス
 */
public class SimpleStateNode<STATE> implements Node<STATE> {
    protected final STATE state;
    private final Collection<SimpleStateNode<STATE>> children;

    protected SimpleStateNode(STATE state, Collection<SimpleStateNode<STATE>> children) {
        this.state = state;
        this.children = children;
    }

    /**
     * ファクトリーメソッド
     *
     * @param state   ルートノードとなるオブジェクト
     * @param <STATE> ルートノードとなるオブジェクトのクラス
     * @return ルートノードだけからなるツリー
     */
    public static <STATE> SimpleStateNode<STATE> initialNode(STATE state) {
        return new LeafSimpleStateNode<>(state);
    }

    @Override
    public STATE getState() {
        return state;
    }

    @Override
    public Collection<ChildNodesEntry<STATE>> getChildNodesEntries() {
        return Collections.singleton(() -> children);
    }

    /**
     * 自身の末端のノード全てに対し、leafStatesFactory を適用して子ノードを追加し、
     * 新しいツリーとして再構成したツリーを返す
     *
     * @param leafStatesFactory 子ノードを生成する関数
     * @return 新しいノードが追加されたツリー
     */
    public SimpleStateNode<STATE> grow(LeafStatesFactory<STATE> leafStatesFactory) {
        // 末端でないノードは、子ノードに子の処理を丸投げして、
        // 結果をまとめなおして新しいノードを作る
        Collection<SimpleStateNode<STATE>> updatedChildren =
                children.stream()
                        .map(child -> child.grow(leafStatesFactory))
                        .collect(Collectors.toList());

        return new SimpleStateNode<>(state, updatedChildren);
    }

    /**
     * 子ノードのコレクションを得る
     *
     * @return 子ノードのコレクション
     */
    Collection<SimpleStateNode<STATE>> getChildren() {
        return children;
    }

    /**
     * 子ノードのファクトリーメソッドのインターフェース
     *
     * @param <STATE> 管理対象のオブジェクトのクラス
     */
    public interface LeafStatesFactory<STATE> {
        default Collection<STATE> create(SimpleStateNode<STATE> node) {
            return create(node.getState());
        }

        Collection<STATE> create(STATE state);
    }
}

/**
 * 末端ノードを表すクラス
 *
 * @param <STATE> 管理対象のオブジェクトのクラス
 */
class LeafSimpleStateNode<STATE> extends SimpleStateNode<STATE> {
    protected LeafSimpleStateNode(STATE state) {
        super(state, Collections.emptyList());
    }

    @Override
    public SimpleStateNode<STATE> grow(LeafStatesFactory<STATE> leafStatesFactory) {

        // 子ノードを増やしたので、自分自身はLeafではなく普通のノードになる
        Collection<SimpleStateNode<STATE>> newChildren =
                leafStatesFactory.create(this)
                        .stream()
                        .map(LeafSimpleStateNode::new)
                        .collect(Collectors.toList());

        return new SimpleStateNode<>(state, newChildren);
    }
}

