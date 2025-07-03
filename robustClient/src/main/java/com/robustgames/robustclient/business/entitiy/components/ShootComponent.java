package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import com.robustgames.robustclient.business.logic.gameService.MapService;
import com.robustgames.robustclient.business.logic.Direction;
import com.robustgames.robustclient.business.logic.tankService.RotateService;
import com.robustgames.robustclient.business.logic.tankService.ShootService;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.robustgames.robustclient.business.entitiy.EntityType.*;
import static com.robustgames.robustclient.business.logic.gameService.MapService.isoScreenToGrid;
import static com.robustgames.robustclient.business.logic.tankService.MovementService.step;

public class ShootComponent extends Component {
    private EventHandler<MouseEvent> aimHandler;
    @Override
    public void onAdded() {
        aimHandler = e -> {
            Point2D mousePos = new Point2D(e.getSceneX(), e.getSceneY()-100);
            //ROBUST_DEBUG Part One - for checking screen coordinates during aiming shot (Part Two in rotateTurret)
            /*            System.out.println(e.getSceneX() + " " + (e.getSceneY()-100));
            System.out.println(isoScreenToGrid(e.getSceneX(), e.getSceneY()-100) + " ");*/
            RotateService.rotateTurret(MapService.isoScreenToGrid(mousePos), entity);
        };

        FXGL.getSceneService().getCurrentScene().getContentRoot().addEventHandler(MouseEvent.MOUSE_MOVED, aimHandler);



        if (entity.getComponent(APComponent.class).canUse(3)) {
            List<Entity> entityList;
            List<Entity> tileList;
            Point2D tankPos = MapService.isoScreenToGrid(entity.getCenter());

            // Aim in all four directions
            for (Direction dir : Direction.values()) {
                Point2D currentGridPos = tankPos;
                while (true) {
                    currentGridPos = step(currentGridPos, dir); //takes a step in the chosen direction
                    if (!MapService.isWithinMapLimits(currentGridPos))
                        break;

                    Point2D posTile = MapService.isoGridToScreen(currentGridPos).subtract(64, -1);
                    tileList = getGameWorld().getEntitiesAt(posTile);

                    Point2D posEntity = MapService.isoGridToScreen(currentGridPos).subtract(64, 64);
                    entityList = getGameWorld().getEntitiesAt(posEntity);

                    if (!entityList.isEmpty()) {
                        if (entityList.size() > 1) {
                            throw new IllegalStateException("More than one entity found at target position "
                                    + currentGridPos);
                        }
                        Entity target = entityList.getFirst();
                        ShootService.spawnAttackTarget(target, entity);
                        break;
                    } else if (!tileList.isEmpty()) {
                        ShootService.spawnAttackTarget(tileList.getFirst(), entity);
                    }
                }
            }
        }
        else getNotificationService().pushNotification("Not enough Action Points to shoot!");
    }

    @Override
    public void onRemoved() {
        reset();
        if (aimHandler != null) {
            FXGL.getSceneService().getCurrentScene().getContentRoot().removeEventHandler(MouseEvent.MOUSE_MOVED, aimHandler);
            aimHandler = null;
        }
        getGameWorld().removeEntities(byType(ACTIONSELECTION));
    }

    public void reset(){
        TankDataComponent tankData = entity.getComponent(TankDataComponent.class);
        boolean hadTextures = false;

        // Remove turret texture
        Texture turret = tankData.getTurretTexture();
        if (turret != null && entity.getViewComponent().getChildren().contains(turret)) {
            entity.getViewComponent().removeChild(turret);
            tankData.setTurretTexture(null);
            hadTextures = true;
        }

        // Remove hull texture
        Texture tankHullTexture = tankData.getHullTexture();
        if (tankHullTexture != null && entity.getViewComponent().getChildren().contains(tankHullTexture)) {
            entity.getViewComponent().removeChild(tankHullTexture);
            tankData.setHullTexture(null);
            hadTextures = true;
        }

        if(!hadTextures){
            return;
        }

        if (tankData.getNewTankTexture() != tankData.getInitialTankTexture() && !entity.getViewComponent().getChildren().contains(tankData.getNewTankTexture())){
            entity.getViewComponent().addChild(tankData.getNewTankTexture());
        }
        else if (!entity.getViewComponent().getChildren().contains(tankData.getInitialTankTexture())){
            entity.getViewComponent().addChild(tankData.getInitialTankTexture());
        }
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