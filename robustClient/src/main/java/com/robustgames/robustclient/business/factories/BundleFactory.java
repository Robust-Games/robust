package com.robustgames.robustclient.business.factories;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.robustgames.robustclient.application.RobustApplication;
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
     * Creates a Bundle describing a move action from an entity to a target grid position.
     *
     * @param entity        The entity that is moving.
     * @param targetGridPos The grid position the entity is moving to.
     * @return A Bundle representing the move action.
     */
    public static Bundle createMoveActionBundle(Entity entity, Point2D targetGridPos) {
        Bundle bundle = new Bundle("MoveAction");
        int clientId = FXGL.<RobustApplication>getAppCast().getClientId();
        if (entity.hasComponent(IDComponent.class)) {
            bundle.put("clientId", clientId);
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
}