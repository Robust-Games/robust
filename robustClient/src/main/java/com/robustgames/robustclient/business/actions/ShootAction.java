package com.robustgames.robustclient.business.actions;

import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.action.Action;
import com.robustgames.robustclient.business.entitiy.components.APComponent;
import com.robustgames.robustclient.business.logic.tankService.ShootService;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.robustgames.robustclient.business.entitiy.EntityType.ACTIONSELECTION;
import static com.robustgames.robustclient.business.logic.tankService.ShootService.spawnAttackTarget;

public class ShootAction extends Action {
    private final Entity target;

    public ShootAction(Entity target) {
        this.target = target;
    }

    @Override
    protected void onStarted() {
        spawnAttackTarget(target, entity);
        getGameTimer().runOnceAfter(() -> {
            getGameWorld().removeEntities(byType(ACTIONSELECTION));
            ShootService.executeShoot(target, entity, false);
            getGameTimer().runOnceAfter(this::setComplete, Duration.seconds(1.2));
        }, Duration.seconds(0.5));


    }

    @Override
    protected void onUpdate(double tpf) {

    }
    @Override
    protected void onQueued() {
        super.onQueued();
    }

    @Override
    protected void onCompleted() {
        super.onCompleted();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

}
