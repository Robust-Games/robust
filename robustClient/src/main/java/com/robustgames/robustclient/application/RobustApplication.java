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
import com.robustgames.robustclient.business.factories.MapFactory;
import com.robustgames.robustclient.business.factories.PlayerFactory;
import com.robustgames.robustclient.business.logic.gameService.MapService;
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
        onBtnDown(MouseButton.SECONDARY, () -> {
            MapService.deSelectTank();
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

    public void onTankClicked(Entity tank) {
        //hp bar visible
        tankButtonView.setVisible(true);
        tankDataView.setVisible(true);
        tankDataView.setSelectedTank(tank);
    }

    @Override
    protected void initGame() {
        getGameScene().getViewport().setY(-100);
        // getGameScene().getViewport().setZoom(100);

        tankButtonView = new TankButtonView(); // Sofort bauen!
        tankDataView = new TankDataView();
        endTurnView = new EndTurnView();

        Client<Bundle> client = getNetService().newTCPClient("localhost", 55555);
        client.setOnConnected(conn -> {
            connection = conn; // Merke die Connection für spätere Sends
        });
        client.connectAsync();

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
    }

    // Connection Getter
    public Connection<Bundle> getConnection() {
        return this.connection;
    }

    public static void main(String[] args) {
        launch(args);
    }
}