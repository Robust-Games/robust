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

    //Key input
    @Override
    protected void initInput() {

/*
        getInput().addAction(new UserAction("Up") {
            @Override
            protected void onAction() {
            }
            @Override
            protected void onActionEnd() {
            }
        }, KeyCode.A);//button pressed
*/
        onBtnDown(MouseButton.PRIMARY, () -> {
            Point2D mouseWorldPos = FXGL.getInput().getMousePositionWorld();
            System.out.println(MapService.screenToGrid(mouseWorldPos));

        });

    }
    //HUD und UI
    @Override
    protected void initUI() {
        /*Label label = new Label("Hello, FXGL!");
        label.setFont(Font.font(20.0));
        FXGL.addUINode(label, 350.0, 290.0);*/
    }

    //Window settings
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("ROBUST");
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
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
        FXGL.setLevelFromMap("map2D.tmx");
    }

    public static void main(String[] args) {
        launch(args);
    }
}