package com.robustgames.robustclient.business.actions;

import com.almasb.fxgl.entity.action.Action;
import com.almasb.fxgl.texture.Texture;
import com.robustgames.robustclient.business.entitiy.components.MovementComponent;
import com.robustgames.robustclient.business.entitiy.components.TankDataComponent;
import com.robustgames.robustclient.business.logic.Direction;
import com.robustgames.robustclient.business.logic.tankService.RotateService;

public class RotateAction extends Action {
    private final Texture newTankTexture;
    private final Direction direction;

    public RotateAction(Texture newTankTexture, Direction direction) {
        this.newTankTexture = newTankTexture;
        this.direction = direction;

    }
    @Override
    protected void onUpdate(double tpf) {
        if (direction == Direction.LEFT) {
            RotateService.rotateTankLeft(entity);
        }
        else
            RotateService.rotateTankRight(entity);
        setComplete();
    }

    @Override
    protected void onCompleted() {
        super.onCompleted();
        entity.getComponent(TankDataComponent.class).setInitialTankTexture(newTankTexture);
    }
}
