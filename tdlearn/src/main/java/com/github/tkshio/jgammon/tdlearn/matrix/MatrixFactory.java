package com.github.tkshio.jgammon.tdlearn.matrix;

public interface MatrixFactory<T> {

    Matrix<T> create(double[][] values);
}
