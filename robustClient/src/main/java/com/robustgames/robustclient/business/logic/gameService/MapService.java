package com.robustgames.robustclient.business.logic.gameService;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.robustgames.robustclient.business.entitiy.EntityType;
import com.robustgames.robustclient.business.entitiy.components.APComponent;
import com.robustgames.robustclient.business.entitiy.components.SelectableComponent;
import com.robustgames.robustclient.business.entitiy.components.TankDataComponent;
import com.robustgames.robustclient.business.logic.Direction;
import com.robustgames.robustclient.business.logic.Player;
import javafx.geometry.Point2D;
import java.util.HashSet;
import java.util.Set;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getNotificationService;
import static com.robustgames.robustclient.business.entitiy.EntityType.*;

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
     * Converts a screen-space point from screen coordinates to isometric grid coordinates.
     * This transformation is based on the specified tile dimensions and origin offsets.
     *
     * @param screenPos the position in screen-space coordinates to be transformed
     * @return the corresponding position in grid coordinates
     */
    public static Point2D isoScreenToGrid(Point2D screenPos) {
        Point2D gridPos2D = new Point2D(screenPos.getX()/TILE_WIDTH_ISO, screenPos.getY()/TILE_HEIGHT);
        int x = (int) ((gridPos2D.getY() - ISO_TILE_ORIGIN_Y) + (gridPos2D.getX() - ISO_TILE_ORIGIN_X));
        int y = (int) ((gridPos2D.getY() - ISO_TILE_ORIGIN_Y) - (gridPos2D.getX() - ISO_TILE_ORIGIN_X));
        return new Point2D(x, y);
    }
    /**
     * Converts a screen-space point from isometric coordinates to grid coordinates.
     * This transformation is based on the specified tile dimensions and origin offsets.
     *
     * @param screenPositionX the x position in screen-space coordinates to be transformed
     * @param screenPositionY the x position in screen-space coordinates to be transformed
     * @return the corresponding position in grid coordinates
     */
    public static Point2D isoScreenToGrid(double screenPositionX, double screenPositionY) {
        return isoScreenToGrid(new Point2D(screenPositionX, screenPositionY));
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
    public static Entity findTankOfPlayer(Player player){
        for (Entity tank : FXGL.getGameWorld().getEntitiesByComponent(TankDataComponent.class)) {
            if (tank.getComponent(TankDataComponent.class).getOwner().equals(player)) {
                return tank;
            }
        }
        return null;
    }

    public static Entity findSelectedTank(){
        for (Entity e : FXGL.getGameWorld().getEntities()) {
            if (e.hasComponent(SelectableComponent.class))
                return e;
        }
        return null;
    }

    public static boolean hasMountainAt(Point2D gridPos) {
        return FXGL.getGameWorld().getEntitiesByType(EntityType.MOUNTAIN)
                .stream().anyMatch(e -> {
                    Point2D pos = isoScreenToGrid(e.getCenter());
                    return pos.equals(gridPos);
                });
    }

    public static boolean hasCityAt(Point2D gridPos) {
        return FXGL.getGameWorld().getEntitiesByType(CITY)
                .stream().anyMatch(e -> {
                    Point2D pos = isoScreenToGrid(e.getCenter());
                    return pos.equals(gridPos);
                });
    }

    public static boolean hasTankAt(Point2D gridPos) {
        return FXGL.getGameWorld().getEntitiesByType(TANK)
                .stream().anyMatch(e -> {
                    Point2D pos = isoScreenToGrid(e.getCenter());
                    return pos.equals(gridPos);
                });
    }

    // Optional: Map-Grenzen prüfen
    public static boolean isOverTheEdge(Point2D gridPos) {
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
        Entity selectedTank = findSelectedTank();

        if (selectedTank == null) {
            getNotificationService().pushNotification("Not enough Action Points!");
            return moveTargets;
        }
        int ap = selectedTank.getComponent(APComponent.class).getCurrentAP();

        if (ap <= 0){
            getNotificationService().pushNotification("Not enough Action Points!");
            return moveTargets;
        }
        String state = "";
        if (selectedTank.getViewComponent().getChildren().contains(selectedTank.getComponent(TankDataComponent.class).getInitialTankTexture())){
            state = selectedTank.getComponent(TankDataComponent.class).getInitialTankTexture().getImage().getUrl().substring(selectedTank.getComponent(TankDataComponent.class).getInitialTankTexture().getImage().getUrl().lastIndexOf("/") + 1);
        }
        else{
            state = selectedTank.getComponent(TankDataComponent.class).getNewTankTexture().getImage().getUrl().substring(selectedTank.getComponent(TankDataComponent.class).getInitialTankTexture().getImage().getUrl().lastIndexOf("/") + 1);
        }

        // 2) Choose axes
        Direction[] axes;

        if (state.equals("tank_top_left.png") || state.equals("tank_down_right.png")) {
            axes = new Direction[]{ Direction.LEFT, Direction.RIGHT };
        } else if (state.equals("tank_top_right.png") || state.equals("tank_down_left.png")) {
            axes = new Direction[]{ Direction.UP, Direction.DOWN };
        }
        else throw new IllegalArgumentException("Invalid Tank State! in MapService getTankMoveTargets: " + state + " is not a valid state for a tank!");

        // 3) Jump along each axis until it hits a mountain or the edge
        for (Direction dir : axes) {
            Point2D current = tankPos;
            for (int stepCount = 1; stepCount <= ap; stepCount++) {
                current = step(current, dir);

                if (!isOverTheEdge(current) || hasMountainAt(current))
                    break;
                if (!hasTankAt(current) && !hasCityAt(current))
                    moveTargets.add(current);
            }
        }
        return moveTargets;
    }

    // Schritt-Funktion
    public static Point2D step(Point2D pos, Direction dir) {
        return switch (dir) {
            case UP -> new Point2D(pos.getX(), pos.getY() - 1);
            case DOWN -> new Point2D(pos.getX(), pos.getY() + 1);
            case LEFT -> new Point2D(pos.getX() - 1, pos.getY());
            case RIGHT -> new Point2D(pos.getX() + 1, pos.getY());
            default -> throw new IllegalArgumentException();
        };
    }
    }
