package com.robustgames.robustclient.business.logic.gameService;


import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.action.Action;
import com.almasb.fxgl.entity.action.ActionComponent;
import com.almasb.fxgl.net.Connection;
import com.robustgames.robustclient.business.entitiy.components.APComponent;
import com.robustgames.robustclient.business.entitiy.components.TankDataComponent;
import com.robustgames.robustclient.business.factories.BundleFactory;
import com.robustgames.robustclient.business.logic.Player;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.getNotificationService;
import static com.robustgames.robustclient.business.entitiy.EntityType.TANK;

public class TurnService {
    public static Player currentPlayer;
    static boolean player1Ready = false;
    static boolean player2Ready = false;

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
        if (player1Ready && player2Ready) {
            FXGL.getGameWorld().getEntitiesByType(TANK).forEach(tank -> {
                ActionComponent ac = tank.getComponent(ActionComponent.class);
                Player owner = tank.getComponent(TankDataComponent.class).getOwner();

                ObservableList<Action> queue = ac.actionsProperty();
                var data = new Bundle("Action Queue");

                List<Action> serializableQueue = new ArrayList<>(queue);

                data.put("actionque", (Serializable) serializableQueue);

                if (queue.isEmpty()) {
                    System.out.println("  (empty)");
                } else {
                    System.out.println("Owner " + owner);
                    System.out.println("QueQue " + queue);
                    System.out.println("Aktion " + queue.toArray());
                }


                if (ac.isPaused()) {
                    ac.resume();
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
        getNotificationService().pushNotification(currentPlayer + "'S TURN" );
    }

    private static Connection<HashMap<String, Serializable>> connection;

    public static void setConnection(Connection<Bundle> conn) {
    }


}

