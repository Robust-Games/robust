package com.robustgames.robustclient.business.logic.tankService;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.action.ActionComponent;
import com.robustgames.robustclient.business.actions.ShootAction;
import com.robustgames.robustclient.business.entitiy.components.APComponent;
import com.robustgames.robustclient.business.entitiy.components.ShootComponent;
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

        spawnAttackTarget(target, tank, false);
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

        // Damage the target
        target.getComponent(HealthIntComponent.class).damage(1);

        // Spawn shell
        if (target.getType() != TILE) {
            spawnShell(tank, target.getPosition());
        }
        else {
            spawnShell(tank, target.getPosition().add(64, 1) );
        }

        // Add explosion animation after bullet travels there
        getGameTimer().runOnceAfter(() -> {
            if (target.getType() != TILE)
                target.addComponent(new AnimExplosionComponent(0,0));
            else
                target.addComponent(new AnimExplosionComponent(0,-64));
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

    public static void spawnAttackTarget(Entity target, Entity attackingTank, Boolean duringAction) {
        Point2D targetPosition = target.getPosition();
        String targetName = "Tile_attack_selection";

        if (target.getType() != TILE) {
            if (!duringAction || !target.isType(TANK)) {//Because the tank attack graphic looks weird if the target tank moves
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


}
