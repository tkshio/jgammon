package com.github.tkshio.jgammon.gammon.director;

import com.github.tkshio.jgammon.common.context.GameContextHandler;
import com.github.tkshio.jgammon.common.context.GameContextHandler.TebanContextHandler;
import com.github.tkshio.jgammon.common.director.*;
import com.github.tkshio.jgammon.common.utils.RWTuple;
import com.github.tkshio.jgammon.common.utils.SGTuple;
import com.github.tkshio.jgammon.gammon.*;
import com.github.tkshio.jgammon.gammon.utils.BackgammonScoreStat;
import lombok.Builder;

import java.util.IntSummaryStatistics;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * 自己対局機能を提供する
 */
@Builder
public class BackgammonAutoPlay {
    @Builder.Default
    private final BackgammonDirectorConf dConf = BackgammonDirectorConf.onePlyDirectorConf();

    @Builder.Default
    private final Player<BackgammonState> white = RandomPlayer.create("WHITE");

    @Builder.Default
    private final Player<BackgammonState> red = RandomPlayer.create("(RED)");

    @Builder.Default
    private final GameContextHandler<BackgammonState> contextHandler = new GameContextHandler<>() {
    };

    @Builder.Default
    private final Supplier<BackgammonBoard> initialBoard = () -> BackgammonBoard.create(BGType.standard);
    @Builder.Default
    private final BackgammonDice bgDice = BackgammonDice.create();

    @Builder.Default
    private final int maxTurn = 200;

    /**
     * N回対局を繰り返す
     *
     * @param n 対局回数
     */
    public void run(int n) {
        run(0, n);
    }

    /**
     * fromで指定された開始番号番号からn回対局を繰り返す
     *
     * @param fromInclusive 開始番号
     * @param n             対局回数
     */
    public void run(int fromInclusive, int n) {
        // 対局番号を用意、記録
        IntStream nums = IntStream.range(fromInclusive, fromInclusive + n);
        contextHandler.startTrials(fromInclusive, n);


        // 手番の長さの統計を取る
        IntSummaryStatistics plysStat = new IntSummaryStatistics();

        // プレイヤーおよびそれぞれの結果集計の準備
        RWTuple<Player<BackgammonState>> players = RWTuple.of(red, white);
        RWTuple<BackgammonScoreStat> scoreStats = RWTuple.from(BackgammonScoreStat::new);
        RWTuple<BiFunction<GameInfo, BackgammonState, GameInfo>> statCouners =
                RWTuple.of(scoreStats, stat -> (info, state) -> {
                    plysStat.accept(state.getPly());
                    stat.add(state.getResult());

                    // 結果出力用に、スコアを更新したGameInfoを生成する
                    return new GameInfo(
                            info.getGameNo(),
                            RWTuple.of(scoreStats, BackgammonScoreStat::getPoint),
                            RWTuple.of(players, Player::getName)
                    );
                });

        // 対局の実施
        nums.sequential().forEachOrdered(i -> {
            var gameInfo = new GameInfo(
                    i,
                    RWTuple.of(scoreStats, BackgammonScoreStat::getPoint),
                    RWTuple.of(players, Player::getName));

            runGame(gameInfo,
                    dConf,
                    players,
                    statCouners
            );
        });

        // 結果の出力
        long trials = plysStat.getCount();
        contextHandler.endTrials(trials,
                red.getName(), scoreStats.red().getPoint(),
                white.getName(), scoreStats.white().getPoint(),
                String.format("%15s %s", red.getName(), scoreStats.red().summarize(trials)),
                String.format("%15s %s", white.getName(), scoreStats.white().summarize(trials)),
                "",
                "Plys statistics:" + plysStat.toString()
        );
    }

    /**
     * 1ゲームの実行
     *
     * @param info           対局番号、スコアなどの記録
     * @param dConf          ルール、先読みの深さなどの設定情報
     * @param players        対局を行うプレイヤーの対
     * @param resultHandlers 結果格納のためのコールバック先
     */
    private void runGame(GameInfo info,
                         BackgammonDirectorConf dConf,
                         RWTuple<Player<BackgammonState>> players,
                         RWTuple<BiFunction<GameInfo, BackgammonState, GameInfo>> resultHandlers) {

        // StateOperatorの生成
        var stateOperator = BackgammonStateOperator.create(bgDice);

        // OpeningRollを振って、先手・後手を決定する
        SGTuple<Player<BackgammonState>> sgPlayers;
        SGTuple<TebanContextHandler<BackgammonState>> sgLoggers;
        SGTuple<BiFunction<GameInfo, BackgammonState, GameInfo>> sgResultHandlers;

        BackgammonState initialState;

        BackgammonDice.OpeningRoll openingRoll =
                stateOperator.generateOpeningRoll();
        if (openingRoll.firstDiceIsHigher()) {
            initialState = stateOperator.redGoesFirst(initialBoard.get());
            sgPlayers = players.redGoesFirst();
            sgLoggers = contextHandler.redGoesFirst();
            sgResultHandlers = resultHandlers.redGoesFirst();
        } else {
            initialState = stateOperator.whiteGoesFirst(initialBoard.get());
            sgPlayers = players.whiteGoesFirst();
            sgLoggers = contextHandler.whiteGoesFirst();
            sgResultHandlers = resultHandlers.whiteGoesFirst();
        }

        // Directorに先手・後手の各プレイヤーと、オープニングロールを通知
        var director = dConf.configuredBuilder(
                stateOperator, openingRoll.asRoll(), initialState)
                .nodesEvaluators(SGTuple.of(
                        sgPlayers.sente().createEvaluators().sente(),
                        sgPlayers.gote().createEvaluators().gote())
                )
                .contextHandlers(sgLoggers)
                .build();

        var runner = GameRunner.create(director);

        // 対局開始
        contextHandler.startGame(info, initialState);
        var finalState = GameRunner.run(runner, maxTurn);

        // 結果の通知
        var state = finalState.getState();
        finalState.getResult().accept(new GameResult.Acceptor() {
            @Override
            public void senteWon() {
                var after = sgResultHandlers.sente().apply(info, state);

                var winner = sgPlayers.sente();
                var loser = sgPlayers.gote();
                sgLoggers.sente().won(after, winner, loser, state);
            }

            @Override
            public void goteWon() {
                var after = sgResultHandlers.gote().apply(info, state);

                var winner = sgPlayers.gote();
                var loser = sgPlayers.sente();
                sgLoggers.gote().won(after, winner, loser, state);
            }

            @Override
            public void abort() {
                contextHandler.abortGame(state);
            }
        });

    }

    /**
     * ビルダークラス
     */
    public static class BackgammonAutoPlayBuilder {
    }
}
