package com.github.tkshio.jgammon.common.director;

import com.github.tkshio.jgammon.common.node.Node;
import lombok.Data;


/**
 * {@link Director}を操作して対局を実行するクラス
 *
 * @param <STATE> 局面を表すクラス
 * @param <NODE>  ノードを表すクラス
 */
public interface GameRunner<STATE, NODE extends Node<STATE>> {

    /**
     * ファクトリーメソッド
     *
     * @param director 進行役
     * @param <STATE>  局面を表すクラス
     * @param <T>      ノードを表すクラス
     * @return 構築されたオブジェクト
     */
    static <STATE, T extends Node<STATE>>
    GameRunner<STATE, T> create(Director<STATE, T> director
    ) {
        // ディレクターから先手・後手を担当するオブジェクトを受け取る
        var tebanPlayers = director.createTebanPlayers();
        Director.TebanPlayer<STATE, T> sente = tebanPlayers.sente();
        Director.TebanPlayer<STATE, T> gote = tebanPlayers.gote();

        // インターフェースに定義された各メソッドに、先手・後手を割り当てる
        return new GameRunner<>() {

            @Override
            public T startGame() {
                return director.startGame();
            }

            @Override
            public GameState<STATE, T> doOpening(T openingNode) {
                return sente.playOpening(openingNode);
            }

            @Override
            public GameState<STATE, T> doSenteBan(T node) {
                return sente.play(node);
            }

            @Override
            public GameState<STATE, T> doGoteBan(T node) {
                return gote.play(node);
            }

            @Override
            public void abortGame() {
                director.abortGame();
            }
        };
    }

    /**
     * 1対局を実行する
     *
     * <p>ターン数がmaxTurnに達すると、中断となる。
     *
     * @param runner  対局を実行するオブジェクト
     * @param maxTurn 最大ターン（2plyで1ターン）
     * @param <STATE> 局面を表すクラス
     * @param <T>     ノードを表すクラス
     * @return ゲームの最終状態
     */
    static <STATE, T extends Node<STATE>>
    GameFinalState<STATE> run(GameRunner<STATE, T> runner, int maxTurn) {
        int turn = 1;
        T openingNode = runner.startGame();
        GameState<STATE, T> senteBanEnd = runner.doOpening(openingNode);
        do {
            if (senteBanEnd.isEOG()) {
                return new GameFinalState<>(senteBanEnd.getNode().getState(),
                        GameResult.SENTE_WIN);
            }

            GameState<STATE, T> goteBanEnd;
            if ((goteBanEnd = runner.doGoteBan(senteBanEnd.getNode())).isEOG()) {
                return new GameFinalState<>(goteBanEnd.getNode().getState(),
                        GameResult.GOTE_WIN);
            }
            if (turn >= maxTurn) {
                break;
            }
            turn++;
            senteBanEnd = runner.doSenteBan(goteBanEnd.getNode());
        } while (true);

        runner.abortGame();
        return new GameFinalState<>(senteBanEnd.node.getState(), GameResult.ABORT);
    }

    /**
     * 対局を開始する
     *
     * @return 初期状態のノード
     */
    NODE startGame();

    /**
     * 初手をプレイする
     *
     * @param openingNode 初期状態のノード
     * @return 初手プレイ後のゲーム進行状態（後手番）
     */
    GameState<STATE, NODE> doOpening(NODE openingNode);

    /**
     * 先手番をプレイする
     *
     * @param node 後手番終了後のノード
     * @return ゲーム進行状態（先手番終了後）
     */
    GameState<STATE, NODE> doSenteBan(NODE node);

    /**
     * 後手番をプレイする
     *
     * @param node 先手番終了後のノード
     * @return ゲーム進行状態（後手番終了後）
     */
    GameState<STATE, NODE> doGoteBan(NODE node);

    /**
     * ゲームを中断する
     */
    void abortGame();

    /**
     * ゲームの最終状態を表すクラス
     *
     * @param <STATE> 局面を表すクラス
     */
    @Data
    class GameFinalState<STATE> {
        private final STATE state;
        private final GameResult result;
    }

    /**
     * ゲームの進行状態を表すクラス
     *
     * @param <STATE> 局面を表すクラス
     * @param <T>     ノードを表すクラス
     */
    @Data
    class GameState<STATE, T extends Node<STATE>> {
        private final T node;
        private final boolean isEOG;
    }

}
