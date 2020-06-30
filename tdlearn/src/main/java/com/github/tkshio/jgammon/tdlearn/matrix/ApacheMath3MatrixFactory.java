package com.github.tkshio.jgammon.tdlearn.matrix;

import org.apache.commons.math3.analysis.function.Sigmoid;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealMatrixChangingVisitor;

import java.util.function.Function;

public class ApacheMath3MatrixFactory implements MatrixFactory<RealMatrix> {
    private final static Sigmoid s = new Sigmoid();

    @Override
    public Matrix<RealMatrix> create(double[][] values) {
        RealMatrix m = MatrixUtils.createRealMatrix(values);
        return wrap(m);
    }

    private Matrix<RealMatrix> wrap(RealMatrix mx) {
        return new Matrix<>() {
            @Override
            public Matrix<RealMatrix> multiply(Matrix<RealMatrix> m) {
                return wrap(mx.multiply(m.unbox()));
            }

            @Override
            public Matrix<RealMatrix> add(Matrix<RealMatrix> m) {
                return wrap(mx.add(m.unbox()));
            }

            @Override
            public Matrix<RealMatrix> sigmoid() {
                return apply(s::value);
            }

            @Override
            public double[][] getData() {
                return mx.getData();
            }

            @Override
            public Matrix<RealMatrix> scalarMultiply(double v) {
                return wrap(mx.scalarMultiply(v));
            }

            @Override
            public Matrix<RealMatrix> hadamard_product(Matrix<RealMatrix> m) {
                int rows = mx.getRowDimension();
                int columns = mx.getColumnDimension();
                RealMatrix m2 = m.unbox();
                if (m2.getRowDimension() != rows || m2.getColumnDimension() != columns) {
                    throw new IllegalArgumentException();
                }
                RealMatrix result = MatrixUtils.createRealMatrix(new double[mx.getRowDimension()][mx.getColumnDimension()]);

                result.walkInOptimizedOrder((LocalRMCV) (row, column, value) -> mx.getEntry(row, column) * m2.getEntry(row, column));

                return wrap(result);
            }

            @Override
            public Matrix<RealMatrix> scalarAdd(double v) {
                return wrap(mx.scalarAdd(v));
            }

            @Override
            public Matrix<RealMatrix> transpose() {
                return wrap(mx.transpose());
            }

            @Override
            public RealMatrix unbox() {
                return mx;
            }

            @Override
            public int rows() {
                return mx.getRowDimension();
            }

            @Override
            public int columns() {
                return mx.getColumnDimension();
            }

            @Override
            public Matrix<RealMatrix> getColumnMatrix(int col) {
                return wrap(mx.getColumnMatrix(col));
            }

            @Override
            public Matrix<RealMatrix> getRowMatrix(int row) {
                return wrap(mx.getRowMatrix(row));
            }

            @Override
            public Matrix<RealMatrix> hadamard_product(double[][] array) {
                RealMatrix result = MatrixUtils.createRealMatrix(new double[mx.getRowDimension()][mx.getColumnDimension()]);
                result.walkInOptimizedOrder((LocalRMCV) (row, column, value) -> mx.getEntry(row, column) * array[row][column]);

                return wrap(result);
            }

            @Override
            public Matrix<RealMatrix> sigmoidDev() {
                return apply(v -> v * (1 - v));
            }


            private Matrix<RealMatrix> apply(Function<Double, Double> f) {
                RealMatrix result = MatrixUtils.createRealMatrix(new double[mx.getRowDimension()][mx.getColumnDimension()]);
                result.walkInOptimizedOrder((LocalRMCV) (row, column, value) -> f.apply(mx.getEntry(row, column)));

                return wrap(result);
            }

            @Override
            public String toString() {
                return mx.toString();
            }
        };
    }


    private interface LocalRMCV extends RealMatrixChangingVisitor {
        default void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn) {
        }

        default double end() {
            return 0;
        }

    }
}