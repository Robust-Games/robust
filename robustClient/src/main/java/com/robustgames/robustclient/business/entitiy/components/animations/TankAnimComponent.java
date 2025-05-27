package com.robustgames.robustclient.business.entitiy.components.animations;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

public class TankAnimComponent extends Component {
    private Entity tank;
    private Entity tile;
    private AnimatedTexture texture;
    private AnimationChannel tankMotorShake, tankSelectionMarker;

    public TankAnimComponent() {
        tankMotorShake = new AnimationChannel(FXGL.image("Tank_selected.png"), 2, 128,128,Duration.seconds(1), 0, 1);
        tankSelectionMarker = new AnimationChannel(FXGL.image("Tank_selected_Border.png"), 4, 128, 128, Duration.seconds(1), 0, 3);

        //texture = new AnimatedTexture(tankIdle);
        texture = new AnimatedTexture(tankSelectionMarker);
    }

    @Override
    public void onAdded() {
        //entity.getViewComponent().addChild(texture);
        texture.loopAnimationChannel(tankSelectionMarker);

    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        entity.getViewComponent().removeChild(texture);
    }

    @Override
    public void onUpdate(double tpf) {
//        System.out.println(getEntity());
//        System.out.println(getEntity().isActive());
//        if (getEntity().isActive()) {
//            else if (getEntity().hasComponent(HPComponent.class) && texture.getAnimationChannel() == tankSelection)
//                texture.loopAnimationChannel(tankIdle);


    }

}