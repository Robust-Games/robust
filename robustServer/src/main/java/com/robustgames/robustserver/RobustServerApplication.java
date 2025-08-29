/**
 * @author Burak Altun, Carolin Scheffler, Ersin Yesiltas
 */
package com.robustgames.robustserver;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.net.NetService;
import com.almasb.fxgl.net.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Main entry point for the Robust server application.
 * <p>
 * Initializes the FXGL server and accepts incoming TCP connections.
 * Each connection is passed to the {@link GameSession}, which assigns player roles
 * and manages message handling between connected clients.
 */
public class RobustServerApplication {

    private final Map<Connection<Bundle>, Integer> clientIds = new HashMap<>();
    private final Map<Integer, Connection<Bundle>> idToConnection = new HashMap<>();
    private int nextId = 1;
    private Server<Bundle> server;

    private boolean player1Ready = false;
    private boolean player2Ready = false;

    private GameSession session;

    /**
     * Starts the FXGL TCP server and listens for incoming client connections.
     * Accepted clients are passed to the game session for management.
     */
    public void startServer() {
        NetService netService = new NetService();
        server = netService.newTCPServer(55555);

        session = new GameSession();

        server.setOnConnected(conn -> {
            System.out.println("[Server] New client connected.");
            session.tryAddClient(conn);

            conn.addMessageHandler((connection, bundle) -> {
                String type = bundle.getName();
                System.out.println("[Server] Received bundle: " + type + " from " + connection);

                switch (type) {
                    case "hello": {
                        if (!clientIds.containsKey(connection)) {
                            clientIds.put(connection, nextId);
                            idToConnection.put(nextId, connection);
                            System.out.println("[Server] Assigned ID " + nextId + " to a client");
                            nextId++;
                        }

                        if (clientIds.size() == 2) {
                            // send assigned IDs to both clients
                            clientIds.forEach((clientConn, id) -> {
                                Bundle assign = new Bundle("assign_id");
                                assign.put("clientId", id);
                                clientConn.send(assign);
                                System.out.println("[Server] Sent assign_id " + id + " to client");
                            });
                        }
                        break;
                    }

                    case "PlayerReady": {
                        int clientId = bundle.get("clientId");
                        System.out.println("[Server] Received PlayerReady from client " + clientId);

                        if (clientId == 1) player1Ready = true;
                        if (clientId == 2) player2Ready = true;

                        if (player1Ready && player2Ready) {
                            System.out.println("[Server] Both players ready, sending ExecuteTurn");
                            Bundle executeTurn = new Bundle("ExecuteTurn");
                            idToConnection.values().forEach(connTarget -> connTarget.send(executeTurn));
                            player1Ready = false;
                            player2Ready = false;
                        }
                        break;
                    }
                    case "PlayerAlive": {break;}

                    default: {
                        int senderId = bundle.get("clientId");
                        Connection<Bundle> targetConn = getOtherClientConnection(senderId);

                        if (targetConn != null) {
                            targetConn.send(bundle);
                            System.out.println("[Server] Forwarded bundle '" + type + "' from client " + senderId + " to opponent.");
                        } else {
                            System.out.println("[Server] Opponent not connected. Cannot forward bundle from client " + senderId);
                        }
                        break;
                    }
                }
            });

            server.setOnDisconnected(disconn -> {
                System.out.println("[Server] Client disconnected.");
                if (disconn.equals(session.getPlayer1())) {
                    System.out.println("[Server - Session] PLAYER1 disconnected.");
                    session.clearPlayer1();
                } else if (disconn.equals(session.getPlayer2())) {
                    System.out.println("[Server - Session] PLAYER2 disconnected.");
                    session.clearPlayer2();
                }
            });
        });

        server.startAsync();
        System.out.println("[Server] Robust server started on port 55555.");
    }
    /**
     * Delivers the connection of the other client with the ID @param senderId
     * */
    private Connection<Bundle> getOtherClientConnection(int senderId) {
        if (senderId == 1 && idToConnection.containsKey(2)) return idToConnection.get(2);
        if (senderId == 2 && idToConnection.containsKey(1)) return idToConnection.get(1);
        return null;
    }
    private void runConsole() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                switch (line.trim().toLowerCase()) {
                    case "exit":
                    case "quit":
                        System.out.println("[Server] Shutting down...");
                        if (server != null) {
                            clientIds.forEach((conn, id) ->{
                                session.handleServerDisconnect(conn);
                                    });
                            server.stop();
                        }
                        return;

                    case "status":
                        System.out.println("[Server] Connected clients: " + clientIds.size());
                        clientIds.forEach((conn, id) ->
                                System.out.println(" - Client ID " + id + " @ " + conn));
                        break;

                    case "help":
                        System.out.println("Available commands:");
                        System.out.println("  status   - show connected clients");
                        System.out.println("  exit     - stop the server");
                        System.out.println("  help     - show this help");
                        break;

                    default:
                        System.out.println("[Console] Unknown command. Type 'help'.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        RobustServerApplication server = new RobustServerApplication();
        server.startServer();
        server.runConsole();
    }
}
