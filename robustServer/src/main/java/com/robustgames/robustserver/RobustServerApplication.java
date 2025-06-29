package com.robustgames.robustserver;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.net.Server;

import java.util.HashMap;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

public class RobustServerApplication extends GameApplication {

    private final Map<Connection<Bundle>, Integer> clientIds = new HashMap<>();
    private int nextId = 1;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Robust Server");
        settings.setVersion("0.1");
        settings.setWidth(640);
        settings.setHeight(480);
    }

    @Override
    protected void initGame() {
        Server<Bundle> server = getNetService().newTCPServer(55555);

        server.setOnConnected(connection -> {

            connection.addMessageHandlerFX((conn, bundle) -> {
                System.out.println("Received from client: " + bundle);
                String type = bundle.getName();

                switch (type) {
                    case "hello": {
                        // Client hat sich mit "hello" gemeldet -> ID zuweisen
                        if (!clientIds.containsKey(conn)) {
                            clientIds.put(conn, nextId++);
                            System.out.println("Assigned tentative ID " + clientIds.get(conn) + " to a client");
                        }

                        // Prüfen, ob 2 Clients verbunden sind
                        if (clientIds.size() == 2) {
                            // IDs an beide Clients senden
                            clientIds.forEach((clientConn, id) -> {
                                Bundle assign = new Bundle("assign_id");
                                assign.put("id", id);
                                clientConn.send(assign);
                                System.out.println("Sent assign_id " + id + " to client");
                            });
                        }
                        break;
                    }

                    case "ServerResponse": {
                        int clientId = clientIds.getOrDefault(conn, -1);
                        System.out.println("Nachricht von Client-ID: " + clientId);
                        Bundle response = new Bundle("ServerResponse");
                        response.put("status", "ACK");
                        response.put("message", "Hello Client, your bundle (" + bundle.getName() + ") was received!");
                        conn.send(response);
                        break;
                    }

                    default:
                        System.out.println("Unknown message type: " + type);
                        break;
                }
            });
        });

        System.out.println("FXGL NetServer listening on port 55555!");
        server.startAsync();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
