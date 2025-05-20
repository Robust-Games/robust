package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.entity.component.Component;
import com.robustgames.robustclient.business.logic.MapService;
import javafx.geometry.Point2D;
import java.util.Set;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.robustgames.robustclient.business.logic.MapService.getTankNeighbours;

public class MovementComponent extends Component {

    @Override
    public void onAdded() {
        Point2D tankPos = MapService.isoScreenToGrid(entity.getCenter());
        System.out.println("tank pos CENTER: " + entity.getCenter()); //debug
        System.out.println("tank pos: " + tankPos); //debug

        Set<Point2D> neighborCells = getTankNeighbours(tankPos);
        for (Point2D neighbor : neighborCells) {
            Point2D pos1 = MapService.isoGridToScreen(neighbor);
            getGameWorld().spawn("moveTiles", pos1.getX()-64, pos1.getY()-64);
        }

    }

    @Override
    public void onRemoved() {
        super.onRemoved();
    }
}
