/**
 * @author Burak Altun, Carolin Scheffler, Ersin Yesiltas, Nico Steiner
 */
package com.robustgames.robustclient.business.logic.networkService;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.action.ActionComponent;
import com.robustgames.robustclient.application.RobustApplication;
import com.robustgames.robustclient.business.actions.MovementAction;
import com.robustgames.robustclient.business.actions.RotateAction;
import com.robustgames.robustclient.business.actions.ShootAction;
import com.robustgames.robustclient.business.entitiy.components.APComponent;
import com.robustgames.robustclient.business.entitiy.components.IDComponent;
import com.robustgames.robustclient.business.entitiy.components.TankDataComponent;
import com.robustgames.robustclient.business.logic.gameService.MapService;
import javafx.application.Platform;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGL.getDialogService;
import static com.almasb.fxgl.dsl.FXGL.getGameController;
import static com.robustgames.robustclient.business.entitiy.EntityType.TANK;

public class ConnectionMessageHandler {
    RobustApplication app;

    ConnectionMessageHandler() {
        this.app = FXGL.getAppCast();
    }

    public void handleMessage(Bundle bundle) {
        switch (bundle.getName()) {
            case "GameStart" -> handleGameStart(bundle);
            case "ServerACK" -> handleServerAck(bundle);
            case "Reject" -> handleReject(bundle);
            case "MoveAction" -> handleMoveAction(bundle);
            case "RotateAction" -> handleRotateAction(bundle);
            case "ShootAction" -> handleShootAction(bundle);
            case "ExecuteTurn" -> handleExecuteTurn(bundle);
            case "assign_id" -> handleAssignId(bundle);
            case "hello" -> handleHello(bundle);
            case "OpponentLeft" -> handleOpponentLeft(bundle);
            default -> System.out.println("Unhandled bundle: " + bundle.getName());
        }
    }


    private void handleGameStart(Bundle bundle) {
        String assignedPlayer = bundle.get("assignedPlayer");
        app.setAssignedPlayer(assignedPlayer);
        System.out.println("Assigned role: " + assignedPlayer);
        app.hideWaitingForOpponent();
        app.initOnlineGameLogicAndUI();
    }

    private void handleServerAck(Bundle bundle) {
/*        if (waitingBox != null && waitingBox.getChildren().contains(waitingText)) {
            Platform.runLater(()->{
                Text newWaitingText = (Text) waitingBox.getChildren().getFirst();
                newWaitingText.setText("Waiting for other player");
                waitingBox.setTranslateX(getAppWidth() / 2.0 - waitingText.getLayoutBounds().getWidth() / 2.0);
            });
        }*/
        System.out.println("ACK received: " + bundle.get("originalBundle"));

    }

    private void handleReject(Bundle bundle) {
        System.out.println("Rejected: " + bundle.get("message"));
        getGameController().exit();
    }

    private void handleMoveAction(Bundle bundle) {
        System.out.println("MoveAction received: " + bundle);

        long entityId = bundle.get("entityId");
        double toX = bundle.get("toX");
        double toY = bundle.get("toY");

        Entity tank = FXGL.getGameWorld().getEntitiesByComponent(IDComponent.class).stream().filter(e -> e.getComponent(IDComponent.class).getId() == entityId).findFirst().orElse(null);

        if (tank != null) {
            Point2D screenTarget = MapService.isoGridToScreen(toX, toY).subtract(64, 64);
            Entity dummyTarget = FXGL.entityBuilder().at(screenTarget).build();

            MovementAction moveAction = new MovementAction(dummyTarget, false);
            tank.getComponent(ActionComponent.class).addAction(moveAction);
            tank.getComponent(ActionComponent.class).pause();
        }
    }

    private void handleRotateAction(Bundle bundle) {
        System.out.println("Received RotateAction: " + bundle);

        long entityId = bundle.get("entityId");
        String textureName = bundle.get("direction") + ".png";

        Entity tank = FXGL.getGameWorld().getEntitiesByComponent(IDComponent.class).stream().filter(e -> e.getComponent(IDComponent.class).getId() == entityId).findFirst().orElse(null);

        if (tank == null) {
            System.err.println("Tank with ID " + entityId + " not found.");
            return;
        }

        RotateAction rotateAction = new RotateAction(textureName, false);
        ActionComponent ac = tank.getComponent(ActionComponent.class);
        ac.addAction(rotateAction);
        ac.pause();
    }

    private void handleShootAction(Bundle bundle) {
        long shooterId = bundle.get("shooterId");
        long targetId = bundle.get("targetId");

        Entity shooter = FXGL.getGameWorld().getEntitiesByComponent(IDComponent.class).stream().filter(e -> e.getComponent(IDComponent.class).getId() == shooterId).findFirst().orElse(null);

        Entity target = FXGL.getGameWorld().getEntitiesByComponent(IDComponent.class).stream().filter(e -> e.getComponent(IDComponent.class).getId() == targetId).findFirst().orElse(null);

        if (shooter != null && target != null) {
            ShootAction shootAction = new ShootAction(target, false);
            shooter.getComponent(ActionComponent.class).addAction(shootAction);
            shooter.getComponent(ActionComponent.class).pause();
        } else {
            System.err.println("Shooter or Target not found");
        }
    }

    private void handleExecuteTurn(Bundle bundle) {
        System.out.println("ExecuteTurn received - starting turn actions");
        //Entity myTank = FXGL.<RobustApplication>getAppCast().getMyTank();
        //myTank.getComponent(TankDataComponent.class).resetBeforeTurn();
        FXGL.getGameWorld().getEntitiesByType(TANK).forEach(tank -> {
            ActionComponent ac = tank.getComponent(ActionComponent.class);
            if (ac.isPaused()) {
                ac.resume();
            }
            Platform.runLater(() -> app.getEndTurnView().disableProperty().setValue(false));
            tank.getComponent(APComponent.class).reset();
        });
    }

    private void handleAssignId(Bundle bundle) {
        int id = bundle.get("clientId");
        app.setClientId(id);
        System.out.println("received Client-ID: " + id);
    }

    private void handleHello(Bundle bundle) {
        System.out.println("received Hello from Server");
    }

    private void handleOpponentLeft(Bundle bundle) {
        Platform.runLater(() -> {
            getDialogService().showMessageBox("Opponent disconnected",
                    () -> getGameController().gotoMainMenu());
        });
        FXGL.getService(ConnectionService.class).robustDisconnect();
    }


}