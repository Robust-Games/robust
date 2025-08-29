/**
 * @author Burak Altun, Carolin Scheffler, Ersin Yesiltas, Nico Steiner
 */
package com.robustgames.robustclient.business.logic.networkService;

import com.almasb.fxgl.core.EngineService;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.net.Client;
import com.almasb.fxgl.net.Connection;
import com.robustgames.robustclient.application.RobustApplication;
import com.robustgames.robustclient.business.factories.BundleFactory;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.almasb.fxgl.dsl.FXGL.*;

public class ConnectionService extends EngineService {
    private static ConnectionService instance;
    private final ConnectionMessageHandler messageHandler;
    private final boolean firstHeartbeat = true;
    private final BooleanProperty connected = new SimpleBooleanProperty(false);
    RobustApplication app;
    private Client<Bundle> client;
    private Connection<Bundle> connection;
    private ScheduledExecutorService heartbeatMonitor;
    private ScheduledExecutorService connectionMonitor;
    private long lastHeartbeatTime = 0;

    public ConnectionService() {
        messageHandler = new ConnectionMessageHandler();
        this.app = FXGL.getAppCast();
    }

    public void initializeNetworkClient(String ip, int port) {
        try {
            client = getNetService().newTCPClient(ip, port);
            client.setOnConnected(conn -> {
                connection = conn;
                connected.set(true);
                Bundle hello = new Bundle("hello");
                conn.send(hello);

                conn.addMessageHandlerFX((c, responseBundle) -> {
                    System.out.println("Received from server: " + responseBundle);
                    if ("heartbeat".equals(responseBundle.getName())) {
                        if (firstHeartbeat) {
                            startConnectionMonitoring();
                        }
                        lastHeartbeatTime = System.currentTimeMillis();
                        BundleFactory.signalPlayerAlive();
                    } else {
                        messageHandler.handleMessage(responseBundle);
                    }
                });
            });
            client.connectAsync();

            // Set a timeout for connection
            Thread initialConnTimeoutThread = getInitialConnTimeoutThread();
            initialConnTimeoutThread.start();

        } catch (Exception e) {
            Platform.runLater(() -> {
                app.hideWaitingForOpponent();
                getDialogService().showMessageBox("Connection error: " + e.getMessage(), () -> getGameController().gotoMainMenu());
            });
        }
    }

    public void robustDisconnect() {
        connected.set(false);

        if (connectionMonitor != null) {
            connectionMonitor.shutdown();
            connectionMonitor = null;
        }

        if (connection != null) {
            connection.terminate();
            connection = null;
        }

        if (client != null) {
            client.disconnect();
            client = null;
        }
    }

    @NotNull
    private Thread getInitialConnTimeoutThread() {
        Thread connectionTimeoutThread = new Thread(() -> {
            try {
                Thread.sleep(5000); // 5-second timeout
                if (connection == null) {
                    Platform.runLater(() -> {
                        app.hideWaitingForOpponent();
                        getDialogService().showMessageBox("Connection timed out: Server not responding", () -> getGameController().gotoMainMenu());
                    });
                }
            } catch (InterruptedException e) {
                // Connection succeeded before timeout
            }
        });
        connectionTimeoutThread.setDaemon(true);
        return connectionTimeoutThread;
    }

    private void startConnectionMonitoring() {
        connectionMonitor = Executors.newSingleThreadScheduledExecutor();
        connectionMonitor.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastHeartbeatTime > 11000) { // 10 seconds without heartbeat
                Platform.runLater(() -> {
                    getDialogService().showMessageBox("Connection to server lost",
                            () -> getGameController().gotoMainMenu());
                });
                robustDisconnect();
                connectionMonitor.shutdown();
            }
        }, 10, 3, TimeUnit.SECONDS);
    }

    public Connection<Bundle> getConnection() {
        return this.connection;
    }

    public BooleanProperty connectedProperty() {
        return connected;
    }

    public boolean isConnected() {
        return connected.get();
    }
}