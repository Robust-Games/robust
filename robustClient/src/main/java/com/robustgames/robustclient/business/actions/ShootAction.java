package com.robustgames.robustclient.business.actions;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.action.Action;
import com.robustgames.robustclient.business.entitiy.components.animations.AnimTankTurret;
import com.robustgames.robustclient.business.logic.gameService.MapService;
import com.robustgames.robustclient.business.entitiy.components.TankDataComponent;
import com.robustgames.robustclient.business.logic.gameService.MapService;
import com.robustgames.robustclient.business.logic.tankService.RotateService;
import com.robustgames.robustclient.business.logic.tankService.ShootService;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.robustgames.robustclient.business.entitiy.EntityType.*;
import static com.robustgames.robustclient.business.logic.tankService.ShootService.spawnAttackTarget;

public class ShootAction extends Action {
    private final Point2D targetGridPosition;
    private final Point2D targetScreenPosition;
    private final Entity originalTarget;

    /**
     * Creates a new ShootAction targeting the position of the entity
     * @param target The entity that was targeted during planning. This entity's position
     *               is stored, but the entity itself is only used for showing
     */
    public ShootAction(Entity target) {
        this.originalTarget = target;

        this.targetScreenPosition = target.getPosition();

        if (target.getType() == CITY) {
            this.targetGridPosition = MapService.isoScreenToGrid(target.getPosition().add(64, 64));
        }
        else if (target.getType() == TILE) {
            this.targetGridPosition = MapService.isoScreenToGrid(target.getPosition().add(64, 32));
        }
        else
            this.targetGridPosition = MapService.isoScreenToGrid(target.getCenter());
    }

    /**
     * Called when the action starts executing during turn processing.
     * 1. Shows the target
     * 2. After a short delay to compensate for tank movement, removes the AttackTile and finds the entity currently
     * at target position
     * 3. Executes the shoot action on the current entity at that position (or tile if tank is not found)
     */
    @Override
    protected void onStarted() {
        spawnAttackTarget(originalTarget, entity);

        RotateService.rotateTurret(targetGridPosition, entity);

        getGameTimer().runOnceAfter(() -> {
            getGameWorld().removeEntities(byType(ACTIONSELECTION));
            Entity currentTarget;
            if (originalTarget.isType(TILE)) {
                currentTarget = findEntityAtPosition(originalTarget, true);
            }
            else if (originalTarget.isType(TANK)) {
                currentTarget = findEntityAtPosition(originalTarget, false);
            }
            else currentTarget = originalTarget;

            if (currentTarget != null) {
                entity.addComponent(new AnimTankTurret(entity.getComponent(TankDataComponent.class).getTurretTextureName()));
                ShootService.executeShoot(currentTarget, entity);
            } else {
                System.err.println("No target found at position: " + targetGridPosition);
            }
            getGameTimer().runOnceAfter(this::setComplete, Duration.seconds(1.2));
        }, Duration.seconds(2));
    }

    /**
     * Finds the entity at the target position.
     * This method is called during action execution to determine what entity
     * is currently at the position which was initially targeted during planning. This allows the
     * action to correctly handle cases where entities move between planning and execution.
     *
     * @return The entity at the target position, or null if no entity is found
     */
    private Entity findEntityAtPosition(Entity targetEntity, Boolean TargetIsTile) {
        if (TargetIsTile) {
            Point2D posEntity = targetScreenPosition.subtract(0, 65);
            List<Entity> entityList = getGameWorld().getEntitiesAt(posEntity);
            if (!entityList.isEmpty()) {
                if (entityList.size() > 1) {
                    throw new IllegalStateException("More than one entity found at target position "
                            + targetGridPosition);
                }
                return entityList.getFirst();
            }
            else return targetEntity;

        }
        else{
            if (targetEntity.getPosition().equals(targetScreenPosition))
                return targetEntity;
            else {
                Point2D posTile = (targetScreenPosition).add(0, 65);
                List<Entity> tileList = getGameWorld().getEntitiesAt(posTile);
                if (!tileList.isEmpty()) {
                    if (tileList.size() > 1) {
                        throw new IllegalStateException("More than one entity found at target position "
                                + targetGridPosition);
                    }
                    return tileList.getFirst();
                }
                else throw new IllegalStateException("No Tile found at target position @findEntityAtPosition - ShootAction");
            }
        }
    }

    @Override
    protected void onUpdate(double tpf) {

    }
    @Override
    protected void onCompleted() {
        super.onCompleted();
        entity.getComponent(TankDataComponent.class).resetBeforeTurn();
    }

}
