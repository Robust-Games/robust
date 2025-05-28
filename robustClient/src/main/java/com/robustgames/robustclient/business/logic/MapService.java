package com.robustgames.robustclient.business.logic;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.robustgames.robustclient.business.entitiy.EntityType;
import com.robustgames.robustclient.business.entitiy.components.SelectableComponent;
import javafx.geometry.Point2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.robustgames.robustclient.business.logic.Direction;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

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

    /**
     * Converts a screen-space point from isometric coordinates to grid coordinates.
     * This transformation is based on the specified tile dimensions and origin offsets.
     *
     * @param screenPos the position in screen-space isometric coordinates to be transformed
     * @return the corresponding position in grid coordinates
     */
    public static Point2D isoScreenToGrid(Point2D screenPos) {
        Point2D gridPos2D = new Point2D(screenPos.getX()/TILE_WIDTH_ISO, screenPos.getY()/TILE_HEIGHT);
        int x = (int) ((gridPos2D.getY() - ISO_TILE_ORIGIN_Y) + (gridPos2D.getX() - ISO_TILE_ORIGIN_X));
        int y = (int) ((gridPos2D.getY() - ISO_TILE_ORIGIN_Y) - (gridPos2D.getX() - ISO_TILE_ORIGIN_X));
        return new Point2D(x, y);
    }
    /**
     * Converts an isometric grid position represented by a {@link Point2D}
     * to its corresponding screen coordinates.
     *
     * @param position the position in isometric grid coordinates
     * @return the corresponding screen coordinates as a {@link Point2D}
     */
    public static Point2D isoGridToScreen(Point2D position) {
        return isoGridToScreen(position.getX(), position.getY());
    }
    /**
     * Converts isometric grid coordinates to screen coordinates, considering the tile dimensions and origin.
     *
     * @param gridX the X coordinate in isometric grid space
     * @param gridY the Y coordinate in isometric grid space
     * @return a {@code Point2D} representing the equivalent screen coordinates
     */
    public static Point2D isoGridToScreen(double gridX, double gridY) {
        double screenX = (ISO_TILE_ORIGIN_X * TILE_WIDTH_ISO) + (gridX - gridY) * (TILE_WIDTH_ISO /2.0);
        double screenY = ISO_TILE_ORIGIN_Y * TILE_HEIGHT + (gridX + gridY) * (TILE_HEIGHT /2.0);
        return new Point2D(screenX, screenY);
    }

    /**
     * Converts a screen position in orthogonal coordinates to its corresponding position on the grid.
     * The conversion is based on predefined tile dimensions.
     *
     * @param screenPos the screen position in orthogonal coordinates to be converted to grid coordinates
     * @return a {@code Point2D} representing the corresponding position on the grid
     */
    public static Point2D orthScreenToGrid(Point2D screenPos) {
        int gridX = (int) (screenPos.getX() / TILE_WIDTH_ORTH);
        int gridY = (int) (screenPos.getY() / TILE_HEIGHT);
        return new Point2D(gridX, gridY);
    }

    /**
     * Converts grid coordinates in an orthogonal grid system into screen/world coordinates.
     *
     * @param gridX the x-coordinate in the orthogonal grid
     * @param gridY the y-coordinate in the orthogonal grid
     * @return a {@code Point2D} representing the corresponding screen/world coordinates
     */
    // grid indices to world coordinates for spawning stuff
    public static Point2D orthGridToScreen(double gridX, double gridY) {
        double worldX = gridX * TILE_WIDTH_ORTH;
        double worldY = gridY * TILE_HEIGHT;
        return new Point2D(worldX, worldY);
    }

    /**
     * This method searches through all entities in the game world for the one that has the {@code SelectableComponent}.
     * The game only allows one tank to be selected at all times.
     * @return the selected tank entity, or {@code null} if no such entity is marked as selected.
     */
    public static Entity findSelectedTank(){
        for (Entity e : FXGL.getGameWorld().getEntities()) {
            if (e.hasComponent(SelectableComponent.class))
                return e;
        }
        return null;
    }
    /**
     * Marks the given tank entity as selected by adding a {@code SelectableComponent}.*
     * @param tank the tank entity to be marked as selected
     */
    public static void selectTank(Entity tank){
        tank.addComponent(new SelectableComponent());
    }

    /**
     * Deselects the specified tank entity by removing its {@code SelectableComponent}.
     * @param tank the tank entity to be deselected
     */
    public static void deSelectTank(Entity tank){
        tank.removeComponent(SelectableComponent.class);
    }

    /**
     * Deselects the currently selected tank by removing its {@code SelectableComponent}.
     * This method identifies the selected tank by checking for an entity with a {@code SelectableComponent}
     * which only tanks get assigned.
     */
    public static void deSelectTank(){
        Entity tank = findSelectedTank();
        if (tank != null)
            tank.removeComponent(SelectableComponent.class);
    }
    /**
     * Gets all valid neighbor positions for a tank on the game map in a Set.
     * Only returns positions that are within map boundaries and not the tank's current position
     *
     * @param tankPos current position of the tank in tile coordinates (0,0) - (7,7)
     * @return Set of valid neighboring positions
     */

    public static Set<Point2D> getTankNeighbours(Point2D tankPos){
        Set<Point2D> neighborCells = new HashSet<>();
        for (int x = -5; x <= 5; x++) {
            for (int y = -5; y <= 5; y++) {
                double xDirection = tankPos.getX() + x;
                double yDirection = tankPos.getY() + y;
                if (x == 0 || y == 0 || //tank itself will not be selectable
                        xDirection < 0 || xDirection > 7 || //map boundaries
                        yDirection < 0 || yDirection > 7) {
                    continue;
                }
                neighborCells.add(new Point2D(xDirection, tankPos.getY()));
                neighborCells.add(new Point2D(tankPos.getX(), yDirection));
            }
        }
        return neighborCells;
    }

    public static boolean hasMountainAt(Point2D gridPos) {
        return FXGL.getGameWorld().getEntitiesByType(EntityType.MOUNTAIN)
                .stream().anyMatch(e -> {
                    Point2D pos = isoScreenToGrid(e.getCenter());
                    return pos.equals(gridPos);
                });
    }

    // Optional: Map-Grenzen prüfen
    public static boolean isValidTile(Point2D gridPos) {
        return gridPos.getX() >= 0 && gridPos.getX() < 8 && gridPos.getY() >= 0 && gridPos.getY() < 8;
    }

    /**
     * Calculates all valid move targets for a tank based on its current position.
     * The movement is constrained by the tank's orientation, valid tiles within the map,
     * and any terrain restrictions such as mountains.
     *
     * @param tankPos the current position of the tank in grid coordinates
     * @return a set of grid positions representing valid move targets for the tank
     */
    public static Set<Point2D> getTankMoveTargets(Point2D tankPos) {

        Set<Point2D> moveTargets = new HashSet<>();

        Entity tank = findSelectedTank();
        if (tank == null)
            return moveTargets;

        String state = getTankImageFilename(tank);

        Direction[] axes;
        if (state.equals("tank_top_left.png") || state.equals("tank_down_right.png")) {
            axes = new Direction[]{ Direction.LEFT, Direction.RIGHT };
        } else {
            axes = new Direction[]{ Direction.UP, Direction.DOWN };
        }

        for (Direction dir : axes) {
            Point2D current = tankPos;
            while (true) {
                current = step(current, dir);

                if (!isValidTile(current) || hasMountainAt(current))
                    break;

                moveTargets.add(current);
            }
        }
        return moveTargets;
    }


    /**
     * Retrieves the filename of the tank image associated with the given entity.
     * This method searches the view components of the entity for an {@code ImageView}
     * containing an image whose URL includes the word "tank".
     *
     * @param tank the entity representing a tank, whose image filename is to be extracted
     * @return the filename of the tank image, or an empty string if no such image is found
     */
    private static String getTankImageFilename(Entity tank) {
        List<Node> ch = tank.getViewComponent().getChildren();
        for (Node e : ch) {
            if (e instanceof ImageView iv) {
                String url = iv.getImage().getUrl();
                if (url.contains("tank")) {
                    return url.substring(url.lastIndexOf("/") + 1);
                }
            }
        }
        return "";
    }



    // Schritt-Funktion
    private static Point2D step(Point2D pos, Direction dir) {
        switch (dir) {
            case UP:    return new Point2D(pos.getX(), pos.getY() - 1);
            case DOWN:  return new Point2D(pos.getX(), pos.getY() + 1);
            case LEFT:  return new Point2D(pos.getX() - 1, pos.getY());
            case RIGHT: return new Point2D(pos.getX() + 1, pos.getY());
            default: throw new IllegalArgumentException();
        }
    }


}
