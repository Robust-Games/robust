package com.robustgames.robustclient.business.logic.tankService;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.action.ActionComponent;
import com.almasb.fxgl.texture.Texture;
import com.robustgames.robustclient.business.actions.ShootAction;
import com.robustgames.robustclient.business.entitiy.components.APComponent;
import com.robustgames.robustclient.business.entitiy.components.ShootComponent;
import com.robustgames.robustclient.business.entitiy.components.TankDataComponent;
import com.robustgames.robustclient.business.entitiy.components.animations.AnimExplosionComponent;
import com.robustgames.robustclient.business.entitiy.components.animations.AnimMountainComponent;
import com.robustgames.robustclient.business.entitiy.components.animations.AnimTankTurret;
import com.robustgames.robustclient.business.logic.gameService.GameState;
import com.robustgames.robustclient.business.logic.gameService.MapService;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.getGameTimer;
import static com.robustgames.robustclient.business.entitiy.EntityType.*;

public class ShootService {

    public static void planShoot(Entity target, Entity tank) {
        if (tank == null || !tank.hasComponent(ShootComponent.class)) return;

        // Use action points when planning the shot
        tank.getComponent(APComponent.class).damageFully();

        ActionComponent ac = tank.getComponent(ActionComponent.class);
        ac.addAction(new ShootAction(target));
        ac.pause();

        tank.removeComponent(ShootComponent.class);

    }

    /**
     * This method executes the shot at the target.
     * It's meant to be called from ShootAction during turn execution.
     * 
     * @param target The target entity to shoot at
     * @param tank The tank entity doing the shooting
     */
    public static void executeShoot(Entity target, Entity tank) {
        if (tank == null) return;

        if (target.isType(TANK)) {
            int damage = calculateDirectionalDamage(target, tank);
            target.getComponent(HealthIntComponent.class).damage(damage);
        } else {
            target.getComponent(HealthIntComponent.class).damage(1);
        }

        // Spawn shell
        if (target.getType() != TILE) {
            spawnShell(tank, target.getCenter());
        }
        else {
            spawnShell(tank, target.getPosition().add(64, 1) );
        }
        FXGL.play("tank_shoot.wav");

        // Add explosion animation after bullet travels there
        getGameTimer().runOnceAfter(() -> {
            if (target.getType() != TILE) {
                if (target.getType() == TANK) {
                    FXGL.play("hit_tile.wav"); //TODO Change sound
                } else if (target.getType() == MOUNTAIN) {
                    FXGL.play("hit_mountain.wav");
                } else if (target.getType() == CITY) {
                    FXGL.play("hit_city.wav");
                }
                target.addComponent(new AnimExplosionComponent(0, 0));
            } else {
                target.addComponent(new AnimExplosionComponent(0, -64));
                FXGL.play("hit_tile.wav");
            }
        }, Duration.millis(target.distance(tank)));

        // Remove explosion and the target (if it dies) after animation completes
        getGameTimer().runOnceAfter(() -> {
            target.removeComponent(AnimExplosionComponent.class);
            tank.removeComponent(AnimTankTurret.class);
            if (target.getComponent(HealthIntComponent.class).getValue()<=0) {
                if (target.getType() == MOUNTAIN) {
                    target.addComponent(new AnimMountainComponent());
                }
                else if (target.isType(TANK)||target.isType(CITY)){
                    getGameTimer().runOnceAfter(() -> {
                        target.removeFromWorld();
                        GameState.gameOver();
                    }, Duration.millis(1500));
                }
                else {
                    target.removeFromWorld();
                }
            }
            }, Duration.millis(target.distance(tank)+1200)); //1200 = Explosion animation duration
    }

