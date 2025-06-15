package com.robustgames.robustserver;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Server;

import static com.almasb.fxgl.dsl.FXGL.*;

public class RobustServerApplication extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Robust Server");
        settings.setWidth(640);
        settings.setHeight(480);
    }


    @Override
    protected void initGame() {
        Server<Bundle> server = getNetService().newTCPServer(55555);

        // Handler wird pro Client-Connection gesetzt!
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