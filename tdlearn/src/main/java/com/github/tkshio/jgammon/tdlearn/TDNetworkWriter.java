package com.github.tkshio.jgammon.tdlearn;

import com.github.tkshio.jgammon.tdlearn.matrix.Matrix;
import com.github.tkshio.jgammon.tdlearn.nn.NNLayer;
import com.github.tkshio.jgammon.tdlearn.nntd.NNLayerExposer;
import com.github.tkshio.jgammon.tdlearn.td.TDLearner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * {@link TDLearner}をファイルに出力する
 */
public class TDNetworkWriter {
    private final NNLayerExposer tdn;


    private TDNetworkWriter(NNLayerExposer tdn) {
        this.tdn = tdn;
    }

    /**
     * オブジェクトを生成する
     *
     * @param tdn 出力対象となる{@link TDLearner}オブジェクト
     * @param <M> 行列を表現するクラス
     * @return 生成されたオブジェクト
     */
    public static <M> TDNetworkWriter create(NNLayerExposer tdn) {
        return new TDNetworkWriter(tdn);
    }

    /**
     * 指定されたファイルに出力する
     *
     * <p>出力したファイルは、{@link TDLearnEvaluatorReader} で読み込むことができる。
     *
     * <p>なお、学習機能を維持した状態で読み込む機能は未実装。
     *
     * @param file 出力先ファイル
     * @throws FileNotFoundException 指定されたファイルが存在しない
     */
    public void write(File file/*TODO: METAINFO for learning*/) throws FileNotFoundException {
        try (PrintStream out = new PrintStream(new FileOutputStream(file))) {
            write(out, tdn.getHiddenLayer(), "hidden");
            write(out, tdn.getOutputLayer(), "output");
        }
    }

    private void write(PrintStream out, NNLayer<?> nnLayer, String label) {
        Matrix<?> weight = nnLayer.getWeight();
        write(out, weight, label + "_weight");
        Matrix<?> bias = nnLayer.getBias();
        write(out, bias, label + "_bias");
    }

    private void write(PrintStream out, Matrix<?> matrix, String label) {
        out.println("// " + label);
        double[][] data = matrix.getData();
        for (var row : data) {
            out.println(
                    Arrays.stream(row)
                            .mapToObj(Double::toString)
                            .collect(Collectors.joining(",")));
        }
        out.println("");
    }
}
