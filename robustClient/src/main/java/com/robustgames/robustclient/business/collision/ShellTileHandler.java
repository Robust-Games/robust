package com.robustgames.robustclient.business.collision;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import static com.robustgames.robustclient.business.entitiy.EntityType.SHELL;
import static com.robustgames.robustclient.business.entitiy.EntityType.TILE;

public class ShellTileHandler extends CollisionHandler {

    public ShellTileHandler() {
        super(SHELL, TILE);
    }

    @Override
    protected void onCollisionBegin(Entity shell, Entity tile) {
        shell.removeFromWorld();
        tile.removeFromWorld();

    }
}
