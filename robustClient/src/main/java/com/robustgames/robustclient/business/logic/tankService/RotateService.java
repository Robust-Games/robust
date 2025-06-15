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

    public static Texture rotateTankLeft(Entity selectedTank) {
        initialTankTexture = selectedTank.getComponent(TankDataComponent.class).getInitialTankTexture();
        initialTankView = initialTankTexture.getImage().getUrl().substring(initialTankTexture.getImage().getUrl().lastIndexOf("/") + 1);

        switch (initialTankView) {
            case "tank_top_right.png" -> {
                newTankView = "tank_top_left.png";
                newTankTexture = FXGL.getAssetLoader().loadTexture("tank_top_left.png");
            }
            case "tank_top_left.png" -> {
                newTankView = "tank_down_left.png";
                newTankTexture = FXGL.getAssetLoader().loadTexture("tank_down_left.png");
            }
            case "tank_down_left.png" -> {
                newTankView = "tank_down_right.png";
                newTankTexture = FXGL.getAssetLoader().loadTexture("tank_down_right.png");
            }
            case "tank_down_right.png" -> {
                newTankView = "tank_top_right.png";
                newTankTexture = FXGL.getAssetLoader().loadTexture("tank_top_right.png");
            }
        }
        selectedTank.getComponent(TankDataComponent.class).setNewTankTexture(newTankTexture);
        selectedTank.getViewComponent().addChild(newTankTexture);
        selectedTank.getViewComponent().removeChild(initialTankTexture);
        return newTankTexture;

    }

    public static Texture rotateTankRight(Entity selectedTank) {
        initialTankTexture = selectedTank.getComponent(TankDataComponent.class).getInitialTankTexture();
        initialTankView = initialTankTexture.getImage().getUrl().substring(initialTankTexture.getImage().getUrl().lastIndexOf("/") + 1);

        switch (initialTankView) {
            case "tank_top_right.png" -> {
                newTankView = "tank_down_right.png";
                newTankTexture = FXGL.getAssetLoader().loadTexture("tank_down_right.png");
            }
            case "tank_top_left.png" -> {
                newTankView = "tank_top_right.png";
                newTankTexture = FXGL.getAssetLoader().loadTexture("tank_top_right.png");
            }
            case "tank_down_left.png" -> {
                newTankView = "tank_top_left.png";
                newTankTexture = FXGL.getAssetLoader().loadTexture("tank_top_left.png");
            }
            case "tank_down_right.png" -> {
                newTankView = "tank_down_left.png";
                newTankTexture = FXGL.getAssetLoader().loadTexture("tank_down_left.png");
            }
        }
        selectedTank.getComponent(TankDataComponent.class).setNewTankTexture(newTankTexture);
        selectedTank.getViewComponent().addChild(newTankTexture);
        selectedTank.getViewComponent().removeChild(initialTankTexture);

        return newTankTexture;
    }
    //Todo: Hier dann noch die Rotate Turret sachen

}
