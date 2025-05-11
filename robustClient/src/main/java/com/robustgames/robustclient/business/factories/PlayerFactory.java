package com.robustgames.robustclient.business.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.robustgames.robustclient.business.entitiy.components.RotateComponent;

public class PlayerFactory implements EntityFactory {
    @Spawns("tank1")
    public Entity spawnTankPlayer1(SpawnData data) {
        RotateComponent rc = new RotateComponent();
        rc.setCurrentAngle(270); //default -> schaut nach links

        return FXGL.entityBuilder(data)
                .viewWithBBox("tank2D_left.png")
                .with(rc)
                .onClick(e ->{ // OnClick löst Lambda aus
                    FXGL.getGameWorld().getProperties().setValue("selectedTank", e);
                    System.out.println("tank1 gewählt");
                })
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
        RotateComponent rc = new RotateComponent();
        rc.setCurrentAngle(90);// default -> schaut nach rechts

        return FXGL.entityBuilder(data)
                .viewWithBBox("tank2D_right.png")
                .with(rc)
                .with(new RotateComponent())
                .onClick(e ->{ // OnClick löst Lambda aus
                    FXGL.getGameWorld().getProperties().setValue("selectedTank", e); // speichert Panzer unter selected Entity
                    System.out.println("tank2 gewählt");
                })
                .build();
    }
    @Spawns("city2")
    public Entity spawnCityPlayer2(SpawnData data) {
        return FXGL.entityBuilder(data)
                .viewWithBBox("city2D.png").onClick(System.out::println)
                .build();
    }
}
