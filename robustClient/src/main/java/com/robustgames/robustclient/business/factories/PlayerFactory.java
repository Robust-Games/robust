package com.robustgames.robustclient.business.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.robustgames.robustclient.application.RobustApplication;
import com.robustgames.robustclient.business.entitiy.components.APComponent;
import com.robustgames.robustclient.business.entitiy.components.RotateComponent;
import com.robustgames.robustclient.business.entitiy.components.SelectableComponent;
import com.robustgames.robustclient.business.logic.GameState;
import com.robustgames.robustclient.business.logic.MapService;

import static com.robustgames.robustclient.business.entitiy.EntityType.*;

public class PlayerFactory implements EntityFactory {
    private static final int HP = 3;

    @Spawns("tank1")
    public Entity spawnTankPlayer1(SpawnData data) {
        var hpBar = GameState.hpBarInit(HP);
        var hpComp = new HealthIntComponent(HP);
        hpBar.currentValueProperty().bind(hpComp.valueProperty());

        return FXGL.entityBuilder(data)
                .type(TANK)
                .view(hpBar)
                .with(hpComp)
                .viewWithBBox("tank_top_left.png")
                .with(new RotateComponent())
                .with(new APComponent(5))
                .onClick(tank -> {
                    //TODO Make the tile that the tank is standing on, also select the tank. i.e. add a tank property to hovertile
                    MapService.deSelectTank();
                    tank.addComponent(new SelectableComponent());
                    FXGL.<RobustApplication>getAppCast().onTankClicked(tank);

                })
                .build();
    }

    @Spawns("city1")
    public Entity spawnCityPlayer1(SpawnData data) {
        var hpBar = GameState.hpBarInit(HP);
        var hpComp = new HealthIntComponent(HP);
        hpBar.currentValueProperty().bind(hpComp.valueProperty());

        return FXGL.entityBuilder(data)
                .type(CITY)
                .view(hpBar)
                .with(hpComp)
                .viewWithBBox("city1.png")
                .build();
    }

    @Spawns("tank2")
    public Entity spawnTankPlayer2(SpawnData data) {
        var hpBar = GameState.hpBarInit(HP);
        var hpComp = new HealthIntComponent(HP);
        hpBar.currentValueProperty().bind(hpComp.valueProperty());

        return FXGL.entityBuilder(data)
                .type(TANK)
                .view(hpBar)
                .with(hpComp)
                .viewWithBBox("tank_down_right.png")
                .with(new RotateComponent())
                .with(new APComponent(5))
                .onClick(tank -> {
                    //TODO Make the tile that the tank is standing on, also select the tank. i.e. add a tank property to hovertile
                    MapService.deSelectTank();
                    tank.addComponent(new SelectableComponent());
                    FXGL.<RobustApplication>getAppCast().onTankClicked(tank);

                })
                .build();
    }

    @Spawns("city2")
    public Entity spawnCityPlayer2(SpawnData data) {
        var hpBar = GameState.hpBarInit(HP);
        var hpComp = new HealthIntComponent(HP);
        hpBar.currentValueProperty().bind(hpComp.valueProperty());

        return FXGL.entityBuilder(data)
                .type(CITY)
                .view(hpBar)
                .with(hpComp)
                .viewWithBBox("city1.png")
                .build();
    }
}
