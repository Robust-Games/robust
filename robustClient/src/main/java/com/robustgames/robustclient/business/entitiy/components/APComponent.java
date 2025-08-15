/**
 * @author Burak Altun, Ersin Yesiltas, Nico Steiner
 */
package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.dsl.components.RechargeableIntComponent;

public class APComponent extends RechargeableIntComponent {
    private final int maxAP = 5;
    private double apCost = 0;

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

    public double getAPCost() {
        return apCost;
    }

    public void setAPCost(double apCost) {
        this.apCost = apCost;
    }

    /*
    sets the current AP.
    used to set AP after synchronisation with the server
    */
    public void setCurrentAP(int value) {
        this.valueProperty().set(value);
    }
}

