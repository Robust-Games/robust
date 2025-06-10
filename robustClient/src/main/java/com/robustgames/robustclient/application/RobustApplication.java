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
import com.robustgames.robustclient.business.collision.ShellCityHandler;
import com.robustgames.robustclient.business.collision.ShellTankHandler;
import com.robustgames.robustclient.business.collision.ShellTileHandler;
import com.robustgames.robustclient.business.factories.MapFactory;
import com.robustgames.robustclient.business.factories.PlayerFactory;
import com.robustgames.robustclient.business.logic.MapService;
import com.robustgames.robustclient.presentation.scenes.SelectionView;
import javafx.geometry.Point2D;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.robustgames.robustclient.business.entitiy.EntityType.TILE;

public class RobustApplication extends GameApplication {
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    SelectionView selectionView;

    private Client<Bundle> client;
    private Connection<Bundle> connection;

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

        //Click Debug
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
        selectionView.setVisible(false);

        addUINode(selectionView);
    }

    public void onTankClicked(Entity tank) {
        selectionView.setVisible(true);
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
        FXGL.setLevelFromMap("mapTest.tmx"); //map2D.tmx für 2D und mapTest.tmx für Isometrisch

        GameWorld world = getGameWorld();
        List<Entity> allEntities = world.getEntities().subList(3, world.getEntities().size());
        for (Entity entity : allEntities) {
            Point2D orthGridPos = MapService.orthScreenToGrid(entity.getPosition());
            Point2D isoGridPos = MapService.isoGridToScreen(orthGridPos.getX(), orthGridPos.getY());
            if (entity.isType(TILE))
                entity.setPosition(isoGridPos.getX(), isoGridPos.getY());
            else
                entity.setPosition(isoGridPos.getX() - 64, isoGridPos.getY() - 64);
        }

        client = getNetService().newTCPClient("localhost", 55555);
        client.setOnConnected(conn -> {
            connection = conn; // Merke die Connection für spätere Sends
            selectionView = new SelectionView(connection);
            addUINode(selectionView);
        });

        client.connectAsync();

        selectionView = new SelectionView(connection);
    }

    public static void main(String[] args) {
        launch(args);
    }
}