package com.github.tkshio.jgammon.common.evaluator;

import lombok.Data;

/**
 * 評価値と、その対象となったオブジェクトのペアを保持する
 *
 * @param <T> 保持対象のオブジェクトのクラス
 */
@Data
public class EvaluatedNode<T> {
    private final T stateNode;
    private final Eval eval;
}
