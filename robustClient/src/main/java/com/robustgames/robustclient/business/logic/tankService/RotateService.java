package com.robustgames.robustclient.business.logic.tankService;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.texture.Texture;
import com.robustgames.robustclient.business.entitiy.components.TankDataComponent;
import com.robustgames.robustclient.business.logic.Direction;
import com.robustgames.robustclient.business.logic.Player;
import com.robustgames.robustclient.business.logic.gameService.MapService;
import javafx.geometry.Point2D;

import static java.lang.Math.abs;

public class RotateService {
    static String initialTankView;
    static Texture initialTankTexture;
    static Texture newTankTexture;




    public static String rotateTank(Entity selectedTank,  Direction direction) {
        TankDataComponent tankData = selectedTank.getComponent(TankDataComponent.class);
        String newTankView = "";

        initialTankTexture = selectedTank.getComponent(TankDataComponent.class).getInitialTankTexture();
        newTankTexture = selectedTank.getComponent(TankDataComponent.class).getNewTankTexture();
        initialTankView = newTankTexture.getImage().getUrl().substring(newTankTexture.getImage().getUrl().lastIndexOf("/") + 1);

        if (direction == Direction.LEFT){
            newTankView = newTankView + rotateTankLeft(initialTankView);
        }
        else if (direction == Direction.RIGHT){
            newTankView = newTankView + rotateTankRight(initialTankView);
        }
        else throw new IllegalArgumentException("Invalid direction in rotateTank of RotateService: " + direction);

        if (selectedTank.getViewComponent().getChildren().contains(initialTankTexture)) {
            newTankTexture = FXGL.getAssetLoader().loadTexture(newTankView);
            selectedTank.getViewComponent().addChild(newTankTexture);
            selectedTank.getViewComponent().removeChild(initialTankTexture);
        }
        else {
            // First remove the old newTankTexture if it exists
            if (selectedTank.getViewComponent().getChildren().contains(newTankTexture)) {
                selectedTank.getViewComponent().removeChild(newTankTexture);
            }

            Texture tankTexture = FXGL.getAssetLoader().loadTexture(newTankView);
            newTankTexture = tankTexture; // Direct assignment instead of set()
            selectedTank.getViewComponent().addChild(newTankTexture);
        }
        selectedTank.getComponent(TankDataComponent.class).setNewTankTexture(newTankTexture);
        return newTankView;

    }


