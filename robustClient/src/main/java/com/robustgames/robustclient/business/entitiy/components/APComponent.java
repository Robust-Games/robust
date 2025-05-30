package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.entity.component.Component;

public class APComponent extends Component {
    private final int maxAP = 5;
    protected int currentAP;

    public APComponent (int currentAP){
        this.currentAP = currentAP;
    }

    public boolean canUse(int cost) {
        return currentAP >= cost;
    }

    public boolean use(int cost) {
        if (!canUse(cost)){
            return false;
        }
        currentAP -= cost;
        return true;
    }

    public void reset() { // für Server
        currentAP = maxAP;
    }

    public int getCurrentAP() {
        return currentAP;
    }
}
