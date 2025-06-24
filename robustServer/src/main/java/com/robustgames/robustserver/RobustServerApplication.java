package com.robustgames.robustserver;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Server;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * The RobustServerApplication listens for incoming Bundles from clients
 * and responds with an acknowledgment message.
 */
public class RobustServerApplication extends GameApplication {

    /**
     * Configures basic application settings such as window size, version, and title.
     *
     * @param settings The GameSettings object to be configured.
     */
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Robust Server");
        settings.setVersion("0.1");
        settings.setWidth(640);
        settings.setHeight(480);
    }

    /**
     * Initializes the FXGL TCP server, listens for client connections,
     * and handles incoming Bundles by sending an acknowledgment response.
     */
    @Override
    protected void initGame() {
        Server<Bundle> server = getNetService().newTCPServer(55555);

        server.setOnConnected(connection -> {
            connection.addMessageHandlerFX((conn, bundle) -> {
                System.out.println("Received from client: " + bundle);

                // Create and send a response bundle
                Bundle response = new Bundle("ServerResponse");
                response.put("status", "ACK");
                response.put("message", "Hello Client, your bundle (" + bundle.getName() + ") was received!");

                conn.send(response);
            });
        });

        System.out.println("FXGL NetServer listening on port 55555!");
        server.startAsync();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
