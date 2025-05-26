package com.robustgames.robustclient.business.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.robustgames.robustclient.application.RobustApplication;
import com.robustgames.robustclient.business.entitiy.components.RotateComponent;
import com.robustgames.robustclient.business.entitiy.components.SelectableComponent;
import com.robustgames.robustclient.business.logic.MapService;

import static com.robustgames.robustclient.business.entitiy.EntityType.CITY;
import static com.robustgames.robustclient.business.entitiy.EntityType.TANK;

public class PlayerFactory implements EntityFactory {

    @Spawns("tank1")
    public Entity spawnTankPlayer1(SpawnData data) {
        return FXGL.entityBuilder(data)
                .viewWithBBox("tank_top_left.png")
                .with(new RotateComponent())
                .onClick(tank ->{
                    //TODO Make the tile that the tank is standing on, also select the tank. i.e. add a tank property to hovertile
                    MapService.deSelectTank();
                    tank.addComponent(new SelectableComponent());
                    FXGL.<RobustApplication>getAppCast().onTankClicked(tank);

                })
                .build();
    }
  
    @Spawns("city1")
    public Entity spawnCityPlayer1(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(CITY)
                .viewWithBBox("city1.png").onClick(System.out::println)
                .build();
    }
  
    @Spawns("tank2")
    public Entity spawnTankPlayer2(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(TANK)
                .viewWithBBox("tank_down_right.png")
                .with(new RotateComponent())
                .onClick(tank ->{
                    //TODO Make the tile that the tank is standing on, also select the tank. i.e. add a tank property to hovertile
                    MapService.deSelectTank();
                    tank.addComponent(new SelectableComponent());
                    FXGL.<RobustApplication>getAppCast().onTankClicked(tank);

                })
                .build();
    }
  
    @Spawns("city2")
    public Entity spawnCityPlayer2(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(CITY)
                .viewWithBBox("city1.png").onClick(System.out::println)
                .build();
    }
}
