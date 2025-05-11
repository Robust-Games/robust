package com.robustgames.robustclient.business.logic;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.robustgames.robustclient.business.entitiy.components.SelectableComponent;
import javafx.geometry.Point2D;

/**
 * Tracks the tile logic, currently in Orthographic 2D
 */
//TODO make it work with isometric
public class MapService {
    private static final int TILE_WIDTH_ORTH = 64;
    private static final int TILE_WIDTH_ISO = 128;
    private static final int TILE_HEIGHT = 64;
    private static final int ISO_TILE_OFFSET_X = 5;
    private static final int ISO_TILE_OFFSET_Y = 0; //aktuell 0 aber evtl. 1 in zukunft

    public static Point2D isoScreenToGrid(Point2D screenPos) {
        System.out.println("x = " + screenPos.getX());
        System.out.println("y = " + screenPos.getY());
        Point2D gridPos = new Point2D(screenPos.getX()/TILE_WIDTH_ISO, screenPos.getY()/TILE_HEIGHT);
        System.out.println("gridPos = " + gridPos);
        int x = (int) gridPos.getX() - ISO_TILE_OFFSET_X;
        int y = (int) gridPos.getY() - ISO_TILE_OFFSET_Y;
        int gridX =  y+x;
        int gridY = y-x;
        return new Point2D(gridX, gridY);
    }
    // isometric grid indices to screen (not world) coordinates, so it can draw stuff correctly
    public static Point2D isoGridToScreen(double gridX, double gridY) {
        double worldX = (gridX - gridY) * (TILE_WIDTH_ISO /2.0); //08:34 im video
        double worldY = (gridX + gridY) * (TILE_HEIGHT/2.0);
        worldX += ISO_TILE_OFFSET_X * TILE_WIDTH_ISO; //damit die koordinaten ab Zentrum der Karte anfangen
        worldY += ISO_TILE_OFFSET_Y * TILE_WIDTH_ISO;
        return new Point2D(worldX, worldY);
    }



    public static Point2D orthScreenToGrid(Point2D screenPos) {
        int gridX = (int) (screenPos.getX() / TILE_WIDTH_ORTH);
        int gridY = (int) (screenPos.getY() / TILE_HEIGHT);
        return new Point2D(gridX, gridY);
    }

    // grid indices to world coordinates for spawning stuff
    public static Point2D orthGridToScreen(double gridX, double gridY) {
        double worldX = gridX * TILE_WIDTH_ORTH + TILE_WIDTH_ORTH / 2.0;
        double worldY = gridY * TILE_HEIGHT + TILE_HEIGHT / 2.0;
        return new Point2D(worldX, worldY);
    }
    public static Entity findSelectedTank(){
        for (Entity e : FXGL.getGameWorld().getEntities()) {
            if (e.hasComponent(SelectableComponent.class))
                return e;
        }
        return null;
    }
    public static void deSelectPreviousTank(){
        Entity tank = findSelectedTank();
        if (tank != null)
            tank.removeComponent(SelectableComponent.class);
    }
}
