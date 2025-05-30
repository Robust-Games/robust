package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

public class ShellComponent extends Component {

    private Entity target;

    public ShellComponent(Entity target) {
        this.target = target;
    }

    @Override
    public void onUpdate(double tpf) {
        if (!target.isActive()) {
            entity.removeFromWorld();
            return;
        }

        if (entity.distanceBBox(target) < 64*tpf) {
            entity.removeFromWorld();
            return;
        }
        entity.translateTowards(target.getCenter(), (500 * tpf));
    }


}
