package com.robustgames.robustclient.application;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.robustgames.robustclient.business.collision.ShellCityHandler;
import com.robustgames.robustclient.business.collision.ShellTankHandler;
import com.robustgames.robustclient.business.collision.ShellTileHandler;
import com.robustgames.robustclient.business.factories.MapFactory;
import com.robustgames.robustclient.business.factories.PlayerFactory;
import com.robustgames.robustclient.business.logic.MapService;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;

import static com.almasb.fxgl.dsl.FXGL.*;

public class RobustApplication extends GameApplication  {
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;

    //Window settings
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("ROBUST");
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
    }

    //Key input
    @Override
    protected void initInput() {
        //DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
        onBtnDown(MouseButton.PRIMARY, () -> {
            Point2D mouseWorldPos = FXGL.getInput().getMousePositionWorld();
            Point2D gridPos = MapService.orthScreenToGrid(mouseWorldPos);
            System.out.println("isoScreenToGrid = " + gridPos + "\nisoGridToScreen = "
                + MapService.orthGridToScreen(gridPos.getX(), gridPos.getY()));
        });
        //DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG

    }

    //HUD und UI
    @Override
    protected void initUI() {
        /*Label label = new Label("Hello, FXGL!");
        label.setFont(Font.font(20.0));
        FXGL.addUINode(label, 350.0, 290.0);*/
    }

    @Override
    protected void initPhysics() {
        var shellTank = new ShellTankHandler();
        getPhysicsWorld().addCollisionHandler(shellTank);
        //getPhysicsWorld().addCollisionHandler(shellTank.copyFor(SHELL, OTHERENTITYTYPE)); TODO Other Entity Types possible
        var shellCity = new ShellCityHandler();
        getPhysicsWorld().addCollisionHandler(shellCity);
        getPhysicsWorld().addCollisionHandler(new ShellTileHandler());
    }

    @Override
    protected void initGame() {
        Point2D mouseWorldPos = FXGL.getInput().getMousePositionWorld();
        Point2D gridPos = MapService.orthScreenToGrid(mouseWorldPos);
        FXGL.getGameWorld().addEntityFactory(new MapFactory());
        FXGL.getGameWorld().addEntityFactory(new PlayerFactory());
        FXGL.spawn("Background", new SpawnData(0, 0).put("width", WIDTH).put("height", HEIGHT));
        FXGL.setLevelFromMap("map2D.tmx");
    }

    public static void main(String[] args) {
        launch(args);
    }
}