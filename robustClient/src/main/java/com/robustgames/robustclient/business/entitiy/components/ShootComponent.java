package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.robustgames.robustclient.business.logic.gameService.MapService;
import com.robustgames.robustclient.business.logic.Direction;
import com.robustgames.robustclient.business.logic.tankService.ShootService;
import javafx.geometry.Point2D;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.robustgames.robustclient.business.entitiy.EntityType.*;
import static com.robustgames.robustclient.business.logic.gameService.MapService.step;

public class ShootComponent extends Component {

    @Override
    public void onAdded() {
        if (entity.getComponent(APComponent.class).canUse(3)) {
            Point2D tankPos = MapService.isoScreenToGrid(entity.getCenter());
            // Aim in all four directions
            for (Direction dir : Direction.values()) {
                Point2D current = tankPos;
                while (true) {
                    current = step(current, dir); //takes a step in the chosen direction

                    if (!MapService.isOverTheEdge(current))
                        break;

                    Point2D posTile = MapService.isoGridToScreen(current);
                    List<Entity> tileList = getGameWorld().getEntitiesAt(posTile);

                    Point2D posEntity = posTile.subtract(64, 64);
                    List<Entity> entityList = getGameWorld().getEntitiesAt(posEntity);

                    if (!entityList.isEmpty()) {
                        if (entityList.size() > 1) {
                            System.err.println("ALERT! TWO ENTITIES AT THE SAME POSITION");
                        }
                        Entity target = entityList.getFirst();
                        ShootService.spawnAttackTarget(target, entity, false);
                        break;
                    } else if (!tileList.isEmpty()) {
                        ShootService.spawnAttackTarget(tileList.getFirst(), entity, false);
                    }
                }
            }
        }
        else getNotificationService().pushNotification("Not enough Action Points to shoot!");
    }
    @Override
    public void onRemoved() {
        getGameWorld().removeEntities(byType(ACTIONSELECTION));
    }
}
//ROBUST_DEBUG for tests maybe
/*                for (int i = 0; i < getGameScene().getViewport().getX(); i++) {
                    for (int j = 0; j < getGameScene().getViewport().getY(); j++) {
                        Point2D posEntity2 = new Point2D(i - 64, j - 64);
                        List<Entity> targetList2 = getGameWorld().getEntitiesAt(posEntity2);
                        if (targetList2.size() > 1) {
                            System.err.println("ALERT! TWO ENTITIES AT THE SAME POSITION");
                        }
                    }
                }*/