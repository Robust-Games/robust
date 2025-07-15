package com.robustgames.robustclient.business.entitiy.components.animations;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

public class AnimZero extends Component{
    private AnimatedTexture texture;
    private AnimationChannel zeroChannel;

    public AnimZero() {
        zeroChannel = new AnimationChannel(FXGL.image("oneZero.png"), 60, 1, 1, Duration.seconds(1), 0, 59);
        texture = new AnimatedTexture(zeroChannel);
        texture.setTranslateY(-64);

    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
        texture.loopAnimationChannel(zeroChannel);

    }
    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        entity.getViewComponent().removeChild(texture);
    }
}
