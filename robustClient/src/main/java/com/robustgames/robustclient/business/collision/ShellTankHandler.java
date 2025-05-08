package com.robustgames.robustclient.business.collision;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;

import static com.almasb.fxgl.dsl.FXGL.getDialogService;
import static com.almasb.fxgl.dsl.FXGL.getGameController;
import static com.robustgames.robustclient.business.entitiy.EntityType.SHELL;
import static com.robustgames.robustclient.business.entitiy.EntityType.TANK;


public class ShellTankHandler extends CollisionHandler {

    public ShellTankHandler() {
        super(SHELL, TANK);
    }

    @Override
    protected void onCollisionBegin(Entity shell, Entity tank) {
        shell.removeFromWorld();

        // var hp = access HPComponent and DamageComponent

        //hp damage

        /*if (dead tank) {
            tank.removeFromWorld();
            make dead tank sprite or something
            cause game over in GameState
        }*/
    }
}
