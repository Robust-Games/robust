package com.robustgames.robustclient.business.logic.gameService;


import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.action.ActionComponent;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.robustgames.robustclient.business.entitiy.components.APComponent;
import com.robustgames.robustclient.business.entitiy.components.TankDataComponent;
import com.robustgames.robustclient.business.logic.Player;
import javafx.geometry.Point2D;

import java.sql.SQLOutput;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.getNotificationService;
import static com.robustgames.robustclient.business.entitiy.EntityType.TANK;
import static com.robustgames.robustclient.business.factories.PowerupFactory.spawnRandomHpPowerup;
import static com.robustgames.robustclient.business.logic.PowerUp.HEALTH;

public class TurnService {
    public static Player currentPlayer;
    static boolean player1Ready = false;
    static boolean player2Ready = false;
    private static int currentRound = 1;
    // public static boolean isExecuting = false; // 0

    public static void startTurn(Player player) {
        currentPlayer = player;
        Entity playerTank = MapService.findTankOfPlayer(player);
        if (playerTank != null) {
            playerTank.getComponent(TankDataComponent.class).setInitialPos();
        }
    }

    public static void nextPlayer() {
        if (currentPlayer == Player.PLAYER1) {
            player1Ready = true;
            currentPlayer = Player.PLAYER2;
            getNotificationService().pushNotification(currentPlayer + "'S TURN" );
        }
        else {
            player2Ready = true;
            currentPlayer = Player.PLAYER1;
            executeActions();
        }
        startTurn(currentPlayer);

    }

    public static void executeActions() {
        // Execute all actions simultaneously
        if (player1Ready && player2Ready) {
            FXGL.getGameWorld().getEntitiesByType(TANK).forEach(tank -> {
                // isExecuting = true; // 0

//                FXGL.getGameWorld().getEntitiesByType(HEALTH).forEach(p -> { // 1
//                    p.getComponent(CollidableComponent.class).setValue(true);
//                });

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
            FXGL.runOnce(TurnService::checkPowerupPickup, javafx.util.Duration.millis(2500));
        }
    }

    private static void reset() {
    //    isExecuting = false; // 0

//        FXGL.getGameWorld().getEntitiesByType(HEALTH).forEach(p -> { // 1
//            p.getComponent(CollidableComponent.class).setValue(false);
//        });

        player1Ready = false;
        player2Ready = false;
        currentRound += 1;
        spawnRandomHpPowerup();

        FXGL.getGameWorld().getEntitiesByType(TANK).forEach(entity -> {
                entity.getComponent(APComponent.class).reset();
        });
        getNotificationService().pushNotification(currentPlayer + "'S TURN" );
    }

    private static void checkPowerupPickup() { // nimmt nicht die aktuellste Panzer Position, sondern vor dem Spielzug
        List<Entity> tanks = FXGL.getGameWorld().getEntitiesByType(TANK);
        List<Entity> powerups = FXGL.getGameWorld().getEntitiesByType(HEALTH);

        for (Entity tank : tanks) {
            Point2D tankPos = MapService.isoScreenToGrid(tank.getCenter());

            for (Entity powerup : powerups) {
                Point2D centerPos = powerup.getPosition().add(64, 64); // Mitte berechnen
                Point2D powerupGrid = MapService.isoScreenToGrid(centerPos);
                System.out.println("POWERUP: " + powerupGrid);;
                System.out.println("TANKPOS: " + tankPos);

                if (tankPos.equals(powerupGrid)) {
                    tank.getComponent(HealthIntComponent.class).restore(1);
                    powerup.removeFromWorld();
                    break;
                }
            }
        }
    }

}

