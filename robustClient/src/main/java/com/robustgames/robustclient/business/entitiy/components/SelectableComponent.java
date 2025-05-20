package com.robustgames.robustclient.business.entitiy.components;


import com.almasb.fxgl.entity.component.Component;
import com.robustgames.robustclient.business.entitiy.components.animations.TankAnimComponent;
import static com.almasb.fxgl.dsl.FXGL.byType;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.robustgames.robustclient.business.entitiy.EntityType.ACTIONSELECTION;

public class SelectableComponent extends Component {
    //selectionmarker mal mit animation ausprobiert
    //Node selectionMarker = FXGL.getAssetLoader().loadTexture("selection.png");

    @Override
    public void onAdded() {
        entity.addComponent(new TankAnimComponent());

    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        entity.removeComponent(TankAnimComponent.class);
        getGameWorld().removeEntities(byType(ACTIONSELECTION));
//        if (entity.getViewComponent().getChildren().contains(selectionMarker)) {
//            entity.getViewComponent().removeChild(selectionMarker);
//        }

    }
}