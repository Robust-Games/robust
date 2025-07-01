package com.robustgames.robustclient.business.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.texture.Texture;
import com.robustgames.robustclient.business.entitiy.components.ShellComponent;
import com.robustgames.robustclient.business.entitiy.components.animations.AnimCityComponent;
import com.robustgames.robustclient.business.logic.gameService.MapService;
import com.robustgames.robustclient.business.logic.tankService.MovementService;
import com.robustgames.robustclient.business.logic.tankService.RotateService;
import com.robustgames.robustclient.business.logic.tankService.ShootService;
import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import static com.robustgames.robustclient.business.entitiy.EntityType.*;

/**
 * For destructible entities on our map. May later include tiles
 */
public class MapFactory implements EntityFactory {
    @Spawns("Background")
    public Entity spawnBackground(SpawnData data) {
        return FXGL.entityBuilder(data)
                .view(new Rectangle(data.<Integer>get("width"), data.<Integer>get("height"), Color.GREY))
                .with(new IrremovableComponent())
                .zIndex(-100)
                .build();
    }

    @Spawns("mountain")
    public Entity spawnMountain(SpawnData data) {
        var hpComp = new HealthIntComponent(1);

        return FXGL.entityBuilder(data).type(MOUNTAIN)
                .with(hpComp)
                .viewWithBBox("mountain.png")
                .build();
    }

    //Tile Grafik, aktuell nicht genutzt
    @Spawns("floorTile")
    public Entity spawnFloor(SpawnData data) {
        var hpComp = new HealthIntComponent(2);
        Texture floorTexture = FXGL.getAssetLoader().loadTexture("floor_tile1.png");
        var floor = FXGL.entityBuilder(data).type(TILE)
                .zIndex(-1)
                .with(hpComp)
                .build();

        floor.getViewComponent().addChild(floorTexture);
        Texture hoverTexture = FXGL.getAssetLoader().loadTexture("Tile_selection.png");
        hoverTexture.mouseTransparentProperty().setValue(true);
        floorTexture.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
            if (isNowHovered) {
                if (!floor.getViewComponent().getChildren().contains(hoverTexture)) {
                    floor.getViewComponent().addChild(hoverTexture);
                }
            }
            else if (wasHovered)
                floor.getViewComponent().removeChild(hoverTexture);
        });

        return floor;
    }
    @Spawns("floorTileMountain")
    public Entity spawnMountainFloor(SpawnData data) {
        var hpComp = new HealthIntComponent(2);
        Texture floorTexture = FXGL.getAssetLoader().loadTexture("floorTileMountain1.png");
        var floorMountain = FXGL.entityBuilder(data).type(TILE)
                .zIndex(-1)
                .with(hpComp)
                .build();
        floorMountain.getViewComponent().addChild(floorTexture);

        Texture hoverTexture = FXGL.getAssetLoader().loadTexture("Tile_selection.png");
        hoverTexture.mouseTransparentProperty().setValue(true);
        floorTexture.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
            if (isNowHovered) {
                if (!floorMountain.getViewComponent().getChildren().contains(hoverTexture)) {
                    floorMountain.getViewComponent().addChild(hoverTexture);
                }
            }
            else if (wasHovered)
                floorMountain.getViewComponent().removeChild(hoverTexture);
        });

        return floorMountain;
    }
    @Spawns("hoverTile")
    public Entity spawnHoverFloor(SpawnData data) {
        Polygon diamond = new Polygon();
        diamond.getPoints().addAll(
                0.0, 0.0,    // Top
                64.0, 32.0,  // Right
                0.0, 64.0,   // Bottom
                -64.0, 32.0     // Left
        );
        diamond.setOpacity(0.40);
        var cell = FXGL.entityBuilder(data).type(HOVER).viewWithBBox(diamond)
                .with(new HealthIntComponent(2))//TODO Destructable tiles
                .build();
        diamond.fillProperty().bind(
                Bindings.when(cell.getViewComponent().getParent().hoverProperty())
                        .then(Color.DARKGREEN)
                        .otherwise(Color.TRANSPARENT)
        );
        return cell;
    }

    @Spawns("moveTiles")
    public Entity spawnMoveTiles(SpawnData data) {
        var moveTile = FXGL.entityBuilder(data)
                .onClick(MovementService::moveTank)
                .type(ACTIONSELECTION)
                .viewWithBBox("Tile_move_selection.png")
                .build();
        MovementService.changeMountainLayer(moveTile);
        return moveTile;
    }

    @Spawns("attackTargetTiles")
    public Entity spawnAttackTargetTiles(SpawnData data) {
        Entity target = data.get("target");
        Entity attackingTank = data.get("attackingTank");
        String targetName = data.get("targetName");

        var view = FXGL.getAssetLoader().loadTexture(targetName); // manuell erstellen für hoverFunktion


        var entity = FXGL.entityBuilder(data)
                    .onClick(e -> ShootService.planShoot(target, attackingTank))
                    .type(ACTIONSELECTION)
                    .zIndex(target.getZIndex()+1)
                    .viewWithBBox(view)
                    .build();

        return entity;
    }
    @Spawns("attackTargetCity")
    public Entity spawnAttackTargetCity(SpawnData data) {
        Entity target = data.get("target");
        Entity attackingTank = data.get("attackingTank");
        String targetName = data.get("targetName");
        HealthIntComponent hpComp = target.getComponent(HealthIntComponent.class);


        return FXGL.entityBuilder(data)
                .onClick(e -> ShootService.planShoot(target, attackingTank))
                .type(ACTIONSELECTION)
                .zIndex(target.getZIndex()+1)
                .with(hpComp)
                .with(new AnimCityComponent(true))
                .build();
    }

    @Spawns("shell")
    public Entity spawnShell(SpawnData data) {
        Point2D targetLocation = data.get("targetLocation");

        return FXGL.entityBuilder(data)
                .type(SHELL)
                .viewWithBBox("shell.gif")
                .with(new ShellComponent(targetLocation))
                .build();
    }
}
