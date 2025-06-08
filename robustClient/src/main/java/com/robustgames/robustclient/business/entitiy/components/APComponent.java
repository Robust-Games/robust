package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.dsl.components.RechargeableIntComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.IntegerComponent;

public class APComponent extends RechargeableIntComponent {
    private final int maxAP = 5;
    //protected int currentAP;

    public APComponent (int maxAP){
        super(maxAP, 5);
    }

    public boolean canUse(int cost) {
        return getValue() >= cost;
    }

    public void use(int cost) {
        if (!canUse(cost)){
            return;
        }
        this.damage(cost);
    }

    public void reset() { // für Server
        restoreFully();
    }

    public int getCurrentAP() {
        return getValue();
    }

}
