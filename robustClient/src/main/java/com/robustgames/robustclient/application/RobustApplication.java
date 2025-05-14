package com.robustgames.robustclient.application;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.SpawnData;
import com.robustgames.robustclient.business.collision.ShellCityHandler;
import com.robustgames.robustclient.business.collision.ShellTankHandler;
import com.robustgames.robustclient.business.collision.ShellTileHandler;
import com.robustgames.robustclient.business.factories.MapFactory;
import com.robustgames.robustclient.business.factories.PlayerFactory;
import com.robustgames.robustclient.business.logic.MapService;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;

import java.util.List;

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
        String whatWorld = "isometric"; //orthogonal or isometric

        if (whatWorld.equals("orthogonal")) {
            onBtnDown(MouseButton.PRIMARY, () -> {
                Point2D mouseWorldPos = FXGL.getInput().getMousePositionWorld();
                Point2D gridPos = MapService.orthScreenToGrid(mouseWorldPos);
                System.out.println("\nFXGL Mouse coordinates = " + mouseWorldPos
                        + "\northogonal Screen To Grid = " + gridPos
                        + "\northogonal Grid To Screen = " + MapService.orthGridToScreen(gridPos.getX(), gridPos.getY()));
            });
        }
        else if (whatWorld.equals("isometric")) {
            onBtnDown(MouseButton.PRIMARY, () -> {
                Point2D mouseWorldPos = FXGL.getInput().getMousePositionWorld();
                Point2D gridPos = MapService.isoScreenToGrid(mouseWorldPos);
                System.out.println("\nFXGL Mouse coordinates = " + mouseWorldPos
                        + "\nisometric Screen To Grid = " + gridPos
                        + "\nisometric Grid To Screen = " + MapService.isoGridToScreen(gridPos.getX(), gridPos.getY())
                        + "\northogonal Screen To Grid = " + MapService.orthScreenToGrid(mouseWorldPos)
                        + "\northogonal Grid To Screen = " + MapService.orthGridToScreen(MapService.orthScreenToGrid(mouseWorldPos).getX(), MapService.orthScreenToGrid(mouseWorldPos).getY()));
            });
        }
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
        FXGL.getGameWorld().addEntityFactory(new MapFactory());
        FXGL.getGameWorld().addEntityFactory(new PlayerFactory());
        FXGL.spawn("Background", new SpawnData(0, 0).put("width", WIDTH).put("height", HEIGHT));
        FXGL.setLevelFromMap("map2D.tmx"); //map2D.tmx für 2D und mapTest.tmx für Isometrisch

        GameWorld world = getGameWorld();
        //ISOMETRISCHE MAP TRIGGER ISOMETRISCHE MAP TRIGGER ISOMETRISCHE MAP TRIGGER ISOMETRISCHE MAP TRIGGER
//        List<Entity> allEntities = world.getEntities().subList(2, world.getEntities().size());
//        System.out.println(allEntities);
//        for (Entity e : allEntities) {
//            Point2D orthGridPos = MapService.orthScreenToGrid(e.getPosition());
//            Point2D isoGridPos = MapService.isoGridToScreen(orthGridPos.getX(), orthGridPos.getY());
//            e.setPosition(isoGridPos.getX()-32, isoGridPos.getY());
//        } blablabla BLA
        //ISOMETRISCHE MAP TRIGGER ISOMETRISCHE MAP TRIGGER ISOMETRISCHE MAP TRIGGER ISOMETRISCHE MAP TRIGGER
    }

    public static void main(String[] args) {
        launch(args);
    }
}