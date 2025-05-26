package com.robustgames.robustclient.application;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.Input;
import com.robustgames.robustclient.business.collision.ShellCityHandler;
import com.robustgames.robustclient.business.collision.ShellTankHandler;
import com.robustgames.robustclient.business.collision.ShellTileHandler;
import com.robustgames.robustclient.business.entitiy.components.RotateComponent;
import com.robustgames.robustclient.business.factories.MapFactory;
import com.robustgames.robustclient.business.factories.PlayerFactory;
import com.robustgames.robustclient.business.logic.MapService;
import com.robustgames.robustclient.presentation.scenes.SelectionView;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.KeyCode; // Für Tastenangabe

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.robustgames.robustclient.business.entitiy.EntityType.MOUNTAIN;
import static com.robustgames.robustclient.business.entitiy.EntityType.TILE;

public class RobustApplication extends GameApplication  {
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    SelectionView selectionView;
    //Window settings
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("ROBUST");
        settings.setVersion("0.3");
        settings.getCSSList().add("style.css");
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
    }

    //Key input
    @Override
    protected void initInput() {
        Input input = getInput();
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

        onKeyDown(KeyCode.W, () -> {
            var tank = MapService.findSelectedTank();
            if (tank != null)
                tank.getComponent(RotateComponent.class).rotateUp();
        });
        onKeyDown(KeyCode.S, () -> {
            var tank = MapService.findSelectedTank();
            if (tank != null)
                tank.getComponent(RotateComponent.class).rotateDown();
        });
        onKeyDown(KeyCode.A, () -> {
            var tank = MapService.findSelectedTank();
            if (tank != null)
                tank.getComponent(RotateComponent.class).rotateLeft();
        });
        onKeyDown(KeyCode.D, () -> {
            var tank = MapService.findSelectedTank();
            if (tank != null)
                tank.getComponent(RotateComponent.class).rotateRight();
        });


    }

    //HUD und UI
    @Override
    protected void initUI() {
        selectionView.setVisible(false);

        addUINode(selectionView);
        /*Label label = new Label("Hello, FXGL!");
        label.setFont(Font.font(20.0));
        FXGL.addUINode(label, 350.0, 290.0);*/
    }
    public void onTankClicked(Entity tank) {


    //    selectionView.setCell(tank);
        selectionView.setVisible(true);

        //var x = tank.getX() > getAppWidth() / 2.0 ? tank.getX() - 250 : tank.getX();

//        towerSelectionBox.setTranslateX(x);
//        towerSelectionBox.setTranslateY(tank.getY());
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
        selectionView = new SelectionView();
        FXGL.getGameWorld().addEntityFactory(new MapFactory());
        FXGL.getGameWorld().addEntityFactory(new PlayerFactory());
        FXGL.spawn("Background", new SpawnData(0, 0).put("width", WIDTH).put("height", HEIGHT));
        FXGL.setLevelFromMap("mapTest.tmx"); //map2D.tmx für 2D und mapTest.tmx für Isometrisch

        GameWorld world = getGameWorld();
        //ISOMETRISCHE MAP TRIGGER ISOMETRISCHE MAP TRIGGER ISOMETRISCHE MAP TRIGGER ISOMETRISCHE MAP TRIGGER
        List<Entity> allEntities = world.getEntities().subList(3, world.getEntities().size());
        System.out.println(allEntities);
        for (Entity entity : allEntities) {
            Point2D orthGridPos = MapService.orthScreenToGrid(entity.getPosition());
            Point2D isoGridPos = MapService.isoGridToScreen(orthGridPos.getX(), orthGridPos.getY());
            if (entity.isType(TILE))
                entity.setPosition(isoGridPos.getX(), isoGridPos.getY());
            else
                entity.setPosition(isoGridPos.getX()-64, isoGridPos.getY()-64);
        }
        //ISOMETRISCHE MAP TRIGGER ISOMETRISCHE MAP TRIGGER ISOMETRISCHE MAP TRIGGER ISOMETRISCHE MAP TRIGGER
    }

    public static void main(String[] args) {
        launch(args);
    }
}