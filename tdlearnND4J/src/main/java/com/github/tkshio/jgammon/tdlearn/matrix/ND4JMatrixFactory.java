package com.github.tkshio.jgammon.tdlearn.matrix;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

public class ND4JMatrixFactory implements MatrixFactory<INDArray> {
    @Override
    public Matrix<INDArray> create(double[][] values) {
        INDArray indArray = Nd4j.create(values);
        return wrap(indArray);
    }

    private Matrix<INDArray> wrap(INDArray indArray) {
        return new Matrix<>() {
            @Override
            public Matrix<INDArray> multiply(Matrix<INDArray> m) {
                return wrap(indArray.mmul(m.unbox()));
            }

            @Override
            public Matrix<INDArray> add(Matrix<INDArray> m) {
                return wrap(indArray.add(m.unbox()));
            }

            @Override
            public Matrix<INDArray> sigmoid() {
                return wrap(Transforms.sigmoid(indArray));
            }

            @Override
            public double[][] getData() {
                return new double[][]{indArray.toDoubleVector()};
            }

            @Override
            public Matrix<INDArray> scalarMultiply(double v) {
                return wrap(indArray.mul(v));
            }

            @Override
            public Matrix<INDArray> hadamard_product(Matrix<INDArray> m) {
                return wrap(indArray.mul(m.unbox()));
            }

            @Override
            public Matrix<INDArray> scalarAdd(double v) {
                return wrap(indArray.add(v));
            }

            @Override
            public Matrix<INDArray> transpose() {
                return wrap(indArray.transpose());
            }

            @Override
            public INDArray unbox() {
                return indArray;
            }

            @Override
            public int rows() {
                return indArray.rows();
            }

            @Override
            public int columns() {
                return indArray.columns();
            }

            @Override
            public Matrix<INDArray> getColumnMatrix(int col) {
                return wrap(indArray.getColumn(col));
            }

            @Override
            public Matrix<INDArray> getRowMatrix(int row) {
                return wrap(indArray.getRow(row));
            }

            @Override
            public Matrix<INDArray> hadamard_product(double[][] array) {
                return wrap(indArray.mul(Nd4j.create(array)));
            }

            @Override
            public Matrix<INDArray> sigmoidDev() {
                return wrap(Transforms.sigmoidDerivative(indArray));
            }
        };

    }
}
