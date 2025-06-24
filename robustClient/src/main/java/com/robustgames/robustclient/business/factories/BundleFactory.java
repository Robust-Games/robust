package com.robustgames.robustclient.business.factories;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.entity.Entity;
import com.robustgames.robustclient.business.entitiy.components.APComponent;
import com.robustgames.robustclient.business.entitiy.components.IDComponent;
import com.robustgames.robustclient.business.entitiy.components.MovementComponent;
import com.robustgames.robustclient.business.entitiy.components.RotateComponent;
import com.robustgames.robustclient.business.entitiy.components.ShootComponent;
import javafx.geometry.Point2D;

/**
 * Utility factory for serializing and deserializing game entities and their actions into Bundles
 * for network communication between client and server in the robust client architecture.
 */
public class BundleFactory {

    /**
     * Serializes an Entity into a Bundle for network transmission.
     *
     * @param entity The entity to serialize.
     * @return A Bundle containing the entity's data (id, type, position, AP, direction, action status).
     */
    public static Bundle entityToBundle(Entity entity) {
        Bundle bundle = new Bundle("Entity");
        if (entity.hasComponent(IDComponent.class)) {
            bundle.put("id", entity.getComponent(IDComponent.class).getId());
        } else {
            bundle.put("id", -1);
        }
        bundle.put("type", entity.getType().toString());
        Point2D gridPos = entity.getPosition();
        bundle.put("posX", gridPos.getX());
        bundle.put("posY", gridPos.getY());
        if (entity.hasComponent(APComponent.class)) {
            APComponent ap = entity.getComponent(APComponent.class);
            bundle.put("ap", ap.getCurrentAP());
            bundle.put("maxAP", 5);
        }
        if (entity.hasComponent(RotateComponent.class)) {
            String facing = getTankDirection(entity);
            bundle.put("direction", facing);
        }
        bundle.put("canMove", entity.hasComponent(MovementComponent.class));
        bundle.put("canShoot", entity.hasComponent(ShootComponent.class));
        return bundle;
    }

    /**
     * Updates an existing Entity's state from a Bundle received over the network.
     *
     * @param entity The entity to update.
     * @param bundle The Bundle containing updated state information.
     */
    public static void updateEntityFromBundle(Entity entity, Bundle bundle) {
        entity.setPosition(bundle.get("posX"), bundle.get("posY"));
        if (entity.hasComponent(APComponent.class)) {
            int ap = bundle.get("ap");
            entity.getComponent(APComponent.class).setCurrentAP(ap);
        }
    }

    /**
     * Creates a Bundle describing a move action from an entity to a target grid position.
     *
     * @param entity        The entity that is moving.
     * @param targetGridPos The grid position the entity is moving to.
     * @return A Bundle representing the move action.
     */
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

    /**
     * Creates a bundle representing a rotate action, to be sent to the server.
     * The bundle includes the unique entity ID and the new direction (as texture name without ".png").
     *
     * @param entity           The entity being rotated (must have an IDComponent).
     * @param directionTexture The texture filename representing the new direction, e.g., "tank_top_left.png".
     * @return A Bundle containing the rotate action data.
     */
    public static Bundle createRotateActionBundle(Entity entity, String directionTexture) {
        Bundle bundle = new Bundle("RotateAction");
        if (entity.hasComponent(IDComponent.class)) {
            bundle.put("entityId", entity.getComponent(IDComponent.class).getId());
        } else {
            bundle.put("entityId", -1);
        }
        // Remove ".png" for compactness and consistency with direction format
        String dir = directionTexture.endsWith(".png")
                ? directionTexture.substring(0, directionTexture.length() - 4)
                : directionTexture;
        bundle.put("direction", dir);
        return bundle;
    }

    /**
     * Creates a Bundle describing a shoot action from a shooter to a target entity.
     *
     * @param shooter The entity performing the shoot action.
     * @param target  The target entity.
     * @return A Bundle representing the shoot action.
     */
    public static Bundle createShootActionBundle(Entity shooter, Entity target) {
        Bundle bundle = new Bundle("ShootAction");
        bundle.put("shooterId", shooter.hasComponent(IDComponent.class) ? shooter.getComponent(IDComponent.class).getId() : -1);
        bundle.put("targetId", target.hasComponent(IDComponent.class) ? target.getComponent(IDComponent.class).getId() : -1);
        return bundle;
    }

    /**
     * Determines the current facing direction of a tank entity based on the filename of its image.
     *
     * @param tank The tank entity.
     * @return The direction as a string (e.g. "tank_top_left"), or an empty string if not found.
     */
    public static String getTankDirection(Entity tank) {
        for (javafx.scene.Node e : tank.getViewComponent().getChildren()) {
            if (e instanceof javafx.scene.image.ImageView iv) {
                String url = iv.getImage().getUrl();
                if (url.contains("tank")) {
                    String file = url.substring(url.lastIndexOf("/") + 1);
                    return file.replace(".png", "");
                }
            }
        }
        return "";
    }
}