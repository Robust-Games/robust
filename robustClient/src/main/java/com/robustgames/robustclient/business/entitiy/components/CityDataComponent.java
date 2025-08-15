/**
 * @author Nico Steiner
 */
package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import com.robustgames.robustclient.business.logic.Player;

public class CityDataComponent extends Component {
    private Texture initialCityTexture;
    private Texture newCityTexture;
    private final Player owner;

    public CityDataComponent(Player player, Texture view) {
        owner = player;
        initialCityTexture = view;
        newCityTexture = view;
    }

    public Texture getInitialCityTexture() {
        return initialCityTexture;
    }

    public void setInitialCityTexture(Texture initialCityTexture) {
        this.initialCityTexture = initialCityTexture;
    }

    public Texture getNewCityTexture() {
        return newCityTexture;
    }

    public void setNewCityTexture(Texture newCityTexture) {
        this.newCityTexture = newCityTexture;
    }

    public Player getOwner() {
        return owner;
    }
}

