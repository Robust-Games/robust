package com.robustgames.robustclient.business.logic.gameService;


import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.action.ActionComponent;
import com.robustgames.robustclient.application.RobustApplication;
import com.robustgames.robustclient.business.entitiy.components.APComponent;
import com.robustgames.robustclient.business.entitiy.components.TankDataComponent;
import com.robustgames.robustclient.business.logic.Gamemode;
import com.robustgames.robustclient.business.logic.Player;

import static com.almasb.fxgl.dsl.FXGL.getNotificationService;
import static com.robustgames.robustclient.business.entitiy.EntityType.TANK;

public class TurnService {
    public static Player currentPlayer;
    static boolean player1Ready = false;
    static boolean player2Ready = false;
    private static Gamemode currentGamemode = FXGL.<RobustApplication>getAppCast().getSelectedGamemode();

    public static void startTurn(Player player) {
        currentPlayer = player;
        if (currentGamemode.equals(Gamemode.LOCAL)){
            Entity playerTank = MapService.findTankOfPlayer(player);
            if (playerTank != null) {
                playerTank.getComponent(TankDataComponent.class).setInitialPos();
            }
        }
        else if (currentGamemode.equals(Gamemode.ONLINE)) {
            String myPlayerName = FXGL.<RobustApplication>getAppCast().getAssignedPlayer();

            if (myPlayerName != null && myPlayerName.equals(currentPlayer.toString())) {
                // Nur dieser Client darf jetzt aktiv sein
                Entity playerTank = MapService.findTankOfPlayer(player);
                if (playerTank != null) {
                    playerTank.getComponent(TankDataComponent.class).setInitialPos();
                }
            } else {
                System.out.println("Waiting for other player...");

            }
        }
    }

    public static void nextPlayer() {
        if (currentPlayer == Player.PLAYER1) {
            player1Ready = true;
            currentPlayer = Player.PLAYER2;
            //getNotificationService().pushNotification(currentPlayer + "'S TURN");
        } else {
            player2Ready = true;
            currentPlayer = Player.PLAYER1;
            if (currentGamemode.equals(Gamemode.LOCAL)){
                executeActions();
            }
        }
        startTurn(currentPlayer);

    }

    public static void executeActions() {
        // Execute all actions simultaneously
        if (player1Ready && player2Ready) {
            FXGL.getGameWorld().getEntitiesByType(TANK).forEach(tank -> {
                ActionComponent ac = tank.getComponent(ActionComponent.class);
                if (ac.isPaused()) {
                    ac.resume(); // Start executing queued actions
                }
            });
            onActionCompletion();
        }
    }

    public static void onActionCompletion() {
        // Check if all actions are complete
        boolean allDone = FXGL.getGameWorld().getEntitiesByType(TANK)
                .stream()
                .map(e -> e.getComponent(ActionComponent.class))
                .allMatch(ac -> !ac.isPaused());

        if (allDone) {
            reset();
        }
    }

    private static void reset() {
        player1Ready = false;
        player2Ready = false;

        FXGL.getGameWorld().getEntitiesByType(TANK).forEach(entity -> {
            entity.getComponent(APComponent.class).reset();
        });
       //getNotificationService().pushNotification(currentPlayer + "'S TURN");
    }
}

