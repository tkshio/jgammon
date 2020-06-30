package com.github.tkshio.jgammon.gammon.utils;

import com.github.tkshio.jgammon.gammon.BackgammonResult;

import java.text.NumberFormat;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * バックギャモンのゲーム結果の集計を取る
 * <p>
 * 単純にシングル・ギャモン・バックギャモンに分けて数えるだけなので、
 * プレイヤーごとにオブジェクトを用意する必要がある
 */
public class BackgammonScoreStat {
    private final AtomicInteger single = new AtomicInteger(0);
    private final AtomicInteger gammon = new AtomicInteger(0);
    private final AtomicInteger bGammon = new AtomicInteger(0);
    private final BackgammonResult.Acceptor acceptor =
            new BackgammonResult.Acceptor() {
                @Override
                public void single() {
                    single.incrementAndGet();
                }

                @Override
                public void gammon() {
                    gammon.incrementAndGet();
                }

                @Override
                public void backgammon() {
                    bGammon.incrementAndGet();
                }

            };

    /**
     * ゲーム結果を追加する
     *
     * @param result ゲーム結果
     */
    public void add(BackgammonResult result) {
        result.accept(acceptor);
    }

    /**
     * シングル＝１、ギャモン＝２、バックギャモン＝３で得点換算する
     *
     * @return 得点
     */
    public int getPoint() {
        // Cube ....
        return single.get() + gammon.get() * 2 + bGammon.get() * 3;
    }

    /**
     * 集計結果をテキスト出力する
     *
     * @param total 総ゲーム数
     * @return 集計結果を示すテキスト
     */
    public String summarize(long total) {
        if (total == 0) {
            return "";
        }
        var nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(1);
        int s = single.get();
        int g = gammon.get();
        int b = bGammon.get();

        return String.format("single:%d g:%d bg:%d (s:%6s b:%6s bg:%6s , win:%6s = s:%6s + g,bg:%6s)",
                s, g, b,
                nf.format((double) s / total),
                nf.format((double) g / total),
                nf.format((double) b / total),
                nf.format((double) (s + g + b) / total),
                nf.format((double) (s) / total),
                nf.format((double) (g + b) / total)
        );
    }
}