    public static void rotateTurret(Point2D tileGridPos, Entity selectedTank){
        TankDataComponent tankData = selectedTank.getComponent(TankDataComponent.class);

        String newTankHullView;
        Texture newTankHullTexture = tankData.getHullTexture();

        String newTankTurretView = "";

        if (tankData.getOwner().equals(Player.PLAYER2))
            newTankTurretView = "green_";
        Texture newTankTurretTexture = tankData.getTurretTexture();

        newTankTexture = selectedTank.getComponent(TankDataComponent.class).getNewTankTexture();
        initialTankView = newTankTexture.getImage().getUrl().substring(newTankTexture.getImage().getUrl().lastIndexOf("/") + 1);

        Point2D tankGridPos = MapService.isoScreenToGrid(selectedTank.getCenter());
        Point2D diff = tileGridPos.subtract(tankGridPos);
        //ROBUST_DEBUG Part Two - Checking the differences of the coordinates in isometric space
       /* System.out.println("tankGridPos = " + tankGridPos + "");
        System.out.println("diff = " + diff);*/

        // Only instantiated once, afterwards only the turret rotates
        if(newTankHullTexture == null){
            newTankHullView = changeTankHull(initialTankView);
            newTankHullTexture = FXGL.getAssetLoader().loadTexture(newTankHullView);

            // Make sure to remove any existing textures before adding the new hull texture
            if (selectedTank.getViewComponent().getChildren().contains(initialTankTexture)) {
                selectedTank.getViewComponent().addChild(newTankHullTexture);
                selectedTank.getViewComponent().removeChild(initialTankTexture);
            }
            else if (selectedTank.getViewComponent().getChildren().contains(newTankTexture)) {
                selectedTank.getViewComponent().addChild(newTankHullTexture);
                selectedTank.getViewComponent().removeChild(newTankTexture);
            }
            tankData.setHullTexture(newTankHullTexture);
        }

        if (diff.getX() > 0 && diff.getY() == 0) {
            newTankTurretView = newTankTurretView + "tank_turret_down_right.png";
        } else if (diff.getX() > 0 && diff.getY() > 0 && diff.getX() == diff.getY()) {
            newTankTurretView = newTankTurretView + "tank_turret_down.png";
        }else if (diff.getX() == 0 && diff.getY() > 0) {
            newTankTurretView = newTankTurretView + "tank_turret_down_left.png";
        }else if (diff.getX() < 0 && diff.getY() > 0 && abs(diff.getX()) == diff.getY()) {
            newTankTurretView = newTankTurretView + "tank_turret_left.png";
        }else if (diff.getX() < 0 && diff.getY() == 0) {
            newTankTurretView = newTankTurretView + "tank_turret_top_left.png";
        }else if (diff.getX() < 0 && diff.getY() < 0 && diff.getX() == diff.getY()) {
            newTankTurretView = newTankTurretView + "tank_turret_top.png";
        }else if (diff.getX() == 0 && diff.getY() < 0) {
            newTankTurretView = newTankTurretView + "tank_turret_top_right.png";
        }else if (diff.getX() > 0 && diff.getY() < 0 && diff.getX() == abs(diff.getY())) {
            newTankTurretView = newTankTurretView + "tank_turret_right.png";
        }else{
            if (newTankTurretTexture != null)
                newTankTurretView = tankData.getTurretTextureName();
            else
                newTankTurretView = newTankTurretView + "tank_turret_top_left.png";
        }

        // Always remove the old turret texture if it exists
        if (newTankTurretTexture != null && selectedTank.getViewComponent().getChildren().contains(newTankTurretTexture)) {
            selectedTank.getViewComponent().removeChild(newTankTurretTexture);
        }

        if (newTankTurretView != null) {
            newTankTurretTexture = FXGL.getAssetLoader().loadTexture(newTankTurretView);
            selectedTank.getViewComponent().addChild(newTankTurretTexture);
            tankData.setTurretTexture(newTankTurretTexture);
            tankData.setTurretTextureName(newTankTurretView);
        }
    }
    private static String rotateTankLeft(String tankView) {
        String newTankView = tankView;
        switch (tankView) {
            case "tank_top_right.png" -> newTankView = "tank_top_left.png";
            case "tank_top_left.png" -> newTankView = "tank_down_left.png";
            case "tank_down_left.png" -> newTankView = "tank_down_right.png";
            case "tank_down_right.png" -> newTankView = "tank_top_right.png";
            case "green_tank_top_right.png" -> newTankView = "green_tank_top_left.png";
            case "green_tank_top_left.png" -> newTankView = "green_tank_down_left.png";
            case "green_tank_down_left.png" -> newTankView = "green_tank_down_right.png";
            case "green_tank_down_right.png" -> newTankView = "green_tank_top_right.png";
        }
        return newTankView;
    }
    private static String rotateTankRight(String tankView) {
        String newTankView = tankView;
        switch (tankView) {
            case "tank_top_right.png" -> newTankView = "tank_down_right.png";
            case "tank_top_left.png" -> newTankView = "tank_top_right.png";
            case "tank_down_left.png" -> newTankView = "tank_top_left.png";
            case "tank_down_right.png" -> newTankView = "tank_down_left.png";
            case "green_tank_top_right.png" -> newTankView = "green_tank_down_right.png";
            case "green_tank_top_left.png" -> newTankView = "green_tank_top_right.png";
            case "green_tank_down_left.png" -> newTankView = "green_tank_top_left.png";
            case "green_tank_down_right.png" -> newTankView = "green_tank_down_left.png";
        }
        return newTankView;
    }
    public static String changeTankHull(String tankView) {
        String newTankHullView = tankView;
        switch (tankView) {
            case "tank_top_right.png" -> newTankHullView = "tank_top_right_alt.png";
            case "tank_top_left.png" -> newTankHullView = "tank_top_left_alt.png";
            case "tank_down_left.png" -> newTankHullView = "tank_down_left_alt.png";
            case "tank_down_right.png" -> newTankHullView = "tank_down_right_alt.png";
            case "green_tank_top_right.png" -> newTankHullView = "green_tank_top_right_alt.png";
            case "green_tank_top_left.png" -> newTankHullView = "green_tank_top_left_alt.png";
            case "green_tank_down_left.png" -> newTankHullView = "green_tank_down_left_alt.png";
            case "green_tank_down_right.png" -> newTankHullView = "green_tank_down_right_alt.png";
        }
        return newTankHullView;
    }
}
/*
when "aiming" the tank (by adding ShootComponent via button press) I want to display not just the 4 directions of the turret, but the inbetween states as well (since I have the asssets). so, show tank_up.png inbetween tank_top_left.png and tank_top_right.png, how difficult is it to implement that?
 */
