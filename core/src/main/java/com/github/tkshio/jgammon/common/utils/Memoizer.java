package com.github.tkshio.jgammon.common.utils;

import java.util.function.Supplier;

/**
 * 一度返した結果を記憶しておく、{@link Supplier}のラッパー
 */
public interface Memoizer {

    /**
     * Supplierをラップする
     *
     * <p> 初回の呼び出しはSupplierの返値を返すが、二回目以降は
     * Supplierを呼ばず、内部で保持しておいた値を返す
     *
     * @param supplier 対象のSupplier
     * @param <T>      Supplierが返すオブジェクトの型
     * @return ラップされたオブジェクト
     */
    static <T> Supplier<T> memoize(Supplier<T> supplier) {
        return new Supplier<>() {
            private Supplier<T> _supplier = () -> {
                T value = supplier.get();
                this._supplier = () -> value;
                return value;
            };

            public T get() {
                return _supplier.get();
            }
        };
    }
}
