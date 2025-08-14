package com.robustgames.robustclient.business.actions;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.action.Action;
import com.almasb.fxgl.net.Connection;
import com.robustgames.robustclient.application.RobustApplication;
import com.robustgames.robustclient.business.entitiy.components.TankDataComponent;
import com.robustgames.robustclient.business.factories.BundleFactory;
import com.robustgames.robustclient.business.logic.gameService.MapService;
import com.robustgames.robustclient.business.logic.tankService.MovementService;
import javafx.geometry.Point2D;

public class MovementAction extends Action {
    private final Entity target;
    private boolean isLocal;
    private double speed = 200; // pixels/sec


    public MovementAction(Entity target) {
        this.target = target;
        this.isLocal = true;
    }

    public MovementAction(Entity target, boolean isLocal) {
        this.target = target;
        this.isLocal = isLocal;
    }

    /**
     * Called when the movement action starts.
     * <p>
     * This method sends a movement action bundle to the server,
     * informing it about the intended move of the entity to the target's grid position.
     * If a network connection exists, the move information is serialized using {@link BundleFactory}
     * and transmitted over the network. If there is no connection, a message is logged instead.
     * </p>
     */
    @Override
    protected void onStarted() {
//        // Netzwerk: Sende MoveAction an den Server
//        RobustApplication app = FXGL.<RobustApplication>getAppCast(); // Holt die aktuell laufende Instanz der RobustApplication aus dem FXGL-Framework
//        Connection<Bundle> conn = app.getConnection();
//        if (conn != null) {
//            // Client-ID holen
//            int clientId = FXGL.<RobustApplication>getAppCast().getClientId();
//
//            // Zielposition als Grid-Koordinaten (Center des Tiles)
//            Point2D gridTarget = MapService.isoScreenToGrid(target.getCenter());
//            // long tileId = target.getComponent(IDComponent.class).getId(); // erstellt Tile ID
//            Bundle moveBundle = BundleFactory.createMoveActionBundle(entity, gridTarget); // ID mitgeben
//            moveBundle.put("clientId", clientId);
//            conn.send(moveBundle);
//
//            // DEBUG
//            System.out.println(" Sending moveBundle:");
//
//        } else {
//            System.out.println("No connection set – can't send move!");
//        }
    }

    @Override
    protected void onUpdate(double tpf) {
        Point2D direction = target.getPosition().subtract(entity.getPosition()).normalize();
        entity.translate(direction.multiply(tpf * speed));

        if (entity.distance(target) < 5) {
            entity.setPosition(target.getPosition());
            MovementService.changeMountainLayer(entity);
            setComplete();
        }
    }

    @Override
    protected void onQueued() {
        if (!isLocal) return;

        RobustApplication app = FXGL.getAppCast();
        Connection<Bundle> conn = app.getConnection();
        if (conn != null) {
            int clientId = app.getClientId();
            Point2D gridTarget = MapService.isoScreenToGrid(target.getCenter());
            Bundle moveBundle = BundleFactory.createMoveActionBundle(entity, gridTarget);
            moveBundle.put("clientId", clientId);
            conn.send(moveBundle);
        }
    }


    @Override
    protected void onCompleted() {
        super.onCompleted();
        entity.getComponent(TankDataComponent.class).setInitialPos();
        entity.getComponent(TankDataComponent.class).resetBeforeTurn();

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

}

