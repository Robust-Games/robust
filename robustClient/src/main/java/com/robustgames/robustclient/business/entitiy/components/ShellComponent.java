package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

public class ShellComponent extends Component {

    private final Point2D targetLocation;

    public ShellComponent(Point2D targetLocation) {
        this.targetLocation = targetLocation;
    }

    @Override
    public void onUpdate(double tpf) {
        if (entity.getPosition().distance(targetLocation) < 100) {
            entity.removeFromWorld();
            return;
        }
        entity.translateTowards(targetLocation, (800 * tpf));
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
    }
}
