package com.robustgames.robustclient.business.collision;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.robustgames.robustclient.business.entitiy.components.HPComponent;

import static com.robustgames.robustclient.business.entitiy.EntityType.CITY;
import static com.robustgames.robustclient.business.entitiy.EntityType.SHELL;


public class ShellCityHandler extends CollisionHandler {

    public ShellCityHandler() {
        super(SHELL, CITY);
    }

    @Override
    protected void onCollisionBegin(Entity shell, Entity city) {
        shell.removeFromWorld();

       // var hp = access HPComponent and DamageComponent

        //hp damage
        //sprite change

        /*if (city damaged) {
            city.removeFromWorld();
            cause game over in GameState
        }*/
    }
}
