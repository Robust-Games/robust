/**
 * @author Burak Altun, Ersin Yesiltas, Nico Steiner
 */
package com.robustgames.robustclient.business.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.texture.Texture;
import com.robustgames.robustclient.business.entitiy.components.MovementComponent;
import com.robustgames.robustclient.business.entitiy.components.RotateComponent;
import com.robustgames.robustclient.business.entitiy.components.ShellComponent;
import com.robustgames.robustclient.business.entitiy.components.animations.AnimCityComponent;
import com.robustgames.robustclient.business.entitiy.components.animations.AnimZero;
import com.robustgames.robustclient.business.logic.tankService.MovementService;
import com.robustgames.robustclient.business.logic.tankService.ShootService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;

import static com.robustgames.robustclient.business.entitiy.EntityType.*;

/**
 * For destructible entities on our map. May later include tiles
 */
public class MapFactory implements EntityFactory {
    @Spawns("Background")
    public Entity spawnBackground(SpawnData data) {
        return FXGL.entityBuilder(data)
                .view("background.png")
                .with(new IrremovableComponent())
                .with(new AnimZero())
                .zIndex(-100)
                .build();
    }

    @Spawns("MapBorder")
    public Entity spawnBorder(SpawnData data) {
        return FXGL.entityBuilder(data)
                .view("mapBorder.png")
                .with(new IrremovableComponent())
                .zIndex(-99)
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

    @Spawns("floorTile")
    public Entity spawnFloor(SpawnData data) {
        var hpComp = new HealthIntComponent(2);
        Texture floorTexture = FXGL.getAssetLoader().loadTexture("floor_tile1.png");

        //ObjectProperty<Image> imageProp = new SimpleObjectProperty<>(FXGL.getAssetLoader().loadImage("floor_tile1.png"));
        //floorTexture.imageProperty().bind(imageProp);

        hpComp.valueProperty().addListener((obs, old, newHP) -> {
            if (newHP.intValue() > 1) {
                floorTexture.set(FXGL.getAssetLoader().loadTexture("floor_tile1.png"));
            } else if (newHP.intValue() == 1)
                floorTexture.set(FXGL.getAssetLoader().loadTexture("floor_tile2.png"));
            else if (newHP.intValue() <= 0)
                System.err.println("A");
            //floorTexture.imageProperty().unbind();
        });

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
            } else if (wasHovered)
                floor.getViewComponent().removeChild(hoverTexture);
        });

        return floor;
    }

    @Spawns("floorTileMountain")
    public Entity spawnMountainFloor(SpawnData data) {
        var hpComp = new HealthIntComponent(2);
        Texture floorTexture = FXGL.getAssetLoader().loadTexture("floorTileMountain1.png");

        ObjectProperty<Image> imageProp = new SimpleObjectProperty<>(FXGL.getAssetLoader().loadImage("floorTileMountain1.png"));
/*        floorTexture.imageProperty().bind(imageProp);
        hpComp.valueProperty().addListener((obs, old, newHP) -> {
            if (newHP.intValue() > 1) {
                imageProp.set(FXGL.getAssetLoader().loadImage("floorTileMountain1.png"));
            }else if (newHP.intValue() == 1)
                imageProp.set(FXGL.getAssetLoader().loadImage("floorTileMountain2.png"));
            else if (newHP.intValue() == 0)
                floorTexture.imageProperty().unbind();
        });*/

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
            } else if (wasHovered)
                floorMountain.getViewComponent().removeChild(hoverTexture);
        });

        return floorMountain;
    }

    @Spawns("moveTiles")
    public Entity spawnMoveTiles(SpawnData data) {
        double apCost = data.get("apCost");
        Texture floorTexture = FXGL.getAssetLoader().loadTexture("Tile_move_selection.png");
        Texture hoverTexture = FXGL.getAssetLoader().loadTexture("Tile_move_selection.png");
        Tooltip tooltip = new Tooltip("AP Cost: " + (int) apCost);

        var moveTile = FXGL.entityBuilder(data)
                .onClick(MovementService::moveTank)
                .type(ACTIONSELECTION)
                .viewWithBBox(floorTexture)
                .build();

        hoverTexture.mouseTransparentProperty().setValue(true);
        floorTexture.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
            if (isNowHovered) {
                Point2D screenCoords = floorTexture.localToScreen(0, 0);
                tooltip.show(floorTexture, screenCoords.getX(), screenCoords.getY() + 30);
                if (!moveTile.getViewComponent().getChildren().contains(hoverTexture)) {
                    moveTile.getViewComponent().addChild(hoverTexture);
                }
            } else if (wasHovered) {
                tooltip.hide();
                moveTile.getViewComponent().removeChild(hoverTexture);
            }
        });

        MovementService.changeMountainLayer(moveTile);
        return moveTile;
    }

    @Spawns("attackTargetTiles")
    public Entity spawnAttackTargetTiles(SpawnData data) {
        Entity target = data.get("target");
        Entity attackingTank = data.get("attackingTank");
        String targetName = data.get("targetName");
        Texture floorTexture;
        Texture hoverTexture;

        if (target.getType() != TILE) {
            floorTexture = FXGL.getAssetLoader().loadTexture(targetName + ".png");
        } else {
            floorTexture = FXGL.getAssetLoader().loadTexture("Tile_attack_selection.png");
        }

        var attackTile = FXGL.entityBuilder(data)
                .onClick(e -> ShootService.planShoot(target, attackingTank))
                .type(ACTIONSELECTION)
                .zIndex(target.getZIndex() + 1)
                .viewWithBBox(floorTexture)
                .build();

        if (target.getType() == TILE) {
            hoverTexture = FXGL.getAssetLoader().loadTexture("Tile_attack_selection.png");
        } else
            hoverTexture = FXGL.getAssetLoader().loadTexture(targetName + "_hover.png");
        hoverTexture.mouseTransparentProperty().setValue(true);

        floorTexture.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
            if (isNowHovered) {
                if (!attackTile.getViewComponent().getChildren().contains(hoverTexture)) {
                    attackTile.getViewComponent().addChild(hoverTexture);
                }
            } else if (wasHovered)
                attackTile.getViewComponent().removeChild(hoverTexture);
        });

        MovementService.changeMountainLayer(attackTile);

        return attackTile;
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
                .zIndex(target.getZIndex() + 1)
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

    @Spawns("rotateRight")
    public Entity spawnRightArrow(SpawnData data) {
        Entity tank = data.get("tank");
        Texture arrowTexture = FXGL.getAssetLoader().loadTexture("Tank_rotate_right.png");
        Texture hoverTexture = FXGL.getAssetLoader().loadTexture("Tank_rotate_right_selection.png");
        Tooltip tooltip = new Tooltip("AP Cost: 1");

        var rotateLeftButton = FXGL.entityBuilder(data)
                .onClick(e -> {
                    tank.getComponent(RotateComponent.class).rotateRight();
                    tank.removeComponent(MovementComponent.class);
                    tank.addComponent(new MovementComponent());
                })
                .type(ACTIONSELECTION)
                .viewWithBBox(arrowTexture)
                .build();

        arrowTexture.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
            if (isNowHovered) {
                Point2D screenCoords = arrowTexture.localToScreen(0, 0);
                tooltip.show(arrowTexture, screenCoords.getX(), screenCoords.getY() - 30);
                arrowTexture.set(hoverTexture);
            } else if (wasHovered) {
                tooltip.hide();
                arrowTexture.set(FXGL.getAssetLoader().loadTexture("Tank_rotate_right.png"));
            }
        });

        MovementService.changeMountainLayer(rotateLeftButton);
        return rotateLeftButton;
    }

    @Spawns("rotateLeft")
    public Entity spawnLeftArrow(SpawnData data) {
        Entity tank = data.get("tank");
        Texture arrowTexture = FXGL.getAssetLoader().loadTexture("Tank_rotate_left.png");
        Texture hoverTexture = FXGL.getAssetLoader().loadTexture("Tank_rotate_left_selection.png");
        Tooltip tooltip = new Tooltip("AP Cost: 1");

        var rotateLeftButton = FXGL.entityBuilder(data)
                .onClick(e -> {
                    tank.getComponent(RotateComponent.class).rotateLeft();
                    tank.removeComponent(MovementComponent.class);
                    tank.addComponent(new MovementComponent());
                })
                .type(ACTIONSELECTION)
                .viewWithBBox(arrowTexture)
                .build();

        arrowTexture.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
            if (isNowHovered) {
                Point2D screenCoords = arrowTexture.localToScreen(0, 0);
                tooltip.show(arrowTexture, screenCoords.getX() + 64, screenCoords.getY() - 30);
                arrowTexture.set(hoverTexture);
            } else if (wasHovered) {
                tooltip.hide();
                arrowTexture.set(FXGL.getAssetLoader().loadTexture("Tank_rotate_left.png"));
            }
        });

        MovementService.changeMountainLayer(rotateLeftButton);
        return rotateLeftButton;
    }
}
