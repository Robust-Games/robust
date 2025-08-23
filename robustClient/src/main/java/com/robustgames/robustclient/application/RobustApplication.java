/**
 * @author Burak Altun, Carolin Scheffler, Ersin Yesiltas, Nico Steiner
 */
package com.robustgames.robustclient.application;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.app.scene.StartupScene;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.action.ActionComponent;
import com.almasb.fxgl.net.Client;
import com.almasb.fxgl.net.Connection;
import com.robustgames.robustclient.business.actions.MovementAction;
import com.robustgames.robustclient.business.actions.RotateAction;
import com.robustgames.robustclient.business.actions.ShootAction;
import com.robustgames.robustclient.business.entitiy.components.*;
import com.robustgames.robustclient.business.factories.IDFactory;
import com.robustgames.robustclient.business.factories.MapFactory;
import com.robustgames.robustclient.business.factories.PlayerFactory;
import com.robustgames.robustclient.business.logic.Gamemode;
import com.robustgames.robustclient.business.logic.Player;
import com.robustgames.robustclient.business.logic.gameService.MapService;
import com.robustgames.robustclient.business.logic.gameService.SoundService;
import com.robustgames.robustclient.business.logic.gameService.TurnService;
import com.robustgames.robustclient.presentation.scenes.EndTurnView;
import com.robustgames.robustclient.presentation.scenes.RobustStartupScene;
import com.robustgames.robustclient.presentation.scenes.TankButtonView;
import com.robustgames.robustclient.presentation.scenes.TankDataView;
import com.robustgames.robustclient.presentation.scenes.menus.RobustMainMenu;
import com.robustgames.robustclient.presentation.scenes.menus.RobustPauseMenu;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import static com.almasb.fxgl.dsl.FXGL.texture;

import java.net.ConnectException;
import java.util.Arrays;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.robustgames.robustclient.business.entitiy.EntityType.*;

public class RobustApplication extends GameApplication {
    public Gamemode selectedGamemode = null;
    private VBox gamemodeMenu;
    Pane waitingPane;
    private VBox waitingBox;

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    TankButtonView tankButtonView;
    TankDataView tankDataView;
    EndTurnView endTurnView;
    private int clientId = -1; // -1 = not set

    private Connection<Bundle> connection;

    private String assignedPlayer;

    // New: Factory-Flag!
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
        settings.setVersion("0.4");
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newGameMenu() {
                return new RobustPauseMenu();
            }

            @Override
            public FXGLMenu newMainMenu() {
                return new RobustMainMenu(MenuType.MAIN_MENU);

            }

