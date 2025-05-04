package com.robustgames.robustclient.application;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.robustgames.robustclient.business.GameObjectFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;



public class RobustApplication extends GameApplication  {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;

    //Key input
    @Override
    protected void initInput() {}

    //HUD und UI
    @Override
    protected void initUI() {
        Label label = new Label("Hello, FXGL!");
        label.setFont(Font.font(20.0));
        FXGL.addUINode(label, 350.0, 290.0);
    }

    //Window settings
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("ROBUST");
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
    }

    @Override
    protected void initGame() {
        FXGL.getGameWorld().addEntityFactory(new GameObjectFactory());
        FXGL.spawn("Background", new SpawnData(0, 0).put("width", WIDTH).put("height", HEIGHT));
        //FXGL.setLevelFromMap("map1.tmx");
    }

    public static void main(String[] args) {
        launch(args);
    }
}