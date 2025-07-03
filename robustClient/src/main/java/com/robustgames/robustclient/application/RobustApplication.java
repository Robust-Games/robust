package com.robustgames.robustclient.application;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Client;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.net.Connection;
import com.robustgames.robustclient.business.entitiy.components.IDComponent;
import com.robustgames.robustclient.business.factories.IDFactory;
import com.robustgames.robustclient.business.entitiy.components.SelectableComponent;
import com.robustgames.robustclient.business.factories.MapFactory;
import com.robustgames.robustclient.business.factories.PlayerFactory;
import com.robustgames.robustclient.business.logic.gameService.MapService;
import com.robustgames.robustclient.business.logic.Player;
import com.robustgames.robustclient.business.logic.gameService.TurnService;
import com.robustgames.robustclient.presentation.scenes.TankButtonView;
import com.robustgames.robustclient.presentation.scenes.TankDataView;
import com.robustgames.robustclient.presentation.scenes.EndTurnView;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.robustgames.robustclient.business.entitiy.EntityType.*;

import static com.robustgames.robustclient.business.entitiy.EntityType.TILE;

public class RobustApplication extends GameApplication {
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    TankButtonView tankButtonView;
    TankDataView tankDataView;
    EndTurnView endTurnView;
    private int clientId = -1; // -1 = nicht gesetzt

    private Connection<Bundle> connection;

    private String assignedPlayer;

    public void setClientId(int id) {
        this.clientId = id;
    }

    public int getClientId() {
        return clientId;
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("ROBUST");
        settings.setVersion("0.3");
        settings.getCSSList().add("style.css");
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
    }

    @Override
    protected void initInput() {
        onBtnDown(MouseButton.SECONDARY, () -> {
            deSelectTank();
            tankDataView.setVisible(false);
            tankButtonView.setVisible(false);
        });

        //ROBUST_DEBUG
//        onBtnDown(MouseButton.PRIMARY, () -> {
//            Point2D mouseWorldPos = FXGL.getInput().getMousePositionWorld();
//            Point2D gridPos = MapService.isoScreenToGrid(mouseWorldPos);
//            System.out.println("\nFXGL Mouse coordinates = " + mouseWorldPos
//                    + "\nisometric Screen To Grid = " + gridPos
//                    + "\nisometric Grid To Screen = " + MapService.isoGridToScreen(gridPos.getX(), gridPos.getY())
//                    + "\northogonal Screen To Grid = " + MapService.orthScreenToGrid(mouseWorldPos)
//                    + "\northogonal Grid To Screen = " + MapService.orthGridToScreen(MapService.orthScreenToGrid(mouseWorldPos).getX(), MapService.orthScreenToGrid(mouseWorldPos).getY()));
//        });
    }

    @Override
    protected void initUI() {
        endTurnView.setVisible(true);
        tankButtonView.setVisible(false);
        tankDataView.setVisible(false);
        addUINode(endTurnView);
        addUINode(tankButtonView);
        addUINode(tankDataView);

    }

    /**
     * Selects a tank by adding the {@code SelectableComponent} and making its UI elements visible
     */
    public void onTankClicked(Entity tank) {
        tankButtonView.setVisible(true);
        tankDataView.setVisible(true);
        tankDataView.setSelectedTank(tank);
    }

    /**
     * Deselects the currently selected tank by removing its {@code SelectableComponent}.
     * This method identifies the selected tank by checking for an entity with a {@code SelectableComponent}
     * which only tanks get assigned.
     */
    public void deSelectTank() {
        Entity tank = MapService.findSelectedTank();
        if (tank != null) {
            tank.removeComponent(SelectableComponent.class);
            tankButtonView.setVisible(false);
            tankDataView.setVisible(false);
        }
    }

