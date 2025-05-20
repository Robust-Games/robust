package com.robustgames.robustclient.business.logic;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.robustgames.robustclient.business.entitiy.components.SelectableComponent;
import javafx.geometry.Point2D;
import java.util.HashSet;
import java.util.Set;

/**
 * Tracks the tile logic, currently in Orthographic 2D
 */
//TODO make it work with isometric
public class MapService {
    private static final int TILE_WIDTH_ORTH = 64;
    private static final int TILE_WIDTH_ISO = 128;
    private static final int TILE_HEIGHT = 64;
    private static final int ISO_TILE_ORIGIN_X = 5;
    private static final int ISO_TILE_ORIGIN_Y = 0; //aktuell 0 aber evtl. 1 in zukunft

    public static Point2D isoScreenToGrid(Point2D screenPos) {
        Point2D gridPos2D = new Point2D(screenPos.getX()/TILE_WIDTH_ISO, screenPos.getY()/TILE_HEIGHT);
        int x = (int) ((gridPos2D.getY() - ISO_TILE_ORIGIN_Y) + (gridPos2D.getX() - ISO_TILE_ORIGIN_X));
        int y = (int) ((gridPos2D.getY() - ISO_TILE_ORIGIN_Y) - (gridPos2D.getX() - ISO_TILE_ORIGIN_X));
        return new Point2D(x, y);
    }
    public static Point2D isoGridToScreen(Point2D position) {
        return isoGridToScreen(position.getX(), position.getY());
    }
    // isometric grid indices to screen (not world) coordinates, so it can draw stuff correctly
    public static Point2D isoGridToScreen(double gridX, double gridY) {
        double screenX = (ISO_TILE_ORIGIN_X * TILE_WIDTH_ISO) + (gridX - gridY) * (TILE_WIDTH_ISO /2.0);
        double screenY = ISO_TILE_ORIGIN_Y * TILE_HEIGHT + (gridX + gridY) * (TILE_HEIGHT /2.0);
        return new Point2D(screenX, screenY);
    }

    public static Point2D orthScreenToGrid(Point2D screenPos) {
        int gridX = (int) (screenPos.getX() / TILE_WIDTH_ORTH);
        int gridY = (int) (screenPos.getY() / TILE_HEIGHT);
        return new Point2D(gridX, gridY);
    }

    // grid indices to world coordinates for spawning stuff
    public static Point2D orthGridToScreen(double gridX, double gridY) {
        double worldX = gridX * TILE_WIDTH_ORTH;
        double worldY = gridY * TILE_HEIGHT;
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
    public static Set<Point2D> getTankNeighbours(Point2D tankPos){
        Set<Point2D> neighborCells = new HashSet<>();
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                if (x == 0 || y == 0) { //tank itself will not be selectable
                    continue;
                }
                neighborCells.add(new Point2D(tankPos.getX() + x, tankPos.getY()));
                neighborCells.add(new Point2D(tankPos.getX(), tankPos.getY() + y));
            }
        }

        System.out.println(neighborCells); //debug
        return neighborCells;
    }

}
