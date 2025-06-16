package com.robustgames.robustclient.business.logic.tankService;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.action.ActionComponent;
import com.almasb.fxgl.texture.Texture;
import com.robustgames.robustclient.business.actions.RotateAction;
import com.robustgames.robustclient.business.entitiy.components.TankDataComponent;
import com.robustgames.robustclient.business.logic.Direction;

public class RotateService {
    static String initialTankView;
    static Texture initialTankTexture;
    static String newTankView;
    static Texture newTankTexture;
    static Texture tankTexture;

    public static String rotateTankLeft(Entity selectedTank) {
        initialTankTexture = selectedTank.getComponent(TankDataComponent.class).getInitialTankTexture();
        newTankTexture = selectedTank.getComponent(TankDataComponent.class).getNewTankTexture();
        initialTankView = newTankTexture.getImage().getUrl().substring(newTankTexture.getImage().getUrl().lastIndexOf("/") + 1);

        switch (initialTankView) {
            case "tank_top_right.png" -> {
                newTankView = "tank_top_left.png";
                tankTexture = FXGL.getAssetLoader().loadTexture("tank_top_left.png");
            }
            case "tank_top_left.png" -> {
                newTankView = "tank_down_left.png";
                tankTexture = FXGL.getAssetLoader().loadTexture("tank_down_left.png");
            }
            case "tank_down_left.png" -> {
                newTankView = "tank_down_right.png";
                tankTexture = FXGL.getAssetLoader().loadTexture("tank_down_right.png");
            }
            case "tank_down_right.png" -> {
                newTankView = "tank_top_right.png";
                tankTexture = FXGL.getAssetLoader().loadTexture("tank_top_right.png");
            }
        }


        if (selectedTank.getViewComponent().getChildren().contains(initialTankTexture)) {
            newTankTexture = FXGL.getAssetLoader().loadTexture(newTankView);
            selectedTank.getViewComponent().addChild(newTankTexture);
            selectedTank.getViewComponent().removeChild(initialTankTexture);
        }
        else {
            newTankTexture.set(tankTexture);
        }
        selectedTank.getComponent(TankDataComponent.class).setNewTankTexture(newTankTexture);
        return newTankView;

    }

    public static String rotateTankRight(Entity selectedTank) {
        initialTankTexture = selectedTank.getComponent(TankDataComponent.class).getInitialTankTexture();
        newTankTexture = selectedTank.getComponent(TankDataComponent.class).getNewTankTexture();
        initialTankView = newTankTexture.getImage().getUrl().substring(newTankTexture.getImage().getUrl().lastIndexOf("/") + 1);

        switch (initialTankView) {
            case "tank_top_right.png" -> {
                newTankView = "tank_down_right.png";
                tankTexture = FXGL.getAssetLoader().loadTexture("tank_down_right.png");
            }
            case "tank_top_left.png" -> {
                newTankView = "tank_top_right.png";
                tankTexture = FXGL.getAssetLoader().loadTexture("tank_top_right.png");
            }
            case "tank_down_left.png" -> {
                newTankView = "tank_top_left.png";
                tankTexture = FXGL.getAssetLoader().loadTexture("tank_top_left.png");
            }
            case "tank_down_right.png" -> {
                newTankView = "tank_down_left.png";
                tankTexture = FXGL.getAssetLoader().loadTexture("tank_down_left.png");
            }
        }

        if (selectedTank.getViewComponent().getChildren().contains(initialTankTexture)) {
            newTankTexture = FXGL.getAssetLoader().loadTexture(newTankView);
            selectedTank.getViewComponent().addChild(newTankTexture);
            selectedTank.getViewComponent().removeChild(initialTankTexture);
        }
        else {
            newTankTexture.set(tankTexture);
        }
        selectedTank.getComponent(TankDataComponent.class).setNewTankTexture(newTankTexture);
        return newTankView;
    }
    //Todo: Hier dann noch die Rotate Turret sachen

}
