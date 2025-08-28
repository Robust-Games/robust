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
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.ui.FontType;
import com.robustgames.robustclient.business.entitiy.components.*;
import com.robustgames.robustclient.business.factories.IDFactory;
import com.robustgames.robustclient.business.factories.MapFactory;
import com.robustgames.robustclient.business.factories.PlayerFactory;
import com.robustgames.robustclient.business.logic.Gamemode;
import com.robustgames.robustclient.business.logic.Player;
import com.robustgames.robustclient.business.logic.gameService.MapService;
import com.robustgames.robustclient.business.logic.gameService.SoundService;
import com.robustgames.robustclient.business.logic.gameService.TurnService;
import com.robustgames.robustclient.business.logic.networkService.ConnectionService;
import com.robustgames.robustclient.presentation.scenes.EndTurnView;
import com.robustgames.robustclient.presentation.scenes.RobustStartupScene;
import com.robustgames.robustclient.presentation.scenes.TankButtonView;
import com.robustgames.robustclient.presentation.scenes.TankDataView;
import com.robustgames.robustclient.presentation.scenes.menus.RobustMainMenu;
import com.robustgames.robustclient.presentation.scenes.menus.RobustPauseMenu;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.robustgames.robustclient.business.entitiy.EntityType.*;

public class RobustApplication extends GameApplication {
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    public Gamemode selectedGamemode = null;
    Pane waitingPane;
    TankButtonView tankButtonView;
    TankDataView tankDataView;
    EndTurnView endTurnView;
    private String serverIP = "localhost";
    private int serverPort = 55555;
    private VBox waitingBox;
    private Text waitingText;
    private int clientId = -1; // -1 = not set
    private String assignedPlayer;

    public static void main(String[] args) {
        launch(args);
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int id) {
        this.clientId = id;
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
        settings.getCredits().addAll(Arrays.asList("Robust Games\n", "-Developed by-", "Burak Altun", "Carolin Scheffler", "Ersin Yesiltas", "Nico Steiner\n\n",

                "-A Game made for Hochschule RheinMain-"));
        settings.getCSSList().add("style.css");
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
        settings.setFontUI("ARCADE_R.ttf");
        settings.setFontGame("ARCADE_N.ttf");
        settings.setTicksPerSecond(60);

        settings.addEngineService(ConnectionService.class);
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

    private void startGameAfterMenu() {
        showWaitingForOpponent();
        FXGL.getService(ConnectionService.class).initializeNetworkClient(serverIP, serverPort);
    }

    private void showWaitingForOpponent() {
        waitingText = FXGL.getUIFactoryService().newText("Connecting to Server", Color.WHITE, FontType.GAME, 36);
        waitingText.setEffect(new DropShadow(8, Color.BLACK));
        waitingText.getStyleClass().add("robust-btn-menu-text");

        waitingPane = new Pane();
        waitingBox = new VBox(30);
        waitingBox.setAlignment(Pos.CENTER);
        waitingBox.setTranslateX(getAppWidth() / 2.0 - waitingText.getLayoutBounds().getWidth() / 2.0);
        waitingBox.setTranslateY(getAppHeight() / 2.0);

        ImageView background = texture("background.png", getAppWidth(), getAppHeight());

        waitingBox.getChildren().add(waitingText);
        waitingPane.getChildren().addAll(background, waitingBox);

        FXGL.getGameScene().addUINode(waitingPane);
    }

    public void hideWaitingForOpponent() {
        if (waitingBox != null) {
            FXGL.getGameScene().removeUINode(waitingPane);
            waitingBox = null;
        }
    }

    private void initLocalGameLogicAndUI() {
        FXGL.getGameWorld().addEntityFactory(new MapFactory());
        FXGL.getGameWorld().addEntityFactory(new PlayerFactory());
        getGameScene().getViewport().setY(-100);
        tankButtonView = new TankButtonView();
        tankDataView = new TankDataView();
        endTurnView = new EndTurnView();

        FXGL.spawn("Background", new SpawnData(0, -100).put("width", WIDTH).put("height", HEIGHT));
        FXGL.spawn("MapBorder", new SpawnData(0, -100).put("width", WIDTH).put("height", HEIGHT));
        FXGL.setLevelFromMap("mapTest2.tmx");

        GameWorld world = getGameWorld();
        List<Entity> allEntities = world.getEntities();
        for (Entity entity : allEntities) {
            moveEntityToIsometric(entity);
        }
        TurnService.startTurn(Player.PLAYER1);
    }

    public void initOnlineGameLogicAndUI() {
        FXGL.getGameWorld().addEntityFactory(new MapFactory());
        FXGL.getGameWorld().addEntityFactory(new PlayerFactory());
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
        List<Entity> allEntities = world.getEntities();
        for (Entity entity : allEntities) {
            if ((entity.isType(TILE) || entity.isType(MOUNTAIN) || entity.isType(TANK) || entity.isType(CITY)) && !entity.hasComponent(IDComponent.class)) {
                long id = IDFactory.generateId();
                entity.addComponent(new IDComponent(id));
            }
            moveEntityToIsometric(entity);
        }
        TurnService.startTurn(Player.PLAYER1);
    }

    private void moveEntityToIsometric(Entity entity) {
        Platform.runLater(() -> SoundService.pickSong());
        Point2D orthGridPos = MapService.orthScreenToGrid(entity.getPosition());
        Point2D isoScreenPos = MapService.isoGridToScreen(orthGridPos.getX(), orthGridPos.getY());
        if (entity.isType(TILE)) {
            entity.setPosition(isoScreenPos.getX() - 64, isoScreenPos.getY() + 1);
        } else if (entity.isType(HOVER)) {
            entity.setPosition(isoScreenPos.getX(), isoScreenPos.getY());
        } else if (entity.isType(MOUNTAIN) || entity.isType(TANK) || entity.isType(CITY))
            entity.setPosition(isoScreenPos.getX() - 64, isoScreenPos.getY() - 64);
    }

    public Entity getMyTank() {
        String myPlayer = getAssignedPlayer();
        return FXGL.getGameWorld().getEntitiesByType(TANK).stream()
                .filter(e -> e.getComponent(TankDataComponent.class).getOwner().toString().equals(myPlayer))
                .findFirst()
                .orElse(null);
    }

    public String getAssignedPlayer() {
        return assignedPlayer;
    }

    public void setAssignedPlayer(String assignedPlayer) {
        this.assignedPlayer = assignedPlayer;
    }

    public Gamemode getSelectedGamemode() {
        return selectedGamemode;
    }

    public Pane getEndTurnView() {
        return endTurnView;
    }

    public void setServerIP(String ip) {
        this.serverIP = ip;
    }

    public void setServerPort(int port) {
        this.serverPort = port;
    }
}

