package com.robustgames.robustclient.application;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.entity.action.ActionComponent;
import com.almasb.fxgl.net.Client;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.net.Connection;
import com.robustgames.robustclient.business.actions.MovementAction;
import com.robustgames.robustclient.business.actions.RotateAction;
import com.robustgames.robustclient.business.actions.ShootAction;
import com.robustgames.robustclient.business.entitiy.components.APComponent;
import com.robustgames.robustclient.business.entitiy.components.IDComponent;
import com.robustgames.robustclient.business.factories.IDFactory;
import com.robustgames.robustclient.business.entitiy.components.SelectableComponent;
import com.robustgames.robustclient.business.factories.MapFactory;
import com.robustgames.robustclient.business.factories.PlayerFactory;
import com.robustgames.robustclient.business.logic.Gamemode;
import com.robustgames.robustclient.business.logic.gameService.MapService;
import com.robustgames.robustclient.business.logic.Player;
import com.robustgames.robustclient.business.logic.gameService.TurnService;
import com.robustgames.robustclient.presentation.scenes.TankButtonView;
import com.robustgames.robustclient.presentation.scenes.TankDataView;
import com.robustgames.robustclient.presentation.scenes.EndTurnView;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.robustgames.robustclient.business.entitiy.EntityType.*;

import static com.robustgames.robustclient.business.entitiy.EntityType.TILE;

public class RobustApplication extends GameApplication {
    private Gamemode selectedGamemode = null;

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    TankButtonView tankButtonView;
    TankDataView tankDataView;
    EndTurnView endTurnView;
    private int clientId = -1; // -1 = nicht gesetzt

    private Connection<Bundle> connection;

    private String assignedPlayer;

    // Neu: Factory-Flag!
    private boolean factoriesAdded = false;

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

        // Menüs aktivieren
        settings.setMainMenuEnabled(true);
        settings.setGameMenuEnabled(false);

