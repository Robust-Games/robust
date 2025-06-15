package com.robustgames.robustclient.business.actions;

import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.action.Action;
import com.robustgames.robustclient.business.entitiy.components.APComponent;
import com.robustgames.robustclient.business.logic.tankService.ShootService;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.getGameTimer;

public class ShootAction extends Action {
    private final Entity target;

    public ShootAction(Entity target) {
        this.target = target;
    }

    @Override
    protected void onStarted() {
        entity.getComponent(APComponent.class).damageFully();
    }

    @Override
    protected void onUpdate(double tpf) {
            target.getComponent(HealthIntComponent.class).damage(1);
            ShootService.spawnShell(entity, target.getCenter());

        // Wait for explosion animation to complete
        getGameTimer().runOnceAfter(this::setComplete, Duration.seconds(1.2));
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
