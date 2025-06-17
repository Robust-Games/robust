package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.dsl.components.RechargeableIntComponent;

public class APComponent extends RechargeableIntComponent {
    private final int maxAP = 5;
    //protected int currentAP;

    public APComponent(int maxAP) {
        super(maxAP, 5);
    }

    public boolean canUse(int cost) {
        return getValue() >= cost;
    }

    public void use(int cost) {
        if (!canUse(cost)) {
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

    /*
    Setzt den aktuellen Wert der AP.
    Wird verwendet, um den AP-Status nach Synchronisationen vom Server zu aktualisieren.
    */
    public void setCurrentAP(int value) {
        this.valueProperty().set(value);
    }
}
