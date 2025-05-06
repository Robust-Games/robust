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

    @Spawns("tank")
    public Entity spawnTank(SpawnData data) {
        return FXGL.entityBuilder(data)
                .viewWithBBox("sprite1.png")
                .build();
    }
    @Spawns("city")
    public Entity spawnCity(SpawnData data) {
        return FXGL.entityBuilder(data)
                .viewWithBBox("sprite3.png").onClick(System.out::println)
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
