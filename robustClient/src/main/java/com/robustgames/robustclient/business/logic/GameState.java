package com.robustgames.robustclient.business.logic;

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
}
