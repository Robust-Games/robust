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
    private double speed = 200; // pixels/sec


    public MovementAction(Entity target) {
        this.target = target;
    }

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

}

