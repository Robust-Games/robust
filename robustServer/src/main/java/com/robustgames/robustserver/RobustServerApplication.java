package com.robustgames.robustserver;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.net.NetServer;
import com.almasb.fxgl.net.NetworkService;

public class RobustServerApplication extends GameApplication {

    private NetServer<Object> server;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Robust Server");
        settings.setWidth(400);
        settings.setHeight(300);
    }

    @Override
    protected void initGame() {
        // Hole den NetworkService aus FXGL
        NetworkService network = getGameScene().getApplication().getService(NetworkService.class);

        // Erstelle einen NetServer (Objekttyp für serialisierte Messages)
        server = network.newNetServer(7777, Object.class);

        // Beispiel-Listener für Messages
        server.addMessageHandler(Object.class, (conn, data) -> {
            System.out.println("Received from client: " + data);
        });

        System.out.println("FXGL-NetServer läuft auf Port 7777!");
        server.startAsync();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
