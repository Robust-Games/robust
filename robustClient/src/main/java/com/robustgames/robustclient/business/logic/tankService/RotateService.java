package com.robustgames.robustclient.business.logic.tankService;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.texture.Texture;
import com.robustgames.robustclient.business.entitiy.components.TankDataComponent;
import com.robustgames.robustclient.business.logic.Direction;

public class RotateService {
    static String initialTankView;
    static Texture initialTankTexture;
    static String newTankView;
    static Texture newTankTexture;
    static Texture tankTexture;

    public static String rotateTank(Entity selectedTank, Direction direction) {
        initialTankTexture = selectedTank.getComponent(TankDataComponent.class).getInitialTankTexture();
        newTankTexture = selectedTank.getComponent(TankDataComponent.class).getNewTankTexture();
        initialTankView = newTankTexture.getImage().getUrl().substring(newTankTexture.getImage().getUrl().lastIndexOf("/") + 1);

        if (direction == Direction.LEFT) {
            newTankView = rotateTankLeft(initialTankView);
        } else if (direction == Direction.RIGHT) {
            newTankView = rotateTankRight(initialTankView);
        }

        if (selectedTank.getViewComponent().getChildren().contains(initialTankTexture)) {
            newTankTexture = FXGL.getAssetLoader().loadTexture(newTankView);
            selectedTank.getViewComponent().addChild(newTankTexture);
            selectedTank.getViewComponent().removeChild(initialTankTexture);
        } else {
            tankTexture = FXGL.getAssetLoader().loadTexture(newTankView);
            newTankTexture.set(tankTexture);
        }
        selectedTank.getComponent(TankDataComponent.class).setNewTankTexture(newTankTexture);
        return newTankView;

    }

    public static String rotateTankLeft(String tankView) {
        switch (tankView) {
            case "tank_top_right.png" -> newTankView = "tank_top_left.png";
            case "tank_top_left.png" -> newTankView = "tank_down_left.png";
            case "tank_down_left.png" -> newTankView = "tank_down_right.png";
            case "tank_down_right.png" -> newTankView = "tank_top_right.png";
        }
        return newTankView;
    }

    public static String rotateTankRight(String tankView) {
        switch (tankView) {
            case "tank_top_right.png" -> newTankView = "tank_down_right.png";
            case "tank_top_left.png" -> newTankView = "tank_top_right.png";
            case "tank_down_left.png" -> newTankView = "tank_top_left.png";
            case "tank_down_right.png" -> newTankView = "tank_down_left.png";
        }
        return newTankView;
    }
    //Todo: Hier dann noch die Rotate Turret sachen
}
