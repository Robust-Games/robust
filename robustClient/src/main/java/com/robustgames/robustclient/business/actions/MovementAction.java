package com.robustgames.robustclient.business.actions;

import com.almasb.fxgl.core.serialization.Bundle;
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

import java.io.Serializable;

public class MovementAction extends Action {
    private final Entity target;
    private double speed = 200; // pixels/sec


    public MovementAction(Entity target) {
        this.target = target;
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
        // Netzwerk: Sende MoveAction an den Server
        RobustApplication app = FXGL.<RobustApplication>getAppCast(); // Holt die aktuell laufende Instanz der RobustApplication aus dem FXGL-Framework
        Connection<Bundle> conn = app.getConnection();
        if (conn != null) {
            // Zielposition als Grid-Koordinaten (Center des Tiles)
            Point2D gridTarget = MapService.isoScreenToGrid(target.getCenter());
            Bundle moveBundle = BundleFactory.createMoveActionBundle(entity, gridTarget);
            conn.send(moveBundle);
        } else {
            System.out.println("No connection set – can't send move!");
        }
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
        super.onQueued();
    }


    @Override
    protected void onCompleted() {
        super.onCompleted();
        entity.getComponent(TankDataComponent.class).setInitialPos();

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle("MovementAction");
        bundle.put("targetId", (Serializable) target.typeProperty());
        bundle.put("speed", speed);
        return bundle;
    }
    /*

    public static MovementAction fromBundle(Bundle bundle, EntityLookup lookup) {
        String targetId = bundle.get("targetId");
        Entity targetEntity = lookup.getEntityById(targetId);
        MovementAction action = new MovementAction(targetEntity);
        action.speed = bundle.getDouble("speed");
        return action;
    }
*/

}

