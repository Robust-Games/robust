package com.robustgames.robustclient.business.entitiy.components.animations;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

public class AnimSelectionComponent extends Component {
    private AnimatedTexture texture;
    private AnimationChannel tankSelectionMarker;

    public AnimSelectionComponent() {
        tankSelectionMarker = new AnimationChannel(FXGL.image("Tank_selected_Border.png"), 4, 128, 128, Duration.seconds(1), 0, 3);
        texture = new AnimatedTexture(tankSelectionMarker);
        texture.setTranslateX(-64);
        texture.setTranslateY(-64);

    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
        texture.loopAnimationChannel(tankSelectionMarker);

    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        entity.getViewComponent().removeChild(texture);
    }
}
