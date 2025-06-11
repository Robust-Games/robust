package com.robustgames.robustclient.business.entitiy.components;


import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.robustgames.robustclient.business.logic.MapService;
import javafx.geometry.Point2D;
import java.util.Set;

import static com.almasb.fxgl.dsl.FXGL.byType;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.robustgames.robustclient.business.entitiy.EntityType.ACTIONSELECTION;

public class MovementComponent extends Component {


    @Override
    public void onAdded() {
        Point2D tankPos = MapService.isoScreenToGrid(entity.getCenter());

        Set<Point2D> moveTargets = MapService.getTankMoveTargets(tankPos);
        if (moveTargets.isEmpty())
            return;
        for (Point2D target : moveTargets) {
            Point2D pos1 = MapService.isoGridToScreen(target);
            getGameWorld().spawn("moveTiles", pos1.getX()-64, pos1.getY()-64);
        }
    }

    @Override
    public void onRemoved() {
        getGameWorld().removeEntities(byType(ACTIONSELECTION));
    }
}
