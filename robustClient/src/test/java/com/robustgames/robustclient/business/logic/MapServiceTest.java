package com.robustgames.robustclient.business.logic;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapServiceTest {

    @Test
    public void testIsoScreenToGrid_withOrigin() {
        Point2D screenPos = new Point2D(640, 0);
        Point2D result = MapService.isoScreenToGrid(screenPos);
        assertEquals(new Point2D(0, 0), result);
    }

    @Test
    public void testIsoScreenToGrid_withOffset() {
        Point2D screenPos = new Point2D(640, 192);
        Point2D result = MapService.isoScreenToGrid(screenPos);
        assertEquals(new Point2D(3, 3), result);
    }

    @Test
    public void testIsoScreenToGrid_largeCoordinates() {
        Point2D screenPos = new Point2D(320, 257);
        Point2D result = MapService.isoScreenToGrid(screenPos);
        assertEquals(new Point2D(1, 6), result);
    }

    @Test
    public void testIsoScreenToGrid_negativeCoordinates() {
        Point2D screenPos = new Point2D(832, 269);
        Point2D result = MapService.isoScreenToGrid(screenPos);
        assertEquals(new Point2D(5, 2), result);
    }

    @Test
    public void testIsoScreenToGrid_halfTileOverlap() {
        Point2D screenPos = new Point2D(960, 224);
        Point2D result = MapService.isoScreenToGrid(screenPos);
        assertEquals(new Point2D(6, 1), result);
    }

    @Test
    public void testIsoScreenToGrid_alternateConstructor() {
        Point2D result = MapService.isoScreenToGrid(576, 352);
        assertEquals(new Point2D(5, 6), result);
    }

/*
TODO: does not work at the moment

    @Test
    public void testShoot_targetTile() {
        Entity tank = FXGL.spawn("tank", 100, 100);
        tank.setType(EntityType.TANK);
        MapService.selectTank(tank);

        Entity tile = FXGL.spawn("tile", 200, 200);
        tile.setType(EntityType.TILE);

        MapService.shoot(tile);

        // Ensure shell is created, oriented, and explosion is triggered.
        // Use FXGL testing utilities to verify entities and components.
    }

    @Test
    public void testShoot_targetTank() {
        Entity tank = new Entity();
        tank.setPosition(new Point2D(100, 100));
        tank.setType(EntityType.TANK);
        MapService.selectTank(tank);

        Entity targetTank = new Entity();
        targetTank.setType(EntityType.TANK);
        targetTank.setPosition(new Point2D(200, 200));

        MapService.shoot(targetTank);

        // Ensure shell is created, oriented, and explosion is triggered.
        // Use FXGL testing utilities to verify entities and components.
    }

    @Test
    public void testShoot_targetCity() {
        Entity tank = new Entity();
        tank.setPosition(new Point2D(100, 100));
        tank.setType(EntityType.TANK);
        MapService.selectTank(tank);

        Entity city = new Entity();
        city.setType(EntityType.CITY);
        city.setPosition(new Point2D(300, 300));

        MapService.shoot(city);

        // Ensure shell is created, oriented, and explosion components are added.
        // Use FXGL assertions to verify explosion and timing.
    }

    @Test
    public void testShoot_nullTank() {
        Entity target = new Entity();
        target.setType(EntityType.TILE);
        target.setPosition(new Point2D(200, 200));

        MapService.shoot(target);

        // Verify that shell is not created when no tank is selected.
    }*/
}