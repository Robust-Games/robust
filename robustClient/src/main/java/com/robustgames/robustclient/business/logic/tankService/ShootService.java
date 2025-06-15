package com.robustgames.robustclient.business.logic.tankService;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.robustgames.robustclient.business.entitiy.components.APComponent;
import com.robustgames.robustclient.business.entitiy.components.ShootComponent;
import com.robustgames.robustclient.business.entitiy.components.TankDataComponent;
import com.robustgames.robustclient.business.entitiy.components.animations.AnimExplosionComponent;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.getGameTimer;
import static com.robustgames.robustclient.business.entitiy.EntityType.TILE;

public class ShootService {

    public static void shoot(Entity target, Entity tank) {
        if (tank == null || !tank.hasComponent(ShootComponent.class)) return;
        tank.getComponent(APComponent.class).damageFully();
        target.getComponent(HealthIntComponent.class).damage(1);
        //TODO Game Over

        tank.removeComponent(ShootComponent.class);

        if (target.getType() != TILE) {
            spawnShell(tank, target.getCenter());
        }
        else {
            spawnShell(tank, target.getPosition());
        }

        getGameTimer().runOnceAfter(() -> {
            if (target.getType() != TILE)
                target.addComponent(new AnimExplosionComponent(0,0));
            else
                target.addComponent(new AnimExplosionComponent(-64,-64));
        }, Duration.millis(target.distance(tank)));

        getGameTimer().runOnceAfter(() -> {
            target.removeComponent(AnimExplosionComponent.class);
            if (target.getComponent(HealthIntComponent.class).getValue()==0)
                target.removeFromWorld();
        }, Duration.millis(target.distance(tank)+1200)); //1200 = Explosion animation duration
    }

    public static void spawnAttackTarget(Entity target, Entity attackingTank) {
        Point2D targetPosition = target.getPosition();
        String targetName = "Tile_attack_selection.png";

        if (target.getType() != TILE) {
            List<Node> viewChildren = target.getViewComponent().getChildren();
            for (Node child : viewChildren) {
                if (child instanceof ImageView view) {
                    String url = view.getImage().getUrl();
                    String imageName = url.substring(url.lastIndexOf("/") + 1);
                    targetName = imageName.substring(0, imageName.lastIndexOf(".")) + "_attack.png";
                }
            }
        }
        else targetPosition = targetPosition.subtract(64,64);

        FXGL.spawnFadeIn("attackTargetTiles",
                new SpawnData(targetPosition)
                        .put("attackingTank", attackingTank)
                        .put("target", target)
                        .put("targetName", targetName)
                , Duration.millis(200)
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
