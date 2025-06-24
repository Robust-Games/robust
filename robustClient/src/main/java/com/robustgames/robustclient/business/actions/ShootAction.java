package com.robustgames.robustclient.business.actions;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.action.Action;
import com.almasb.fxgl.net.Connection;
import com.robustgames.robustclient.application.RobustApplication;
import com.robustgames.robustclient.business.factories.BundleFactory;
import com.robustgames.robustclient.business.logic.gameService.MapService;
import com.robustgames.robustclient.business.logic.tankService.ShootService;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.robustgames.robustclient.business.entitiy.EntityType.ACTIONSELECTION;
import static com.robustgames.robustclient.business.entitiy.EntityType.TILE;
import static com.robustgames.robustclient.business.logic.tankService.ShootService.spawnAttackTarget;

public class ShootAction extends Action {
    private final Point2D targetGridPosition;
    private final Point2D targetScreenPosition;
    private final Entity originalTarget;

    /**
     * Creates a new ShootAction targeting the position of the entity
     *
     * @param target The entity that was targeted during planning. This entity's position
     *               is stored, but the entity itself is only used for showing
     */
    public ShootAction(Entity target) {
        this.originalTarget = target;
        boolean isTargetTile = target.isType(TILE);

        if (isTargetTile) {
            this.targetScreenPosition = target.getPosition();
        } else {
            this.targetScreenPosition = target.getCenter();
        }
        this.targetGridPosition = MapService.isoScreenToGrid(targetScreenPosition);
    }

    /**
     * Called when the action starts executing during turn processing.
     * 1. Sends a ShootAction bundle with all relevant target data to the server before the actual shot is processed.
     * 2. Shows the attack target indicator at the original planned target position.
     * 3. After a short delay (to compensate for possible tank movement), removes the attack selection overlay and determines
     * 4. the entity that is currently at the intended target position.
     * 5. If a target is found, the shooting logic is executed against the current target entity. Otherwise, an error is logged.
     * 6. Finally, after a brief animation delay, the action is marked as complete.
     * <p>
     * This method ensures the correct target is used, even if entities have moved since the action was originally planned,
     * and synchronizes the shooting event with the server.
     */
    @Override
    protected void onStarted() {
        // Network: Send ShootAction bundle to the server
        RobustApplication app = FXGL.<RobustApplication>getAppCast();
        Connection<Bundle> conn = app.getConnection();
        if (conn != null) {
            // The "entity" is the shooter, "currentTarget" is the intended target
            Bundle shootBundle = BundleFactory.createShootActionBundle(entity, originalTarget);
            conn.send(shootBundle);
        } else {
            System.out.println("No connection set – can't send shoot!");
        }

        spawnAttackTarget(originalTarget, entity, true);

        getGameTimer().runOnceAfter(() -> {
            getGameWorld().removeEntities(byType(ACTIONSELECTION));
            Entity currentTarget = findEntityAtPosition();

            if (currentTarget != null) {
                if (currentTarget.hasComponent(com.almasb.fxgl.dsl.components.HealthIntComponent.class)) {
                    ShootService.executeShoot(currentTarget, entity);
                } else {
                    System.err.println("WARN: Target has no HealthIntComponent! " + currentTarget.getType());
                }
            } else {
                System.err.println("No target found at position: " + targetGridPosition);
            }
            getGameTimer().runOnceAfter(this::setComplete, Duration.seconds(1.2));
        }, Duration.seconds(2));
    }

    /**
     * Finds the entity currently at the target position.
     * This method is called during action execution to determine what entity
     * is currently at the position that was targeted during planning. This allows the
     * action to correctly handle cases where entities move between planning and execution.
     *
     * @return The entity at the target position, or null if no entity is found
     */
    private Entity findEntityAtPosition() {
        Point2D entityPos = MapService.isoGridToScreen(targetGridPosition).subtract(64, 64);

        List<Entity> entitiesAtPosition = getGameWorld().getEntitiesAt(entityPos);
        if (!entitiesAtPosition.isEmpty()) {
            return entitiesAtPosition.getFirst();
        }

        // If no entity found at the entity position, check at the tile position
        List<Entity> tilesAtPosition = getGameWorld().getEntitiesAt(targetScreenPosition);
        if (!tilesAtPosition.isEmpty()) {
            return tilesAtPosition.getFirst();
        }

        return null;
    }

    @Override
    protected void onUpdate(double tpf) {

    }

    @Override
    protected void onQueued() {
        super.onQueued();
    }

    @Override
    protected void onCompleted() {
        super.onCompleted();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

}