    @Override
    protected void initGame() {
        // Netzwerkverbindung starten (asynchron)
        initializeNetworkClient("localhost", 55555);

        // Warten auf Server-Antwort (clientId != -1)
        while (clientId == -1) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        getGameScene().getViewport().setY(-100);
        // getGameScene().getViewport().setZoom(100);
        tankButtonView = new TankButtonView();
        tankDataView = new TankDataView();
        endTurnView = new EndTurnView();

        FXGL.getGameWorld().addEntityFactory(new MapFactory());
        FXGL.getGameWorld().addEntityFactory(new PlayerFactory());
        FXGL.spawn("Background", new SpawnData(0, -100).put("width", WIDTH).put("height", HEIGHT));
        FXGL.setLevelFromMap("mapTest.tmx"); //map2D.tmx für 2D und mapTest.tmx für Isometrisch

        GameWorld world = getGameWorld();
        List<Entity> allEntities = world.getEntities(); //.subList(2, world.getEntities().size()) -> weil die Texturen Entitaeten sind, die wir nicht mit TYPE filtern koennen
        for (Entity entity : allEntities) {
            if ((entity.isType(TILE) || entity.isType(MOUNTAIN) || entity.isType(TANK) || entity.isType(CITY))
                    && !entity.hasComponent(IDComponent.class)) {
                long id = IDFactory.generateId();
                entity.addComponent(new IDComponent(id));
            }
            Point2D orthGridPos = MapService.orthScreenToGrid(entity.getPosition());
            Point2D isoGridPos = MapService.isoGridToScreen(orthGridPos.getX(), orthGridPos.getY());
            if (entity.isType(TILE)) {
                entity.setPosition(isoGridPos.getX(), isoGridPos.getY());
            } else if (entity.isType(MOUNTAIN) || entity.isType(TANK) || entity.isType(CITY))
                entity.setPosition(isoGridPos.getX() - 64, isoGridPos.getY() - 64);
        }
        TurnService.startTurn(Player.PLAYER1);
    }

    /**
     * Initializes the network client and establishes a connection to the server.
     * Once the connection is established, the Connection object is stored for later use,
     * and a message handler is registered to handle incoming responses from the server.
     */
    private void initializeNetworkClient(String ip, int port) {
        Client<Bundle> client = getNetService().newTCPClient(ip, port);
        client.setOnConnected(conn -> {
            connection = conn;

            Bundle hello = new Bundle("hello");
            conn.send(hello);

            conn.addMessageHandlerFX((c, responseBundle) -> {
                System.out.println("Received from server: " + responseBundle);

                switch (responseBundle.getName()) {
                    case "GameStart" -> {
                        assignedPlayer = responseBundle.get("assignedPlayer");
                        System.out.println("Assigned role: " + assignedPlayer);
                    }
                    case "ServerACK" -> {
                        System.out.println("ACK received: " + responseBundle.get("originalBundle"));
                    }
                    case "Reject" -> {
                        System.out.println("Rejected: " + responseBundle.get("message"));
                        getGameController().exit();
                    }
                    case "move_action" -> {
                        System.out.println("MoveAction empfangen: " + responseBundle);
                        // TODO: verarbeitung bundle
                    }
                    case "assign_id" -> {
                        int id = responseBundle.get("id");
                        setClientId(id);
                        System.out.println("Client-ID erhalten: " + id);
                    }
                    case "hello" -> {
                        System.out.println("Hello vom Server erhalten");
                    }
                    default -> {
                        System.out.println("Unhandled bundle: " + responseBundle.getName());
                    }
                }
            });
        });
        client.connectAsync();
    }

    /**
     * Returns the current active network connection to the server.
     *
     * @return the active Connection for sending and receiving bundles, or null if not connected
     */
    public Connection<Bundle> getConnection() {
        return this.connection;
    }

    /**
     * Returns the assigned player role from the server ("PLAYER1" or "PLAYER2").
     *
     * @return the assigned player role string
     */
    public String getAssignedPlayer() {
        return assignedPlayer;
    }


    public static void main(String[] args) {
        launch(args);
    }
}