package com.robustgames.robustserver;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Server;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Main entry point for the Robust server application.
 * <p>
 * Initializes the FXGL server and accepts incoming TCP connections.
 * Each connection is passed to the {@link GameSession}, which assigns player roles
 * and manages message handling between connected clients.
 */
public class RobustServerApplication extends GameApplication {

    private GameSession session;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Robust Server");
        settings.setVersion("0.2");
        settings.setWidth(640);
        settings.setHeight(480);
    }

    /**
     * Starts the FXGL TCP server and listens for incoming client connections.
     * Accepted clients are passed to the game session for management.
     */
    @Override
    protected void initGame() {
        Server<Bundle> server = getNetService().newTCPServer(55555);

        session = new GameSession();

        server.setOnConnected(conn -> {
            System.out.println("[Server] New client connected.");
            session.tryAddClient(conn);
        });

        server.setOnDisconnected(conn -> {
            System.out.println("[Server] Client disconnected.");

            // Spieler-Zuordnung aufheben
            if (conn.equals(session.getPlayer1())) {
                System.out.println("[Session] PLAYER1 disconnected.");
                session.clearPlayer1();
            } else if (conn.equals(session.getPlayer2())) {
                System.out.println("[Session] PLAYER2 disconnected.");
                session.clearPlayer2();
            }
        });

        System.out.println("[Server] Listening on port 55555.");
        server.startAsync();
    }

    public static void main(String[] args) {
        launch(args);
    }
}