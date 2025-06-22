package com.robustgames.robustclient.business.entitiy.components.animations;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.getGameTimer;

public class AnimTankTurret extends Component {
    private final AnimatedTexture texture;
    private AnimationChannel shootingBarrel;

    public AnimTankTurret(String tankTurretView) {
        String tankTurretViewShoot = tankTurretView.substring(0, tankTurretView.lastIndexOf(".")) + "_shoot.png";
        shootingBarrel = new AnimationChannel(FXGL.image(tankTurretViewShoot), 6, 128, 128, Duration.seconds(0.6), 0, 5);
        texture = new AnimatedTexture(shootingBarrel);


    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
        texture.play();
        getGameTimer().runOnceAfter(() -> {
            entity.getViewComponent().removeChild(texture);
        }, Duration.seconds(0.6));

    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        entity.getViewComponent().removeChild(texture);
    }

}
