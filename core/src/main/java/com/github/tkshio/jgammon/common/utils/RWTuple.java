package com.github.tkshio.jgammon.common.utils;


import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Red/Whiteの対を記述するインターフェース
 *
 * <p> 将棋やチェスでは先手・後手と座標の関係が固定されているが、
 * バックギャモンでは先手・後手はオープニングロールによって定まり、固定されていない
 * <p>
 * Red/Whiteは、表示上常にRedが画面の上、Whiteが下と固定されていて、そのペアを
 * 記述しやすくするためのインターフェースである。
 *
 * @param <T> 管理対象となるオブジェクトのクラス
 */
public interface RWTuple<T> {
    /**
     * 管理するオブジェクトにconveterを適用し、変換結果から新たなRWTupleを構築する
     *
     * @param t         適用対象
     * @param converter 変換を行う関数
     * @param <T>       変換元の型
     * @param <S>       返還後の型
     * @return 変換されたオブジェクトを保持するRWTuple
     */
    static <T, S> RWTuple<S> of(RWTuple<T> t, Function<T, S> converter) {
        return of(converter.apply(t.red()), converter.apply(t.white()));
    }

    /**
     * 指定されたsupplierを2回呼び、それぞれの結果をRed,WhiteとしてRWTupleを構築する
     *
     * @param supplier 呼び出すメソッド
     * @param <T>      supplierが生成するオブジェクトのクラス
     * @return 生成されたRWTuple
     */
    static <T> RWTuple<T> from(Supplier<T> supplier) {
        return of(supplier.get(), supplier.get());
    }

    static <T> RWTuple<T> of(T red, T white) {
        return new RWTuple<>() {
            @Override
            public T red() {
                return red;
            }

            @Override
            public T white() {
                return white;
            }
        };
    }

    /**
     * Red側のオブジェクトを返す
     *
     * @return Red側オブジェクト
     */
    T red();

    /**
     * White側のオブジェクトを返す
     *
     * @return White側オブジェクト
     */
    T white();

    /**
     * Redを先手とする {@link SGTuple}を返す
     *
     * @return 生成されたSGTuple
     */
    default SGTuple<T> redGoesFirst() {
        return SGTuple.of(red(), white());
    }

    /**
     * Whiteを先手とする {@link SGTuple}を返す
     *
     * @return 生成されたSGTuple
     */
    default SGTuple<T> whiteGoesFirst() {
        return SGTuple.of(white(), red());
    }
}
