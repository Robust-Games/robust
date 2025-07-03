package com.robustgames.robustclient.business.entitiy.components;


import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.ui.ProgressBar;
import com.robustgames.robustclient.business.logic.gameService.MapService;
import com.robustgames.robustclient.business.logic.tankService.MovementService;
import javafx.geometry.Point2D;
import java.util.Set;

import static com.almasb.fxgl.dsl.FXGL.byType;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.robustgames.robustclient.business.entitiy.EntityType.ACTIONSELECTION;
import static java.lang.Math.abs;

public class MovementComponent extends Component {

    @Override
    public void onAdded() {
        ProgressBar healthBar = entity.getViewComponent().getChild(1, ProgressBar.class);
        Point2D tankPos = MapService.isoScreenToGrid(entity.getCenter());
        double apCost = 0;
        MovementService.changeMountainLayer(entity);
        Set<Point2D> moveTargets = MovementService.getTankMoveTargets(tankPos);
        if (moveTargets.isEmpty())
            return;
        for (Point2D target : moveTargets) {
            apCost = abs((tankPos.getX()+tankPos.getY()) - (target.getX()+target.getY()));
            System.out.println(target + " AP COST = " + apCost);
            Point2D pos1 = MapService.isoGridToScreen(target);
            System.out.println("B " + pos1);
            getGameWorld().spawn("moveTiles",
                    new SpawnData(pos1.getX()-64, pos1.getY()-64)
                            .put("tank", entity)
                            .put("apCost", apCost));
        }
        healthBar.setVisible(false);
        getGameWorld().spawn("rotateLeft",new SpawnData(entity.getPosition())
                .put("tank", entity));
        getGameWorld().spawn("rotateRight",new SpawnData(entity.getPosition())
                .put("tank", entity));
    }

    @Override
    public void onRemoved() {
        ProgressBar healthBar = entity.getViewComponent().getChild(1, ProgressBar.class);
        healthBar.setVisible(false);
        getGameWorld().removeEntities(byType(ACTIONSELECTION));
        MovementService.changeMountainLayer(entity);
    }
}
