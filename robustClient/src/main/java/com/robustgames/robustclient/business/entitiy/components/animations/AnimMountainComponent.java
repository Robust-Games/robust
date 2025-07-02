package com.robustgames.robustclient.business.entitiy.components.animations;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import javafx.util.Duration;

public class AnimMountainComponent extends Component {
    private final AnimatedTexture texture;
    private final AnimationChannel mountainDestroyed;

    public AnimMountainComponent() {
        mountainDestroyed = new AnimationChannel(FXGL.image("mountain_destroyed.png"), 15, 128,128, Duration.seconds(1.5), 0, 14);
        texture = new AnimatedTexture(mountainDestroyed);
    }

    @Override
    public void onAdded() {
        Texture current = entity.getViewComponent().getChild(0, Texture.class);
        entity.getViewComponent().removeChild(current);
        entity.getViewComponent().addChild(texture);
        texture.play();
        texture.setOnCycleFinished(()->
                entity.removeFromWorld());
    }
    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
    }

}