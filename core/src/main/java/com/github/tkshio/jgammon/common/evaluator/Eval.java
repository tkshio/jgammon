package com.github.tkshio.jgammon.common.evaluator;

/**
 * 局面の評価値を表す
 */
public interface Eval extends Comparable<Eval> {
    /**
     * 評価結果について、ログ等に出力するテキストを返す
     *
     * @return テキスト
     */
    default String asString() {
        return "";
    }

    /**
     * 評価値を返す
     *
     * <p>評価値が最大の手が選ばれる。評価ロジック内での一貫性があれば、
     * 値の範囲は任意でよい。ただし、{@link TwoPlyPlayer} では
     * 複数の局面で平均値をとるなど一定の処理がありうるので、
     * 相応の精度が必要となる
     *
     * @return 評価値
     */
    double getScore();

    /**
     * 評価値同士を比較する
     *
     * @param eval 比較対象
     * @return 比較結果
     */
    default int compareTo(Eval eval) {
        return Double.compare(getScore(), eval.getScore());
    }

    /**
     * 最終的にプレイヤーによってこの評価を持つ手が選ばれた時に呼び出されるコールバックメソッド
     *
     * <p> フィードバックを得るため、評価時の内部的な計算結果を渡すために使用している。
     */
    default void markAsChoice() {
    }
}
