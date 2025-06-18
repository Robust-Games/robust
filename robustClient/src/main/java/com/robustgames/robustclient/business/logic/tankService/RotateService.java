package com.robustgames.robustclient.business.logic.tankService;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.texture.Texture;
import com.robustgames.robustclient.business.entitiy.components.TankDataComponent;
import com.robustgames.robustclient.business.logic.Direction;
import com.robustgames.robustclient.business.logic.MapService;
import javafx.geometry.Point2D;

public class RotateService {
    static String initialTankView;
    static Texture initialTankTexture;
    static String newTankView;
    static Texture newTankTexture;
    static Texture tankTexture;

    public static String rotateTank(Entity selectedTank,  Direction direction) {
        initialTankTexture = selectedTank.getComponent(TankDataComponent.class).getInitialTankTexture();
        newTankTexture = selectedTank.getComponent(TankDataComponent.class).getNewTankTexture();
        initialTankView = newTankTexture.getImage().getUrl().substring(newTankTexture.getImage().getUrl().lastIndexOf("/") + 1);

        if (direction == Direction.LEFT){
            newTankView = rotateTankLeft(initialTankView);
        }
        else if (direction == Direction.RIGHT){
            newTankView = rotateTankRight(initialTankView);
        }

        if (selectedTank.getViewComponent().getChildren().contains(initialTankTexture)) {
            newTankTexture = FXGL.getAssetLoader().loadTexture(newTankView);
            selectedTank.getViewComponent().addChild(newTankTexture);
            selectedTank.getViewComponent().removeChild(initialTankTexture);
        }
        else {
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

    public static void rotateTurret(Point2D tileGridPos, Entity selectedTank){

        initialTankTexture = selectedTank.getComponent(TankDataComponent.class).getInitialTankTexture(); // speichert aktuelle Textur
        newTankTexture = selectedTank.getComponent(TankDataComponent.class).getNewTankTexture(); // // speichert neue Textur
        initialTankView = newTankTexture.getImage().getUrl().substring(newTankTexture.getImage().getUrl().lastIndexOf("/") + 1); // Dateiname aktueller Textur

        var tankGridPos = MapService.isoScreenToGrid(selectedTank.getCenter());
        var diff = tileGridPos.subtract(tankGridPos); // Achsenbestimmung

        var tankData = selectedTank.getComponent(TankDataComponent.class);

        var tankTurret = tankData.getTurretTexture();

        // Grundbauteil um nurnoch Turret zu drehen
        if(tankTurret == null){
            switch (initialTankView) {
                case "tank_top_left.png" -> {
                    selectedTank.getViewComponent().removeChild(initialTankTexture);
                    newTankView = "tank_top_left_alt.png";
                    newTankTexture = FXGL.getAssetLoader().loadTexture(newTankView);
                    selectedTank.getViewComponent().addChild(newTankTexture);
                }

                case "tank_top_right.png" -> {
                    selectedTank.getViewComponent().removeChild(initialTankTexture);
                    newTankView = "tank_top_right_alt.png";
                    newTankTexture = FXGL.getAssetLoader().loadTexture(newTankView);
                    selectedTank.getViewComponent().addChild(newTankTexture);
                }

                case "tank_down_left.png" -> {
                    selectedTank.getViewComponent().removeChild(initialTankTexture);
                    newTankView = "tank_down_left_alt.png";
                    newTankTexture = FXGL.getAssetLoader().loadTexture(newTankView);
                    selectedTank.getViewComponent().addChild(newTankTexture);
                }

                case "tank_down_right.png" -> {
                    selectedTank.getViewComponent().removeChild(initialTankTexture);
                    newTankView = "tank_down_right_alt.png";
                    newTankTexture = FXGL.getAssetLoader().loadTexture(newTankView);
                    selectedTank.getViewComponent().addChild(newTankTexture);
                }
            }

        }

        String turretTextureName = null;

        if (diff.getX() > 0 && diff.getY() == 0) {
            turretTextureName = "tank_turret_down_right.png";
        } else if (diff.getX() < 0 && diff.getY() == 0) {
            turretTextureName = "tank_turret_top_left.png";
        } else if (diff.getY() > 0 && diff.getX() == 0) {
            turretTextureName = "tank_turret_down_left.png";
        } else if (diff.getY() < 0 && diff.getX() == 0) {
            turretTextureName = "tank_turret_top_right.png";
        }

        if (tankTurret != null) {
            selectedTank.getViewComponent().removeChild(tankTurret);

        }

        if (turretTextureName != null) {
            if (tankTurret != null) {
                selectedTank.getViewComponent().removeChild(tankTurret);
            }

            tankTurret = FXGL.getAssetLoader().loadTexture(turretTextureName);
            selectedTank.getViewComponent().addChild(tankTurret);
            tankData.setTurretTexture(tankTurret);
        }
    }
}
