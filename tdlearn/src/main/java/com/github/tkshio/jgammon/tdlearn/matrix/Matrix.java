package com.github.tkshio.jgammon.tdlearn.matrix;

/**
 * 行列演算を提供するインターフェース
 *
 * <p>効率はあまり重視していない
 *
 * @param <T> 行列を表現するクラス
 */
public interface Matrix<T> {

    Matrix<T> multiply(Matrix<T> hiddenLayer);

    Matrix<T> add(Matrix<T> hiddenLayer);

    Matrix<T> sigmoid();

    double[][] getData();

    Matrix<T> scalarMultiply(double v);

    Matrix<T> hadamard_product(Matrix<T> output);

    Matrix<T> scalarAdd(double v);

    Matrix<T> transpose();

    T unbox();

    int rows();

    int columns();

    Matrix<T> getColumnMatrix(int col);

    Matrix<T> getRowMatrix(int row);

    Matrix<T> hadamard_product(double[][] array);

    Matrix<T> sigmoidDev();

}
