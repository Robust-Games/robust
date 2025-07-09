package com.robustgames.robustserver;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.net.Server;

import java.util.*;

import static com.almasb.fxgl.dsl.FXGL.*;

public class RobustServerApplication extends GameApplication {

    private final Map<Connection<Bundle>, Integer> clientIds = new HashMap<>();
    private final Map<Integer, Connection<Bundle>> idToConnection = new HashMap<>();
    private final Map<Integer, String> clientIdToPlayerName = new HashMap<>();
    private int nextId = 1;

    private final List<Bundle> actionsPlayer1 = new ArrayList<>();
    private final List<Bundle> actionsPlayer2 = new ArrayList<>();
    private boolean player1Ready = false;
    private boolean player2Ready = false;
    private String nextTurnPlayer = "PLAYER1";

    private GameSession session;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Robust Server");
        settings.setVersion("0.3");
        settings.setWidth(640);
        settings.setHeight(480);
    }

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
                    case "hello" -> {
                        if (!clientIds.containsKey(connection)) {
                            int assignedId = nextId++;
                            clientIds.put(connection, assignedId);
                            idToConnection.put(assignedId, connection);

                            String assignedPlayer = (assignedId == 1) ? "PLAYER1" : "PLAYER2";
                            clientIdToPlayerName.put(assignedId, assignedPlayer);

                            System.out.println("Assigned ID " + assignedId + " (" + assignedPlayer + ") to client");
                        }

                        if (clientIds.size() == 2) {
                            clientIds.forEach((clientConn, id) -> {
                                Bundle assign = new Bundle("assign_id");
                                assign.put("clientId", id);
                                clientConn.send(assign);
                                System.out.println("Sent assign_id " + id + " to client");

                                Bundle start = new Bundle("GameStart");
                                start.put("assignedPlayer", clientIdToPlayerName.get(id));
                                clientConn.send(start);
                            });
                        }
                    }

                    case "MoveAction", "RotateAction", "ShootAction" -> {
                        Integer clientId = bundle.get("clientId");
                        if (clientId == null) {
                            System.out.println("[WARN] Received action bundle without clientId → ignored.");
                            return;
                        }

                        String player = clientIdToPlayerName.get(clientId);
                        if (player == null) {
                            System.out.println("[WARN] Unknown clientId: " + clientId + " → action ignored.");
                            return;
                        }

                        System.out.println("Storing action for " + player + ": " + type);
                        if (player.equals("PLAYER1")) {
                            actionsPlayer1.add(bundle);
                        } else if (player.equals("PLAYER2")) {
                            actionsPlayer2.add(bundle);
                        }
                    }

                    case "EndTurn" -> {
                        Integer senderId = bundle.get("clientId");
                        String playerName = clientIdToPlayerName.get(senderId);

                        if (playerName == null) {
                            System.out.println("Unknown clientId in EndTurn: " + senderId);
                            return;
                        }

                        if (playerName.equals("PLAYER1")) {
                            player1Ready = true;
                        } else if (playerName.equals("PLAYER2")) {
                            player2Ready = true;
                        }

                        System.out.println(playerName + " is ready.");

                        if (player1Ready && player2Ready) {
                            System.out.println("Both players ready → broadcasting actions");
                            broadcastActions();
                            player1Ready = false;
                            player2Ready = false;
                            actionsPlayer1.clear();
                            actionsPlayer2.clear();
                        }
                    }

                    default -> {
                        System.out.println("Unknown bundle type: " + type + " (ignored)");
                    }
                }
            });
        });

        server.setOnDisconnected(conne -> {
            System.out.println("[Server] Client disconnected.");
            if (conne.equals(session.getPlayer1())) {
                System.out.println("[Session] PLAYER1 disconnected.");
                session.clearPlayer1();
            } else if (conne.equals(session.getPlayer2())) {
                System.out.println("[Session] PLAYER2 disconnected.");
                session.clearPlayer2();
            }
        });

        server.startAsync();
    }

    private void broadcastActions() {
        List<Bundle> all = new ArrayList<>();
        all.addAll(actionsPlayer1);
        all.addAll(actionsPlayer2);

        for (Connection<Bundle> client : clientIds.keySet()) {
            for (Bundle action : all) {
                client.send(action);
            }

            Bundle nextTurn = new Bundle("NextTurn");
            nextTurn.put("nextPlayer", nextTurnPlayer);
            client.send(nextTurn);
        }

        nextTurnPlayer = nextTurnPlayer.equals("PLAYER1") ? "PLAYER2" : "PLAYER1";
    }

    public static void main(String[] args) {
        launch(args);
    }
}
