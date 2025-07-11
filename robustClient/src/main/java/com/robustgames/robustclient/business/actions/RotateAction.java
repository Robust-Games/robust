package com.robustgames.robustclient.business.actions;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.action.Action;
import com.almasb.fxgl.texture.Texture;
import com.robustgames.robustclient.business.entitiy.components.MovementComponent;
import com.robustgames.robustclient.business.entitiy.components.TankDataComponent;
import com.robustgames.robustclient.business.logic.Direction;
import com.robustgames.robustclient.business.logic.tankService.RotateService;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.getGameTimer;

public class RotateAction extends Action {
    private final Texture newTankTexture;

    public RotateAction(String newTankTexture) {
        this.newTankTexture = FXGL.getAssetLoader().loadTexture(newTankTexture);
    }
    @Override
    protected void onUpdate(double tpf) {
        getGameTimer().runOnceAfter(() -> {
                    entity.getComponent(TankDataComponent.class).getInitialTankTexture().set(newTankTexture);
                    setComplete();
        }, Duration.millis(200));
    }


    @Override
    protected void onCompleted() {
        super.onCompleted();
    }
}
