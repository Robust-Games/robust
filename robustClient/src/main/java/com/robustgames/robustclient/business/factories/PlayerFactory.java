package com.robustgames.robustclient.business.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.AutoRotationComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.ui.ProgressBar;
import com.robustgames.robustclient.application.RobustApplication;
import com.robustgames.robustclient.business.entitiy.components.ShellComponent;
import com.robustgames.robustclient.business.entitiy.components.RotateComponent;
import com.robustgames.robustclient.business.entitiy.components.SelectableComponent;
import com.robustgames.robustclient.business.entitiy.components.ShootComponent;
import com.robustgames.robustclient.business.logic.MapService;
import javafx.scene.paint.Color;

import static com.robustgames.robustclient.business.entitiy.EntityType.*;

public class PlayerFactory implements EntityFactory {
    private static final int HP = 3;

    @Spawns("tank1")
    public Entity spawnTankPlayer1(SpawnData data) {
        var hpBar = new ProgressBar();
        hpBar.setWidth(90);
        hpBar.setHeight(15);
        hpBar.setTranslateY(20);
        hpBar.setTranslateX(19);
        hpBar.setMaxValue(HP);
        hpBar.setFill(Color.GREEN);
        //hpBar.setVisible(false);

        var hpComp = new HealthIntComponent(HP);
        hpBar.currentValueProperty().bind(hpComp.valueProperty());

        return FXGL.entityBuilder(data)
                .type(TANK)
                .view(hpBar)
                .with(hpComp)
                .viewWithBBox("tank_top_left.png")
                .with(new RotateComponent())
                .with(new ShootComponent())
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
                .zIndex(1)
                .viewWithBBox("city1.png")
                .build();
    }

    @Spawns("tank2")
    public Entity spawnTankPlayer2(SpawnData data) {
        var hp = new HealthIntComponent(HP);
        //var viewHP =
        return FXGL.entityBuilder(data)
                .type(TANK)
                .zIndex(1)
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
        var hp = new HealthIntComponent(HP);
        //var viewHP =
        return FXGL.entityBuilder(data)
                .type(CITY)
                .zIndex(1)
                .viewWithBBox("city1.png")
                .build();
    }
    @Spawns("shell")
    public Entity spawnShell(SpawnData data) {
        Entity target = data.get("target");

        return FXGL.entityBuilder(data)
                .type(SHELL)
                .viewWithBBox("shell.gif")
                .with(new ShellComponent(target))
                .build();
    }
}