            @Override
            public StartupScene newStartup(int width, int height) {
                return new RobustStartupScene(width, height);
            }

        });
        settings.setMainMenuEnabled(true);
        settings.setGameMenuEnabled(true);
        settings.setFullScreenAllowed(true);
        settings.getCredits().addAll(Arrays.asList(
                "Robust Games\n",
                "-Developed by-",
                "Burak Altun",
                "Carolin Scheffler",
                "Ersin Yesiltas",
                "Nico Steiner\n\n",

                "-A Game made for Hochschule RheinMain-"
        ));
        settings.getCSSList().add("style.css");
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
        settings.setFontUI("ARCADE_R.ttf");
        settings.setFontGame("ARCADE_N.ttf");
        settings.setTicksPerSecond(60);

    }

    @Override
    protected void onUpdate(double tpf) {
        super.onUpdate(tpf);

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
        if (selectedGamemode != null && selectedGamemode.equals(Gamemode.LOCAL)) {
            endTurnView.setVisible(true);
            tankButtonView.setVisible(false);
            tankDataView.setVisible(false);
            addUINode(endTurnView);
            addUINode(tankButtonView);
            addUINode(tankDataView);
        }

    }

    /**
     * Selects a tank by adding the {@code SelectableComponent} and making its UI elements visible
     */
    public void onTankClicked(Entity tank) {
        if (tankButtonView != null) tankButtonView.setVisible(true);
        if (tankDataView != null) {
            tankDataView.setVisible(true);
            tankDataView.setSelectedTank(tank);
        }
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
            if (tank.hasComponent(ShootComponent.class)) {
                tank.removeComponent(ShootComponent.class);
            }
            if (tank.hasComponent(MovementComponent.class)) {
                tank.removeComponent(MovementComponent.class);
            }
            if (tankButtonView != null) tankButtonView.setVisible(false);
            if (tankDataView != null) tankDataView.setVisible(false);
        }
    }

    @Override
    protected void initGame() {
        if (selectedGamemode != null) {
            if (selectedGamemode == Gamemode.ONLINE) {
                startGameAfterMenu();
            } else if (selectedGamemode == Gamemode.LOCAL) {
                initLocalGameLogicAndUI();
            }
        }
    }

    private void showGamemodeMenu() {
        gamemodeMenu = new VBox(30);
        gamemodeMenu.setTranslateX(WIDTH / 2.0 - 100);
        gamemodeMenu.setTranslateY(HEIGHT / 2.0 - 100);

        Text title = new Text("Confirm connection");
        Button btnOnline = new Button("Connect to Online Services");

        btnOnline.setOnAction(e -> {
            FXGL.getGameScene().removeUINode(gamemodeMenu);
            startGameAfterMenu();
        });

        gamemodeMenu.getChildren().addAll(title, btnOnline);
        FXGL.getGameScene().addUINode(gamemodeMenu);
    }

    private void startGameAfterMenu() {
        initializeNetworkClient("localhost", 55555);
        showWaitingForOpponent();
        //FXGL.getNotificationService().pushNotification("Waiting for other player to join...");

    }

    private void showWaitingForOpponent() {
        waitingPane = new Pane();
        waitingBox = new VBox(30);
        waitingBox.setTranslateX(WIDTH / 2.0 - 100);
        waitingBox.setTranslateY(HEIGHT / 2.0 - 100);

        Text waitingText = new Text("Waiting for other player to join");
        waitingText.getStyleClass().add("robust-btn-menu-text");
        ImageView background = texture("background.png", getAppWidth(), getAppHeight());

        waitingBox.getChildren().add(waitingText);
        waitingPane.getChildren().addAll(background, waitingBox);

        FXGL.getGameScene().addUINode(waitingPane);
    }

    private void hideWaitingForOpponent() {
        if (waitingBox != null) {
            FXGL.getGameScene().removeUINode(waitingPane);
            waitingBox = null;
        }
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
                        System.out.println("Received RotateAction: " + responseBundle);

                        long entityId = responseBundle.get("entityId");
                        String textureName = responseBundle.get("direction") + ".png";

                        Entity tank = FXGL.getGameWorld().getEntitiesByComponent(IDComponent.class).stream()
                                .filter(e -> e.getComponent(IDComponent.class).getId() == entityId)
                                .findFirst()
                                .orElse(null);

                        if (tank == null) {
                            System.err.println("Tank with ID " + entityId + " not found.");
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
                            System.err.println("Shooter or Target not found");
                        }
                    }
                    case "ExecuteTurn" -> {
                        System.out.println("ExecuteTurn received - starting turn actions");

                        FXGL.getGameWorld().getEntitiesByType(TANK).forEach(tank -> {
                            ActionComponent ac = tank.getComponent(ActionComponent.class);
                            if (ac.isPaused()) {
                                ac.resume();
                            }
                            Platform.runLater(() -> getEndTurnView().disableProperty().setValue(false));
                            tank.getComponent(APComponent.class).reset();
                        });
                    }


                    case "assign_id" -> {
                        int id = responseBundle.get("clientId");
                        setClientId(id);
                        System.out.println("received Client-ID: " + id);
                        startOnlineGame();
                    }
                    case "hello" -> {
                        System.out.println("received Hello from Server");
                    }
                    default -> {
                        System.out.println("Unhandled bundle: " + responseBundle.getName());
                    }
                }
            });
        });
        client.connectAsync();
    }

    private void continueOnlineGameSetup() {
        initOnlineGameLogicAndUI();
    }

    private void initLocalGameLogicAndUI() {
        addEntityFactoriesOnce(); // IMPORTANT: only register once!
        getGameScene().getViewport().setY(-100);
        tankButtonView = new TankButtonView();
        tankDataView = new TankDataView();
        endTurnView = new EndTurnView();

        FXGL.spawn("Background", new SpawnData(0, -100).put("width", WIDTH).put("height", HEIGHT));
        FXGL.spawn("MapBorder", new SpawnData(0, -100).put("width", WIDTH).put("height", HEIGHT));
        FXGL.setLevelFromMap("mapTest2.tmx");

        GameWorld world = getGameWorld();
        List<Entity> allEntities = world.getEntities(); //.subList(2, world.getEntities().size()) -> because the textures are entities that we can't filter by type
        for (Entity entity : allEntities) {
            moveEntityToIsometric(entity);
        }
        TurnService.startTurn(Player.PLAYER1);
    }

    private void initOnlineGameLogicAndUI() {
        addEntityFactoriesOnce(); // IMPORTANT: only register once!
        getGameScene().getViewport().setY(-100);

        // initialise UI here
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
        FXGL.spawn("MapBorder", new SpawnData(0, -100).put("width", WIDTH).put("height", HEIGHT));
        FXGL.setLevelFromMap("mapTest2.tmx");
        GameWorld world = getGameWorld();
        List<Entity> allEntities = world.getEntities(); //.subList(2, world.getEntities().size()) -> weil die Texturen Entitaeten sind, die wir nicht mit TYPE filtern koennen
        for (Entity entity : allEntities) {
            if ((entity.isType(TILE) || entity.isType(MOUNTAIN) || entity.isType(TANK) || entity.isType(CITY))
                    && !entity.hasComponent(IDComponent.class)) {
                long id = IDFactory.generateId();
                entity.addComponent(new IDComponent(id));
            }
            moveEntityToIsometric(entity);
        }
        TurnService.startTurn(Player.PLAYER1);
    }

    private void moveEntityToIsometric(Entity entity) {
        Platform.runLater(()->SoundService.pickSong());
        Point2D orthGridPos = MapService.orthScreenToGrid(entity.getPosition());
        Point2D isoScreenPos = MapService.isoGridToScreen(orthGridPos.getX(), orthGridPos.getY());
        if (entity.isType(TILE)) {
            entity.setPosition(isoScreenPos.getX() - 64, isoScreenPos.getY() + 1);
        } else if (entity.isType(HOVER)) {
            entity.setPosition(isoScreenPos.getX(), isoScreenPos.getY());
        } else if (entity.isType(MOUNTAIN) || entity.isType(TANK) || entity.isType(CITY))
            entity.setPosition(isoScreenPos.getX() - 64, isoScreenPos.getY() - 64);
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

    public Pane getEndTurnView() {
        return endTurnView;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

