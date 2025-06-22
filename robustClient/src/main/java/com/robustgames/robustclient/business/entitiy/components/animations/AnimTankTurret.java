package com.robustgames.robustclient.business.entitiy.components.animations;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

public class AnimTankTurret extends Component {
    private final AnimatedTexture texture;
    private AnimationChannel expl;
    private String tankTurretView;

    public AnimTankTurret(String tankTurretView) {
        this.tankTurretView = tankTurretView;
        expl = new AnimationChannel(FXGL.image("explosion.png"), 12, 128, 128, Duration.seconds(1.2), 0, 12);
        texture = new AnimatedTexture(expl);


    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
        texture.play();

    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        entity.getViewComponent().removeChild(texture);
    }

}
