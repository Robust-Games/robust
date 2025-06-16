package com.robustgames.robustclient.business.logic;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.ui.ProgressBar;
import com.robustgames.robustclient.business.entitiy.EntityType;
import com.robustgames.robustclient.business.entitiy.components.CityDataComponent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.robustgames.robustclient.business.entitiy.EntityType.CITY;
import static com.robustgames.robustclient.business.entitiy.EntityType.TANK;

/**
 *  Manages turn order, win conditions, etc.
 */
//TODO turn order
public class GameState {
    static Player winner;

    public static void gameOver() {
        List<Entity> cityList = getGameWorld().getEntitiesByType(CITY);
        List<Entity> tankList = getGameWorld().getEntitiesByType(TANK);
        getGameTimer().runOnceAfter(() -> {
            if (cityList.isEmpty() || tankList.isEmpty() || (cityList.size() == 1 && tankList.size() == 1)) {
                getDialogService().showMessageBox("The Game Ends in a Draw", getGameController()::exit);
                return;
            }
            if (cityList.size() == 1) {
                winner = cityList.getFirst().getComponent(CityDataComponent.class).getOwner();
                getDialogService().showMessageBox(winner + " wins", getGameController()::exit);
                return;
            }
            if (tankList.size() == 1) {
                winner = tankList.getFirst().getComponent(CityDataComponent.class).getOwner();
                getDialogService().showMessageBox(winner + " wins", getGameController()::exit);
                return;
            }
            getDialogService().showMessageBox("Nobody wins", getGameController()::exit);

        }, Duration.millis(2000));

    }


    public static ProgressBar hpBarInit(int maxHP){
        var hpBar = new ProgressBar();
        hpBar.setWidth(90);
        hpBar.setHeight(15);
        hpBar.setTranslateY(20);
        hpBar.setTranslateX(19);
        hpBar.setMaxValue(maxHP);
        hpBar.setFill(Color.GREEN);
        //hpBar.setVisible(false);

        return hpBar;
    }
}
