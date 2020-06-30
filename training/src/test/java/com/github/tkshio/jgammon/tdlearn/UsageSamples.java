package com.github.tkshio.jgammon.tdlearn;

import com.github.tkshio.jgammon.app.Main;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

// コマンドラインの実行例
public class UsageSamples {

    // 訓練コマンド

    // 最小限の訓練
    @Test
    @Ignore
    public void run_training() {
        Main.parseAndRun("train", "3");
    }

    // 経過表示あり
    @Test
    @Ignore
    public void run_training_verbose() {
        Main.parseAndRun("train", "-v", "3");
    }

    // 訓練と対局
    @Test
    @Ignore
    public void run_training_then_sampleGame() {
        Main.parseAndRun("train", "--max", "20", "3", "5");
    }

    // 1万回も対局すれば、ある程度まともな打ち手になる
    @Test
    @Ignore
    public void run_training_10000timesShouldMakeImprovements() {
        Main.parseAndRun("train", "-v", "10000", "3");
    }

    // td_default.txt生成に使用
    @Test
    @Ignore
    public void run_training_200k_5set() {
        String n = "200000";
        String s = "10";
        Main.parseAndRun("train",
                "--log", "out/200k.log",
                "--xg", "out/200k_xg.txt",
                "--out", "out/200kdump.log",
                "--node", "200",
                "10", "10",
                n, s, n, s, n, s, n, s, n, s);
    }

    // シード値指定時の動作の確認
    @Test
    @Ignore
    public void run_training_with_Seed_thrice() {
        Main.parseAndRun("train", "--seed", "1", "3", "2");
        Main.parseAndRun("train", "--seed", "1", "3", "2");
        Main.parseAndRun("train", "--seed", "1", "3", "2");
    }

    // ファイルに出力する例：ログファイル
    @Test
    @Ignore
    public void run_training_logsToFile() {
        File out = new File("out/test.log");
        assertTrue(!out.exists() || out.delete());
        Main.parseAndRun("train", "--log", "out/test.log", "10", "3");
        assertTrue(out.exists());
    }

    // ファイルに出力する例： ダンプファイル
    @Test
    @Ignore
    public void run_training_withDump() {
        File dump1 = new File("out/testdump_1.log");
        File dump2 = new File("out/testdump_2.log");
        assertTrue(!dump1.exists() || dump1.delete());
        assertTrue(!dump1.exists() || dump2.delete());
        Main.parseAndRun("train", "--out", "out/testdump.log", "10", "3", "1", "2");
        assertTrue(dump1.exists());
        assertTrue(dump2.exists());
    }


    // ファイルに出力する例： 訓練ののち、対局
    @Test
    @Ignore
    public void run_training_and_game() {
        String file = "out/dump.txt";
        Main.parseAndRun("train", "--out", file, "10");
        assertTrue(new File(file).exists());
        Main.parseAndRun("run", "--alg", file, "random", "--depth", "1", "1", "3");
    }

    // ほかのファイルを読み込んで訓練再開、結果を別ファイルに
    @Test
    @Ignore
    public void run_training_resumeAndWriteToAnotherFile() {
        String fileIn = "out/trial20K_200614/dump3.txt";
        String fileout = "out/dump_resume.txt";
        Main.parseAndRun("train", "--in", fileIn, "--out", fileout, "-v", "2", "5");
    }

    // ND4Jを使ってみる
    @Test
    @Ignore
    public void run_training_withND4J() {
        Main.parseAndRun("train", "--nd4j", "-v", "100", "3");
    }

    // 対局コマンド、XGに出力
    @Test
    @Ignore
    public void run_default() {
        Main.parseAndRun("run", "--seed", "0", "--alg", "td", "td", "--depth", "2", "1", "--xg", "out/defaultply2_defaultply1.txt", "3");
    }

    // 訓練済みファイルで対局
    @Test
    @Ignore
    public void run_trained() {
        String file = "td:out/trial20K_200614/dump3.txt";
        Main.parseAndRun("run", "--alg", file, "random", "--depth", "1", "1", "10");
    }

    // 訓練済みファイルで対局、結果はログファイルへ
    @Test
    @Ignore
    public void run_trained_logsToFile() {
        String file = "td:out/trial20K_200614/dump3.txt";
        Main.parseAndRun("run", "--alg", file, "random", "--depth", "1", "1", "--log", "out/run_trained.log", "10");
    }

    // 2Ply
    @Test
    @Ignore
    public void run_trained_2ply() {
        String file = "td:out/trial20K_200614/dump3.txt";
        Main.parseAndRun("run", "--alg", file, file, "--depth", "2", "1", "30");
    }

}
