package com.github.tkshio.jgammon.common.utils;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 先手・後手の対を記述するインターフェース
 *
 * @param <T> 管理対象となるオブジェクトのクラス
 * @see RWTuple
 */
public interface SGTuple<T> {

    /**
     * ファクトリーメソッド
     *
     * @param sente 先手オブジェクト
     * @param gote  後手オブジェクト
     * @param <T>   先手・後手のオブジェクトのクラス
     * @return 生成されたSGTuple
     */
    static <T> SGTuple<T> of(T sente, T gote) {
        return new SGTuple<>() {
            @Override
            public T sente() {
                return sente;
            }

            @Override
            public T gote() {
                return gote;
            }
        };
    }

    /**
     * 先手・後手を区別せず、共通のオブジェクトを共用する場合のファクトリーメソッド
     *
     * <p> applyEachやapplyでは、同じオブジェクトが2回処理される。
     *
     * @param self 共用されるオブジェクト
     * @param <T>  共用されるオブジェクトの型
     * @return 生成されたSGTuple
     */
    static <T> SGTuple<T> of(T self) {
        return of(self, self);
    }

    /**
     * 先手側のオブジェクトを返す
     *
     * @return 先手側のオブジェクト
     */
    T sente();

    /**
     * 後手側のオブジェクトを返す
     *
     * @return 後手側のオブジェクト
     */
    T gote();

    /**
     * 先手・後手双方に同じ処理を適用する
     *
     * @param consumer 先手・後手のオブジェクトを受け取る関数
     */
    default void applyEach(Consumer<T> consumer) {
        consumer.accept(sente());
        consumer.accept(gote());
    }

    /**
     * 先手・後手の両オブジェクトを変換して、新たなSGTupleを生成する
     *
     * @param converter 変換関数
     * @param <S>       変換後のクラス
     * @return 生成されたSGTuple
     */
    default <S> SGTuple<S> apply(Function<T, S> converter) {
        return of(converter.apply(sente()), converter.apply(gote()));
    }
}
