package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.component.Component;
import com.robustgames.robustclient.business.logic.MapService;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


class MovementComponentTest {

//        /**
//         * Tests the onAdded method of the MovementComponent class.
//         * Verifies that the correct target positions are converted and entities are spawned for movement tiles.
//         */
    /*
    @Test //TODO: does not work at the moment
    void testOnAdded_SpawnsMoveTilesCorrectly() {
        // Arrange
        MovementComponent component = new MovementComponent();
        Entity entity = Mockito.mock(Component.class).getEntity();

        Point2D tankCenter = new Point2D(160, 160);
        Mockito.when(entity.getCenter()).thenReturn(tankCenter);

        Point2D tankPos = new Point2D(5, 5);
        Mockito.when(MapService.isoScreenToGrid(tankCenter)).thenReturn(tankPos);

        Set<Point2D> moveTargets = new HashSet<>();
        moveTargets.add(new Point2D(6, 5));
        moveTargets.add(new Point2D(5, 6));
        Mockito.when(MapService.getTankMoveTargets(tankPos)).thenReturn(moveTargets);

        GameWorld gameWorldMock = Mockito.mock(GameWorld.class);
        Mockito.when(com.almasb.fxgl.dsl.FXGL.getGameWorld()).thenReturn(gameWorldMock);

        // Act
        component.onAdded();

        // Assert
        for (Point2D target : moveTargets) {
            Point2D expectedScreenPos = MapService.isoGridToScreen(target);
            Mockito.verify(gameWorldMock).spawn(
                    ArgumentMatchers.eq("moveTiles"),
                    ArgumentMatchers.eq(expectedScreenPos.getX() - 64),
                    ArgumentMatchers.eq(expectedScreenPos.getY() - 64)
            );
        }
    }*/
}