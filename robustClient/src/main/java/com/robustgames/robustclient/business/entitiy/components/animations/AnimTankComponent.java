package com.robustgames.robustclient.business.entitiy.components.animations;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

public class AnimTankComponent extends Component {
    private final AnimatedTexture texture;
    private final AnimationChannel tankMotorShake;

    public AnimTankComponent() {
        tankMotorShake = new AnimationChannel(FXGL.image("Tank_selected.png"), 2, 128,128,Duration.seconds(0.2), 0, 1);
        texture = new AnimatedTexture(tankMotorShake);
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
        texture.loopAnimationChannel(tankMotorShake);
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        entity.getViewComponent().removeChild(texture);
    }

}