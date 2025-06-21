package com.robustgames.robustclient.business.actions;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.action.Action;
import com.robustgames.robustclient.business.entitiy.components.TankDataComponent;
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

