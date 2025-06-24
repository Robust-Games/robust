package com.robustgames.robustserver;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Server;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * The RobustServerApplication is the main entry point for the Robust server.
 * It starts FXGL in headless mode (no window, no JavaFX GUI) and initializes the FXGL TCP server.
 * This server is intended to run on systems without a graphical environment (e.g., Linux server, Docker).
 */
public class RobustServerApplication extends GameApplication {

    /**
     * Configures basic application settings for the server.
     * Headless mode is enabled to avoid opening any GUI window.
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
     * Initializes the FXGL TCP server and sets up handlers for incoming client connections and bundles.
     * The server will listen on port 55555 for incoming connections and print any received bundles to the console.
     */
    @Override
    protected void initGame() {
        Server<Bundle> server = getNetService().newTCPServer(55555);

        server.setOnConnected(connection -> {
            connection.addMessageHandlerFX((conn, bundle) -> {
                System.out.println("Received from client: " + bundle);
            });
        });

        System.out.println("FXGL-NetServer listening on port 55555!");
        server.startAsync();
    }

    public static void main(String[] args) {
        launch(args);
    }
}