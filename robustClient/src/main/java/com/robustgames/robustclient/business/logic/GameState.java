package com.robustgames.robustclient.business.logic;

import com.almasb.fxgl.ui.ProgressBar;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.getDialogService;
import static com.almasb.fxgl.dsl.FXGL.getGameController;

/**
 *  Manages turn order, win conditions, etc.
 */
//TODO turn order
public class GameState {

    private static void gameOver(boolean cityDestroyed, boolean tankDestroyed) {
        if (cityDestroyed || tankDestroyed) {
            getDialogService().showMessageBox("Player " + "[GETPLAYERID]" + " wins", getGameController()::exit);

        }
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
