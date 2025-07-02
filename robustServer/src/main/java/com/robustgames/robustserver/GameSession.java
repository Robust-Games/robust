package com.robustgames.robustserver;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;

/**
 * Manages a single game session with up to two connected clients.
 * <p>
 * Assigns player roles (PLAYER1 and PLAYER2), handles incoming bundles
 * from both clients, and coordinates communication and synchronization between them.
 */
public class GameSession {

    private PlayerConnectionHandler player1;
    private PlayerConnectionHandler player2;
    private Connection<Bundle> playerOne;
    private Connection<Bundle> playerTwo;

    private boolean gameStarted = false;

    /**
     * Attempts to add a new client connection to the game session.
     * <p>
     * Only two players are supported. Additional clients will be rejected.
     *
     * @param conn The incoming client connection.
     */
    public void tryAddClient(Connection<Bundle> conn) {
        if (player1 == null) {
            player1 = new PlayerConnectionHandler(conn, "PLAYER1", this);
            System.out.println("[Session] PLAYER1 connected.");
        } else if (player2 == null) {
            player2 = new PlayerConnectionHandler(conn, "PLAYER2", this);
            System.out.println("[Session] PLAYER2 connected.");
            startGame();
        } else {
            System.err.println("[Session] Rejected: More than 2 players not supported.");
            Bundle reject = new Bundle("Reject");
            reject.put("message", "Session full.");
            conn.send(reject);
            System.out.println("[Session] Reject sent.");
        }
    }

    /**
     * Starts the game session by notifying both clients of their assigned roles.
     */
    private void startGame() {
        if (player1 != null && player2 != null && !gameStarted) {
            gameStarted = true;

            Bundle start1 = new Bundle("GameStart");
            start1.put("assignedPlayer", "PLAYER1");

            Bundle start2 = new Bundle("GameStart");
            start2.put("assignedPlayer", "PLAYER2");

            player1.send(start1);
            player2.send(start2);

            System.out.println("[Session] Game started. Players assigned.");
        }
    }

    /**
     * Handles an incoming bundle from a connected player.
     * <p>
     * Currently sends back a simple ACK. Future versions should process and
     * synchronize actions between both players.
     *
     * @param senderId The string identifier of the sending player.
     * @param bundle   The received message bundle.
     */
    public void handleBundle(String senderId, Bundle bundle) {
        System.out.println("[" + senderId + "] Sent bundle: " + bundle.getName());

        Bundle ack = new Bundle("ServerACK");
        ack.put("status", "RECEIVED");
        ack.put("originalBundle", bundle.getName());
        ack.put("from", senderId);

        if (senderId.equals("PLAYER1")) {
            player1.send(ack);
        } else if (senderId.equals("PLAYER2")) {
            player2.send(ack);
        }
        // TODO: In future, handle synchronization and action processing here.
    }

    /**
     * Returns the connection assigned to PLAYER1.
     *
     * @return the Connection object for PLAYER1, or null if not assigned
     */
    public Connection<Bundle> getPlayer1() {
        return playerOne;
    }

    /**
     * Returns the connection assigned to PLAYER2.
     *
     * @return the Connection object for PLAYER2, or null if not assigned
     */
    public Connection<Bundle> getPlayer2() {
        return playerTwo;
    }

    /**
     * Removes player1 from the session.
     */
    public void clearPlayer1() {
        playerOne = null;
        gameStarted = false;
    }

    /**
     * Removes player2 from the session.
     */
    public void clearPlayer2() {
        playerTwo = null;
        gameStarted = false;
    }
}