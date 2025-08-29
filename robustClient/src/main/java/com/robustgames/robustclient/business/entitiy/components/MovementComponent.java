/**
 * @author Ersin Yesiltas, Nico Steiner
 */
package com.robustgames.robustclient.business.entitiy.components;


import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.ui.ProgressBar;
import com.robustgames.robustclient.business.logic.gameService.MapService;
import com.robustgames.robustclient.business.logic.tankService.MovementService;
import javafx.geometry.Point2D;
import javafx.scene.Node;

import java.util.List;
import java.util.Set;

import static com.almasb.fxgl.dsl.FXGL.byType;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.robustgames.robustclient.business.entitiy.EntityType.ACTIONSELECTION;
import static java.lang.Math.abs;

public class MovementComponent extends Component {

    @Override
    public void onAdded() {
        ProgressBar healthBar = getHealthBar();
        Point2D tankPos = MapService.isoScreenToGrid(entity.getCenter());
        double apCost = 0;
        MovementService.changeMountainLayer(entity);
        Set<Point2D> moveTargets = MovementService.getTankMoveTargets(tankPos);
        if (moveTargets.isEmpty())
            return;
        for (Point2D target : moveTargets) {
            apCost = abs((tankPos.getX() + tankPos.getY()) - (target.getX() + target.getY()));
            Point2D pos1 = MapService.isoGridToScreen(target);
            getGameWorld().spawn("moveTiles",
                    new SpawnData(pos1.getX() - 64, pos1.getY() - 64)
                            .put("tank", entity)
                            .put("apCost", apCost));
        }
        assert healthBar != null;
        healthBar.setVisible(false);
        getGameWorld().spawn("rotateLeft", new SpawnData(entity.getPosition())
                .put("tank", entity));
        getGameWorld().spawn("rotateRight", new SpawnData(entity.getPosition())
                .put("tank", entity));
    }

    @Override
    public void onRemoved() {
        ProgressBar healthBar = getHealthBar();
        assert healthBar != null;
        healthBar.setVisible(true);
        getGameWorld().removeEntities(byType(ACTIONSELECTION));
        MovementService.changeMountainLayer(entity);
    }

    private ProgressBar getHealthBar() {
        List<Node> children = entity.getViewComponent().getChildren();
        for (Node child : children) {
            if (child instanceof ProgressBar) {
                return (ProgressBar) child;
            }
        }
        return null;
    }
}

