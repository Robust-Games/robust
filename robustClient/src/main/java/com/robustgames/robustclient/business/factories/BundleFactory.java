package com.robustgames.robustclient.business.factories;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.entity.Entity;
import com.robustgames.robustclient.business.entitiy.components.APComponent;
import com.robustgames.robustclient.business.entitiy.components.IDComponent;
import com.robustgames.robustclient.business.entitiy.components.MovementComponent;
import com.robustgames.robustclient.business.entitiy.components.RotateComponent;
import com.robustgames.robustclient.business.entitiy.components.ShootComponent;
import javafx.geometry.Point2D;

public class BundleFactory {

    // Entity zu Bundle Serialisierung
    public static Bundle entityToBundle(Entity entity) {
        Bundle bundle = new Bundle("Entity");

        // Entity-Identität über IdComponent
        if (entity.hasComponent(IDComponent.class)) {
            bundle.put("id", entity.getComponent(IDComponent.class).getId());
        } else {
            bundle.put("id", -1); // oder optional: throw new IllegalStateException(...)
        }
        // Typ
        bundle.put("type", entity.getType().toString());

        // Position
        Point2D gridPos = entity.getPosition();
        bundle.put("posX", gridPos.getX());
        bundle.put("posY", gridPos.getY());

        // HP/AP
        if (entity.hasComponent(APComponent.class)) {
            APComponent ap = entity.getComponent(APComponent.class);
            bundle.put("ap", ap.getCurrentAP());
            bundle.put("maxAP", 5); // Wenn dynamisch, dann ap.getMaxAP()
        }

        // Aktuelle Richtung als Bild-Name
        if (entity.hasComponent(RotateComponent.class)) {
            String facing = getTankDirection(entity);
            bundle.put("direction", facing);
        }

        // Status für Turnübergabe
        bundle.put("canMove", entity.hasComponent(MovementComponent.class));
        bundle.put("canShoot", entity.hasComponent(ShootComponent.class));

        return bundle;
    }

    // Bundle zu Entity updatet bestehende Entity
    public static void updateEntityFromBundle(Entity entity, Bundle bundle) {
        entity.setPosition(bundle.get("posX"), bundle.get("posY"));

        // AP direkt setzen
        if (entity.hasComponent(APComponent.class)) {
            int ap = bundle.get("ap");
            entity.getComponent(APComponent.class).setCurrentAP(ap);
        }
    }

    // Move-Action Bundle
    public static Bundle createMoveActionBundle(Entity entity, Point2D targetGridPos) {
        Bundle bundle = new Bundle("MoveAction");
        if (entity.hasComponent(IDComponent.class)) {
            bundle.put("entityId", entity.getComponent(IDComponent.class).getId());
        } else {
            bundle.put("entityId", -1);
        }
        bundle.put("fromX", entity.getPosition().getX());
        bundle.put("fromY", entity.getPosition().getY());
        bundle.put("toX", targetGridPos.getX());
        bundle.put("toY", targetGridPos.getY());
        return bundle;
    }

    // Shoot-Action Bundle
    public static Bundle createShootActionBundle(Entity shooter, Entity target) {
        Bundle bundle = new Bundle("ShootAction");
        bundle.put("shooterId", shooter.hasComponent(IDComponent.class) ? shooter.getComponent(IDComponent.class).getId() : -1);
        bundle.put("targetId", target.hasComponent(IDComponent.class) ? target.getComponent(IDComponent.class).getId() : -1);
        return bundle;
    }

    // Tank-Richtung als Bildname
    public static String getTankDirection(Entity tank) {
        for (javafx.scene.Node e : tank.getViewComponent().getChildren()) {
            if (e instanceof javafx.scene.image.ImageView iv) {
                String url = iv.getImage().getUrl();
                if (url.contains("tank")) {
                    String file = url.substring(url.lastIndexOf("/") + 1); // z.B. "tank_top_left.png"
                    return file.replace(".png", "");
                }
            }
        }
        return "";
    }
}