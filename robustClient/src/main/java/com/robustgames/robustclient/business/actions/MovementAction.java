/**
 * @author Burak Altun, Carolin Scheffler, Ersin Yesiltas, Nico Steiner
 */
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
import com.robustgames.robustclient.business.logic.networkService.ConnectionService;
import com.robustgames.robustclient.business.logic.tankService.MovementService;
import javafx.geometry.Point2D;

public class MovementAction extends Action {
    private final Entity target;
    private final boolean isLocal;
    private final double speed = 200; // pixels/sec


    /**
     * Creates a movement action that moves the owning entity towards the given target entity.
     * The action originates locally and may be sent to the server when queued (online mode).
     *
     * @param target the target entity whose position will be used as destination
     */
    public MovementAction(Entity target) {
        this.target = target;
        this.isLocal = true;
    }

    /**
     * Creates a movement action with explicit locality flag.
     *
     * @param target  the target entity whose position will be used as destination
     * @param isLocal whether the action originates locally (true) or was received from network (false)
     */
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

    /**
     * Updates the movement each frame by moving the entity towards the target at a constant speed.
     * When the entity is close enough to the target, it snaps to the destination, adjusts layers,
     * and completes the action.
     *
     * @param tpf time per frame provided by the engine
     */
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

    /**
     * Called when this action is queued on an entity. If the action originates locally and a connection
     * is available, a MoveAction bundle is created and sent to the server to synchronize the move.
     */
    @Override
    protected void onQueued() {
        if (!isLocal) return;

        RobustApplication app = FXGL.getAppCast();
        Connection<Bundle> conn = FXGL.getService(ConnectionService.class).getConnection();
        if (conn != null) {
            int clientId = app.getClientId();
            Point2D gridTarget = MapService.isoScreenToGrid(target.getCenter());
            Bundle moveBundle = BundleFactory.createMoveActionBundle(entity, gridTarget);
            moveBundle.put("clientId", clientId);
            conn.send(moveBundle);
        }
    }


    /**
     * Called after the movement action completes. Resets the tank's initial position and
     * prepares it for the next turn.
     */
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


