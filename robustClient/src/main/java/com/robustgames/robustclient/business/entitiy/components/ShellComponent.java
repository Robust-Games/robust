package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

public class ShellComponent extends Component {

    private Entity tank;
    private Entity target;

    public ShellComponent(Entity tank, Entity target) {
        this.tank = tank;
        this.target = target;
    }

    @Override
    public void onUpdate(double tpf) {
        if (!target.isActive()) {
            entity.removeFromWorld();
            return;
        }

        if (entity.distanceBBox(target) < 100 * tpf) {
            onTargetHit();
            return;
        }
        entity.translateTowards(target.getCenter(), 100 * tpf);
    }

    private void onTargetHit() {
        entity.removeFromWorld();

//        var hp = target.getComponent(HealthIntComponent.class);
//
//        hp.damage(1);
//
//        if (hp.isZero()) {
//            target.removeFromWorld();
//        }
    }
}
