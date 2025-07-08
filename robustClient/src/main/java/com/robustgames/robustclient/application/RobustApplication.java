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
    private VBox gamemodeMenu;
    private VBox waitingBox;

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
        showGamemodeMenu();
    }

    private void showGamemodeMenu() {
        gamemodeMenu = new VBox(30);
        gamemodeMenu.setTranslateX(WIDTH / 2.0 - 100);
        gamemodeMenu.setTranslateY(HEIGHT / 2.0 - 100);

        Text title = new Text("Choose Gamemode");
        Button btnLocal = new Button("Local");
        Button btnOnline = new Button("Online Multiplayer");

        btnLocal.setOnAction(e -> {
            selectedGamemode = Gamemode.LOCAL;
            FXGL.getGameScene().removeUINode(gamemodeMenu);
            startGameAfterMenu();
        });

        btnOnline.setOnAction(e -> {
            selectedGamemode = Gamemode.ONLINE;
            FXGL.getGameScene().removeUINode(gamemodeMenu);
            startGameAfterMenu();
        });

        gamemodeMenu.getChildren().addAll(title, btnLocal, btnOnline);
        FXGL.getGameScene().addUINode(gamemodeMenu);
    }

    private void startGameAfterMenu() {
        if (selectedGamemode == Gamemode.ONLINE) {
            initializeNetworkClient("localhost", 55555);
            showWaitingForOpponent();
            FXGL.getNotificationService().pushNotification("Waiting for other player to join...");
        } else if (selectedGamemode == Gamemode.LOCAL) {
            startLocalGame();
        }
    }

    private void showWaitingForOpponent() {
        waitingBox = new VBox(30);
        waitingBox.setTranslateX(WIDTH / 2.0 - 100);
        waitingBox.setTranslateY(HEIGHT / 2.0 - 100);
        Text waitingText = new Text("Waiting for other player to join..");
        waitingBox.getChildren().add(waitingText);
        FXGL.getGameScene().addUINode(waitingBox);
    }

    private void hideWaitingForOpponent() {
        if (waitingBox != null) {
            FXGL.getGameScene().removeUINode(waitingBox);
            waitingBox = null;
        }
    }

    private void startLocalGame() {
        continueLocalGameSetup();
    }

    private void startOnlineGame() {
        continueOnlineGameSetup();
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

            Bundle hello = new Bundle("hello");
            conn.send(hello);

            conn.addMessageHandlerFX((c, responseBundle) -> {
                System.out.println("Received from server: " + responseBundle);

                switch (responseBundle.getName()) {
                    case "GameStart" -> {
                        assignedPlayer = responseBundle.get("assignedPlayer");
                        System.out.println("Assigned role: " + assignedPlayer);
                        hideWaitingForOpponent();
                        continueOnlineGameSetup();
                    }
                    case "ServerACK" -> {
                        System.out.println("ACK received: " + responseBundle.get("originalBundle"));
                    }
                    case "Reject" -> {
                        System.out.println("Rejected: " + responseBundle.get("message"));
                        getGameController().exit();
                    }
                    case "MoveAction" -> {
                        System.out.println("MoveAction empfangen: " + responseBundle);

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
                        System.out.println("RotateAction empfangen: " + responseBundle);

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
                        startOnlineGame();
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

    private void continueLocalGameSetup() {
        initGameLogicAndUI();
    }

    private void continueOnlineGameSetup() {
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

    public static void main(String[] args) {
        launch(args);
    }
}
