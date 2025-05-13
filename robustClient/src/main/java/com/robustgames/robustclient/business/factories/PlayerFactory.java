package com.robustgames.robustclient.business.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;

public class PlayerFactory implements EntityFactory {
    @Spawns("tank1")
    public Entity spawnTankPlayer1(SpawnData data) {
        return FXGL.entityBuilder(data)
                .viewWithBBox("tank2D_left.png").onClick(System.out::println)
                .build();
    }
    @Spawns("city1")
    public Entity spawnCityPlayer1(SpawnData data) {
        return FXGL.entityBuilder(data)
                .viewWithBBox("city2D.png").onClick(System.out::println)
                .build();
    }
    @Spawns("tank2")
    public Entity spawnTankPlayer2(SpawnData data) {
        return FXGL.entityBuilder(data)
                .viewWithBBox("tank2D_right.png").onClick(System.out::println)
                .build();
    }
    @Spawns("city2")
    public Entity spawnCityPlayer2(SpawnData data) {
        return FXGL.entityBuilder(data)
                .viewWithBBox("city2D.png").onClick(System.out::println)
                .build();
    }
}
