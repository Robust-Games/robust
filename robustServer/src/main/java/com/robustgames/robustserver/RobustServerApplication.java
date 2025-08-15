/**
 * @author Burak Altun, Carolin Scheffler, Ersin Yesiltas
 */
package com.robustgames.robustserver;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.net.Server;

import java.util.HashMap;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Main entry point for the Robust server application.
 * <p>
 * Initializes the FXGL server and accepts incoming TCP connections.
 * Each connection is passed to the {@link GameSession}, which assigns player roles
 * and manages message handling between connected clients.
 */
public class RobustServerApplication extends GameApplication {

    private final Map<Connection<Bundle>, Integer> clientIds = new HashMap<>();
    private final Map<Integer, Connection<Bundle>> idToConnection = new HashMap<>();
    private int nextId = 1;

    private boolean player1Ready = false;
    private boolean player2Ready = false;

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
            conn.addMessageHandlerFX((connection, bundle) -> {
                String type = bundle.getName();
                System.out.println("Received bundle: " + type + " from " + connection);

                switch (type) {
                    case "hello": {
                        // Client hat sich mit "hello" gemeldet -> ID zuweisen

                        if (!clientIds.containsKey(connection)) {
                            clientIds.put(connection, nextId);
                            idToConnection.put(nextId, connection); // <-- fehlt, sonst kein Routing
                            System.out.println("Assigned ID " + nextId + " to a client");
                            nextId++;
                        }

                        // Prüfen, ob 2 Clients verbunden sind
                        if (clientIds.size() == 2) {
                            // IDs an beide Clients senden
                            clientIds.forEach((clientConn, id) -> {
                                Bundle assign = new Bundle("assign_id");
                                assign.put("clientId", id);
                                clientConn.send(assign);
                                System.out.println("Sent assign_id " + id + " to client");
                            });
                        }
                        break;
                    }

                    case "PlayerReady": {
                        int clientId = bundle.get("clientId");
                        System.out.println("Received PlayerReady from client " + clientId);

                        if (clientId == 1) player1Ready = true;
                        if (clientId == 2) player2Ready = true;

                        if (player1Ready && player2Ready) {
                            System.out.println("Both players ready, sending ExecuteTurn");

                            Bundle executeTurn = new Bundle("ExecuteTurn");

                            idToConnection.values().forEach(connTarget -> {
                                connTarget.send(executeTurn);
                                System.out.println("Sent ExecuteTurn to client");
                            });

                            // Reset
                            player1Ready = false;
                            player2Ready = false;
                        }
                        break;
                    }

                    default: {
                        // Sender-ID aus Bundle lesen
                        int senderId = bundle.get("clientId");
                        Connection<Bundle> targetConn = getOtherClientConnection(senderId);

                        if (targetConn != null) {
                            targetConn.send(bundle);
                            System.out.println("Forwarded bundle '" + type + "' from client " + senderId + " to opponent.");
                        } else {
                            System.out.println("Opponent not connected. Cannot forward bundle from client " + senderId);
                        }
                        break;
                    }
                }


            });
            server.setOnDisconnected(conne -> {
                System.out.println("[Server] Client disconnected.");
                // Spieler-Zuordnung aufheben
                if (conne.equals(session.getPlayer1())) {
                    System.out.println("[Session] PLAYER1 disconnected.");
                    session.clearPlayer1();
                } else if (conne.equals(session.getPlayer2())) {
                    System.out.println("[Session] PLAYER2 disconnected.");
                    session.clearPlayer2();
                }
            });

        });
        server.startAsync();
    }

    /**
     * Liefert die Verbindung des jeweils anderen Clients.
     */
    private Connection<Bundle> getOtherClientConnection(int senderId) {
        if (senderId == 1 && idToConnection.containsKey(2)) {
            return idToConnection.get(2);
        } else if (senderId == 2 && idToConnection.containsKey(1)) {
            return idToConnection.get(1);
        }
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
