package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.Node;

public class SelectableComponent extends Component {
    Node selectionMarker = FXGL.getAssetLoader().loadTexture("selection.png");

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(selectionMarker);
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        if (entity.getViewComponent().getChildren().contains(selectionMarker)) {
            entity.getViewComponent().removeChild(selectionMarker);
        }

    }
}