        // <<< Menü via SceneFactory registrieren >>>
        settings.setSceneFactory(new com.almasb.fxgl.app.scene.SceneFactory() {
            @Override
            public com.almasb.fxgl.app.scene.FXGLMenu newMainMenu() {
                return new com.robustgames.robustclient.presentation.scenes.menu.RobustMainMenu(RobustApplication.this);
            }
        });
    }

    @Override
    protected void initInput() {
        onBtnDown(MouseButton.SECONDARY, () -> {
            deSelectTank();
            if (tankDataView != null) tankDataView.setVisible(false);
            if (tankButtonView != null) tankButtonView.setVisible(false);
        });
    }

    @Override
    protected void initUI() {
        // UI-Elemente erst nach Setup erzeugen (siehe initGameLogicAndUI)
        // NICHT direkt hier, da sonst NullPointer durch fehlende Instanzen!
    }

    public void onTankClicked(Entity tank) {
        if (tankButtonView != null) tankButtonView.setVisible(true);
        if (tankDataView != null) {
            tankDataView.setVisible(true);
            tankDataView.setSelectedTank(tank);
        }
    }

    public void deSelectTank() {
        Entity tank = MapService.findSelectedTank();
        if (tank != null) {
            tank.removeComponent(SelectableComponent.class);
            if (tankButtonView != null) tankButtonView.setVisible(false);
            if (tankDataView != null) tankDataView.setVisible(false);
        }
    }

    @Override
    protected void initGame() {
        if (selectedGamemode == Gamemode.LOCAL) {
            continueGameSetup();
        }
    }

    private void startLocalGame() {
        continueGameSetup();
    }

    private void addEntityFactoriesOnce() {
        if (!factoriesAdded) {
            FXGL.getGameWorld().addEntityFactory(new MapFactory());
            FXGL.getGameWorld().addEntityFactory(new PlayerFactory());
            factoriesAdded = true;
        }
    }

    private void initializeNetworkClient(String ip, int port) {
        Client<Bundle> client = getNetService().newTCPClient(ip, port);

        client.setOnConnected(conn -> {
            connection = conn;

            // Begrüßung an Server
            Bundle hello = new Bundle("hello");
            conn.send(hello);

            // Alle Server-Messages kommen bereits auf dem FX-Thread an (addMessageHandlerFX)
            conn.addMessageHandlerFX((c, responseBundle) -> {
                System.out.println("Received from server: " + responseBundle);

                switch (responseBundle.getName()) {
                    case "GameStart" -> {
                        // Server hat Rollen zugewiesen -> jetzt erst Spielwelt aufbauen
                        assignedPlayer = responseBundle.get("assignedPlayer");
                        System.out.println("Assigned role: " + assignedPlayer);
                        // Kein hideWaitingForOpponent() mehr – das alte UI ist weg
                        continueGameSetup();     // oder: continueOnlineGameSetup(), falls du das getrennt haben willst
                    }

                    case "ServerACK" -> {
                        System.out.println("ACK received: " + responseBundle.get("originalBundle"));
                    }

                    case "Reject" -> {
                        System.out.println("Rejected: " + responseBundle.get("message"));
                        getGameController().exit();
                    }

                    case "MoveAction" -> {
                        long entityId = responseBundle.get("entityId");
                        double toX = responseBundle.get("toX");
                        double toY = responseBundle.get("toY");

                        Entity tank = FXGL.getGameWorld().getEntitiesByComponent(IDComponent.class).stream()
                                .filter(e -> e.getComponent(IDComponent.class).getId() == entityId)
                                .findFirst()
                                .orElse(null);

                        if (tank != null) {
                            Point2D screenTarget = MapService.isoGridToScreen(toX, toY).subtract(64, 64);
                            Entity dummyTarget = FXGL.entityBuilder().at(screenTarget).build();

                            MovementAction moveAction = new MovementAction(dummyTarget, false);
                            tank.getComponent(ActionComponent.class).addAction(moveAction);
                            tank.getComponent(ActionComponent.class).pause();
                        }
                    }

                    case "RotateAction" -> {
                        long entityId = responseBundle.get("entityId");
                        String textureName = responseBundle.get("direction") + ".png";

                        Entity tank = FXGL.getGameWorld().getEntitiesByComponent(IDComponent.class).stream()
                                .filter(e -> e.getComponent(IDComponent.class).getId() == entityId)
                                .findFirst()
                                .orElse(null);

                        if (tank == null) {
                            System.err.println("Tank mit ID " + entityId + " nicht gefunden");
                            return;
                        }

                        RotateAction rotateAction = new RotateAction(textureName, false);
                        ActionComponent ac = tank.getComponent(ActionComponent.class);
                        ac.addAction(rotateAction);
                        ac.pause();
                    }

                    case "ShootAction" -> {
                        long shooterId = responseBundle.get("shooterId");
                        long targetId = responseBundle.get("targetId");

                        Entity shooter = FXGL.getGameWorld().getEntitiesByComponent(IDComponent.class).stream()
                                .filter(e -> e.getComponent(IDComponent.class).getId() == shooterId)
                                .findFirst().orElse(null);

                        Entity target = FXGL.getGameWorld().getEntitiesByComponent(IDComponent.class).stream()
                                .filter(e -> e.getComponent(IDComponent.class).getId() == targetId)
                                .findFirst().orElse(null);

                        if (shooter != null && target != null) {
                            ShootAction shootAction = new ShootAction(target, false);
                            shooter.getComponent(ActionComponent.class).addAction(shootAction);
                            shooter.getComponent(ActionComponent.class).pause();
                        } else {
                            System.err.println("Shooter oder Target nicht gefunden");
                        }
                    }

                    case "ExecuteTurn" -> {
                        System.out.println("ExecuteTurn empfangen - Aktionen starten");

                        FXGL.getGameWorld().getEntitiesByType(TANK).forEach(tank -> {
                            ActionComponent ac = tank.getComponent(ActionComponent.class);
                            if (ac.isPaused()) {
                                ac.resume();
                            }
                            tank.getComponent(APComponent.class).reset();
                        });
                    }

                    case "assign_id" -> {
                        int id = responseBundle.get("clientId");
                        setClientId(id);
                        System.out.println("Client-ID erhalten: " + id);
                        // KEIN startOnlineGame() mehr hier!
                        // Das eigentliche Setup startet, wenn "GameStart" kommt.
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

    private void continueGameSetup() {
        initGameLogicAndUI();
    }

    private void initGameLogicAndUI() {
        addEntityFactoriesOnce(); // WICHTIG: Nur einmal registrieren!
        getGameScene().getViewport().setY(-100);

        // UI hier initialisieren!
        tankButtonView = new TankButtonView();
        tankDataView = new TankDataView();
        endTurnView = new EndTurnView();
        addUINode(endTurnView);
        addUINode(tankButtonView);
        addUINode(tankDataView);

        endTurnView.setVisible(true);
        tankButtonView.setVisible(false);
        tankDataView.setVisible(false);

        FXGL.spawn("Background", new SpawnData(0, -100).put("width", WIDTH).put("height", HEIGHT));
        FXGL.setLevelFromMap("mapTest.tmx");

        GameWorld world = getGameWorld();
        List<Entity> allEntities = world.getEntities();
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

    public Connection<Bundle> getConnection() {
        return this.connection;
    }

    public String getAssignedPlayer() {
        return assignedPlayer;
    }

    public Gamemode getSelectedGamemode() {
        return selectedGamemode;
    }

    // --- Menü-Bridge: NICHTS SONST ÄNDERN ---
    public void startLocalFromMenu() {
        selectedGamemode = Gamemode.LOCAL;
    }

    public void startOnlineFromMenu(String ip, int port) {
        selectedGamemode = Gamemode.ONLINE;
        initializeNetworkClient(ip, port);
        com.almasb.fxgl.dsl.FXGL.getNotificationService()
                .pushNotification("Waiting for other player to join...");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
