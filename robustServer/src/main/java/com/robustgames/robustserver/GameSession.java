/**
 * @author Ersin Yesiltas, Nico Steiner
 */
package com.robustgames.robustserver;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Manages a single game session with up to two connected clients.
 * <p>
 * Assigns player roles (PLAYER1 and PLAYER2), handles incoming bundles
 * from both clients, and coordinates communication and synchronization between them.
 */
public class GameSession {

    private PlayerConnectionHandler player1;
    private PlayerConnectionHandler player2;
    private Connection<Bundle> connection1;
    private Connection<Bundle> connection2;
    private long player1LastHeartbeat;
    private long player2LastHeartbeat;
    private final ScheduledExecutorService heartbeatChecker = Executors.newSingleThreadScheduledExecutor();
    private final java.util.List<PlayerConnectionHandler> connectedClients = new java.util.ArrayList<>();
    private final ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
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
            connection1 = conn;
            player1 = new PlayerConnectionHandler(conn, "PLAYER1", this);
            connectedClients.add(player1);
            System.out.println("[Server - Session] PLAYER1 connected.");
        } else if (player2 == null) {
            connection2 = conn;
            player2 = new PlayerConnectionHandler(conn, "PLAYER2", this);
            connectedClients.add(player2);
            System.out.println("[Server - Session] PLAYER2 connected.");
            startGame();

        } else {
            System.err.println("[Server - Session] Rejected: More than 2 players not supported.");
            Bundle reject = new Bundle("Reject");
            reject.put("message", "Session full.");
            conn.send(reject);
            System.out.println("[Server - Session] Reject sent.");
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

            sendHeartbeats();
            startHeartbeatChecker();

            System.out.println("[Server - Session] Game started. Players assigned.");
        }
    }

    /**
     * Handles an incoming bundle from a connected player.
     * <p>
     * Currently sends back a simple ACK and PlayerAlive to check connection.
     * Future versions should process and synchronize actions between both players.
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
        if ("PlayerAlive".equals(bundle.getName())) {
            if ("PLAYER1".equals(senderId)) {
                player1LastHeartbeat = System.currentTimeMillis();
            } else {
                player2LastHeartbeat = System.currentTimeMillis();
            }
        }
        // TODO: In future, handle synchronization and action processing here.
    }

    private void sendHeartbeats() {
        heartbeatExecutor.scheduleAtFixedRate(() -> {
            for (PlayerConnectionHandler player : connectedClients) {
                Bundle heartbeat = new Bundle("heartbeat");
                player.send(heartbeat);
            }
        }, 0, 5, TimeUnit.SECONDS); // Send heartbeat every 5 seconds
    }
    private void startHeartbeatChecker() {
        heartbeatChecker.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            if (player1 != null && currentTime - player1LastHeartbeat > 15000) {
                clearPlayer1();
                System.out.println("[Server] Player 1 disconnected");
            }
            if (player2 != null && currentTime - player2LastHeartbeat > 15000) {
                clearPlayer2();
                System.out.println("[Server] Player 2 disconnected");
            }
        }, 15, 5, TimeUnit.SECONDS); // Check every 5 seconds
    }

    /**
     * Returns the connection assigned to PLAYER1.
     *
     * @return the Connection object for PLAYER1, or null if not assigned
     */
    public Connection<Bundle> getPlayer1() {
        return connection1;
    }

    /**
     * Returns the connection assigned to PLAYER2.
     *
     * @return the Connection object for PLAYER2, or null if not assigned
     */
    public Connection<Bundle> getPlayer2() {
        return connection2;
    }

    /**
     * Removes player1 from the session.
     */
    public void clearPlayer1() {
        connectedClients.remove(connection1);
        connection1.terminate();
        connection1 = null;
        player1 = null;
        gameStarted = false;
        if (player2 != null) {
            player2.send(new Bundle("OpponentLeft"));
        }
        heartbeatChecker.shutdown();
    }

    /**
     * Removes player2 from the session.
     */
    public void clearPlayer2() {
        connectedClients.remove(connection2);
        connection2.terminate();
        connection2 = null;
        player2 = null;
        gameStarted = false;
        if (player1 != null) {
            player1.send(new Bundle("OpponentLeft"));
        }
        heartbeatChecker.shutdown();
    }
    public void handleServerDisconnect(Connection<Bundle> disconn) {
        if (disconn == connection1) clearPlayer1();
        if (disconn == connection2) clearPlayer2();
    }
}