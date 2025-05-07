package com.robustgames.robustclient.business;


import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameObjectFactory implements EntityFactory {

    @Spawns("tank1")
    public Entity spawnTankPlayer1(SpawnData data) {
        return FXGL.entityBuilder(data)
                .viewWithBBox("tank2D_left.png")
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
                .viewWithBBox("tank2D_right.png")
                .build();
    }
    @Spawns("city2")
    public Entity spawnCityPlayer2(SpawnData data) {
        return FXGL.entityBuilder(data)
                .viewWithBBox("city2D.png").onClick(System.out::println)
                .build();
    }
    @Spawns("Background")
    public Entity spawnBackground(SpawnData data) {
        return FXGL.entityBuilder(data)
                .view(new Rectangle(data.<Integer>get("width"), data.<Integer>get("height"), Color.GREY))
                .with(new IrremovableComponent())
                .zIndex(-100)
                .build();
    }
}