    public static void spawnAttackTarget(Entity target, Entity attackingTank, boolean duringAction) {
        Point2D targetPosition = target.getPosition();
        String targetName = "Tile_attack_selection";

        if (target.getType() != TILE) {
            if (target.getType() == TANK) {

                TankDataComponent tankData = target.getComponent(TankDataComponent.class);
                String initialTankView;
                if (duringAction) {
                    return;
                }
                else
                {
                    initialTankView = tankData.getInitialTankView();
                }
                targetName = initialTankView.substring(0, initialTankView.lastIndexOf(".")) + "_attack";
            } else {
                List<Node> viewChildren = target.getViewComponent().getChildren();
                for (Node child : viewChildren) {
                    if (child instanceof ImageView view) {
                        String url = view.getImage().getUrl();
                        String imageName = url.substring(url.lastIndexOf("/") + 1);
                        targetName = imageName.substring(0, imageName.lastIndexOf(".")) + "_attack";
                    }
                }
            }
        }

        else if (target.getType() == TILE) {
            targetPosition = targetPosition.subtract(0, 63);
        }

        if (target.getType() == CITY) {
            FXGL.spawn("attackTargetCity",
                    new SpawnData(targetPosition)
                            .put("attackingTank", attackingTank)
                            .put("target", target)
                            .put("targetName", targetName));
        }
        else
            FXGL.spawn("attackTargetTiles",
                    new SpawnData(targetPosition)
                            .put("attackingTank", attackingTank)
                            .put("target", target)
                            .put("targetName", targetName)
            );
    }

    public static void spawnShell(Entity tank, Point2D targetScreenPosition) {
        FXGL.spawnFadeIn("shell",
                new SpawnData(tank.getCenter())
                        .put("tank", tank)
                        .put("targetLocation", targetScreenPosition)
                , Duration.millis(10)
        );
    }

    /**
     * Calculates the damage based on which direction the target tank is hit from.
     * - 3 damage when hit from the back
     * - 2 damage when hit from the side
     * - 1 damage when hit from the front
     *
     * @param targetTank The tank being hit
     * @param shooterTank The tank doing the shooting
     * @return The amount of damage to apply
     */
    private static int calculateDirectionalDamage(Entity targetTank, Entity shooterTank) {
        // Get the texture name to determine the target tank's orientation
 /*       TankDataComponent targetData = targetTank.getComponent(TankDataComponent.class);
        Texture targetTexture = targetData.getInitialTankTexture();
        String targetOrientation = targetTexture.getImage().getUrl();
        targetOrientation = targetOrientation.substring(targetOrientation.lastIndexOf("/") + 1);

        // Calculate the angle between the two tanks
        Point2D targetPos = targetTank.getCenter();
        Point2D shooterPos = shooterTank.getCenter();
        Point2D direction = targetPos.subtract(shooterPos);

        // Convert to grid coordinates for consistent angle calculation
        Point2D targetGridPos = MapService.isoScreenToGrid(targetPos);
        Point2D shooterGridPos = MapService.isoScreenToGrid(shooterPos);
        Point2D gridDirection = targetGridPos.subtract(shooterGridPos);

        // Determine which side of the target tank was hit based on its orientation and the direction of the shot
        switch (targetOrientation) {
            case "tank_top_right.png" -> {
                // Tank facing top-right
                if (gridDirection.getX() <= 0 && gridDirection.getY() >= 0) {
                    return 1; // Front hit
                } else if ((gridDirection.getX() > 0 && gridDirection.getY() > 0) ||
                        (gridDirection.getX() < 0 && gridDirection.getY() < 0)) {
                    return 2; // Side hit
                } else {
                    return 3; // Back hit
                }
                // Tank facing top-right
            }
            case "tank_top_left.png" -> {
                // Tank facing top-left
                if (gridDirection.getX() >= 0 && gridDirection.getY() >= 0) {
                    return 1; // Front hit
                } else if ((gridDirection.getX() < 0 && gridDirection.getY() > 0) ||
                        (gridDirection.getX() > 0 && gridDirection.getY() < 0)) {
                    return 2; // Side hit
                } else {
                    return 3; // Back hit
                }
                // Tank facing top-left
            }
            case "tank_down_left.png" -> {
                // Tank facing down-left
                if (gridDirection.getX() >= 0 && gridDirection.getY() <= 0) {
                    return 1; // Front hit
                } else if ((gridDirection.getX() > 0 && gridDirection.getY() > 0) ||
                        (gridDirection.getX() < 0 && gridDirection.getY() < 0)) {
                    return 2; // Side hit
                } else {
                    return 3; // Back hit
                }
                // Tank facing down-left
            }
            case "tank_down_right.png" -> {
                // Tank facing down-right
                if (gridDirection.getX() <= 0 && gridDirection.getY() <= 0) {
                    return 1; // Front hit
                } else if ((gridDirection.getX() < 0 && gridDirection.getY() > 0) ||
                        (gridDirection.getX() > 0 && gridDirection.getY() < 0)) {
                    return 2; // Side hit
                } else {
                    return 3; // Back hit
                }
            }
        }*/

        // Default to 1 damage if orientation can't be determined
        return 1;
    }
}
