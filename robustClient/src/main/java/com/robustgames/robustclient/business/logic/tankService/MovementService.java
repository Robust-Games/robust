/**
 * @author Burak Altun, Ersin Yesiltas, Nico Steiner
 */
package com.robustgames.robustclient.business.logic.tankService;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.action.ActionComponent;
import com.robustgames.robustclient.business.actions.MovementAction;
import com.robustgames.robustclient.business.entitiy.components.APComponent;
import com.robustgames.robustclient.business.entitiy.components.MovementComponent;
import com.robustgames.robustclient.business.entitiy.components.SelectableComponent;
import com.robustgames.robustclient.business.entitiy.components.TankDataComponent;
import com.robustgames.robustclient.business.logic.Direction;
import com.robustgames.robustclient.business.logic.gameService.MapService;
import javafx.application.Platform;
import javafx.geometry.Point2D;

import java.util.HashSet;
import java.util.Set;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getNotificationService;
import static com.robustgames.robustclient.business.entitiy.EntityType.MOUNTAIN;
import static com.robustgames.robustclient.business.entitiy.EntityType.TANK;

public class MovementService {

    /**
     * Moves the selected tank to the position of the clicked cell if the target is valid.
     *
     * @param clickedCell the cell that was clicked, representing the target position for the tank
     */
    public static void moveTank(Entity clickedCell) {
        Entity selectedTank = MapService.findSelectedTank();
        if (selectedTank != null) {
            int distance = (int) selectedTank.distance(clickedCell) / 64;
            Point2D target = clickedCell.getPosition();
            selectedTank.setPosition(target.getX(), target.getY());
            changeMountainLayer(selectedTank);
            selectedTank.getComponent(APComponent.class).use(distance);

            //removes and adds the SelectableComponent to update animation of tank selection
            //(since the tank selection animation is attached to the tile the tank is standing on)
            selectedTank.removeComponent(SelectableComponent.class);
            selectedTank.addComponent(new SelectableComponent());
            selectedTank.removeComponent(MovementComponent.class);
            selectedTank.addComponent(new MovementComponent());

            ActionComponent ac = selectedTank.getComponent(ActionComponent.class);
            ac.addAction(new MovementAction(clickedCell));

            // Pause until turn execution
            ac.pause();

        }
    }

//    public static void moveTankSended(double toX, double toY, Entity selectedTank) {
//        Point2D screenTarget = MapService.isoGridToScreen(toX, toY).subtract(64, 64);
//        Point2D direction = screenTarget.subtract(selectedTank.getPosition()).normalize();
//        selectedTank.translate(direction.multiply(tpf * 200));  // z. B. in einem Timer
//
//        if (selectedTank.distance(screenTarget) < 5) {
//            selectedTank.setPosition(screenTarget);
//            MovementService.changeMountainLayer(selectedTank);
//        }
//    }


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
        Entity selectedTank = MapService.findSelectedTank();

        if (selectedTank == null) {
            Platform.runLater(()->{
                getNotificationService().pushNotification("Not enough Action Points!");
            });
            return moveTargets;
        }
        int ap = selectedTank.getComponent(APComponent.class).getCurrentAP();

        if (ap <= 0) {
            Platform.runLater(()->{
                getNotificationService().pushNotification("Not enough Action Points!");
            });
            return moveTargets;
        }
        String state = "";
        if (selectedTank.getViewComponent().getChildren().contains(selectedTank.getComponent(TankDataComponent.class).getInitialTankTexture())) {
            state = selectedTank.getComponent(TankDataComponent.class)
                    .getInitialTankTexture().getImage().getUrl().substring(selectedTank.getComponent(TankDataComponent.class)
                            .getInitialTankTexture().getImage().getUrl().lastIndexOf("/") + 1);
        } else {
            state = selectedTank.getComponent(TankDataComponent.class).getNewTankTexture()
                    .getImage().getUrl().substring(selectedTank.getComponent(TankDataComponent.class).getNewTankTexture()
                            .getImage().getUrl().lastIndexOf("/") + 1);
        }

        // 2) Choose axes
        Direction[] axes;

        if (state.equals("tank_top_left.png") || state.equals("tank_down_right.png") || state.equals("green_tank_top_left.png") || state.equals("green_tank_down_right.png")) {
            axes = new Direction[]{Direction.LEFT, Direction.RIGHT};
        } else if (state.equals("tank_top_right.png") || state.equals("tank_down_left.png") || state.equals("green_tank_top_right.png") || state.equals("green_tank_down_left.png")) {
            axes = new Direction[]{Direction.UP, Direction.DOWN};
        } else
            throw new IllegalArgumentException("Invalid Tank State! in MapService getTankMoveTargets: " + state + " is not a valid state for a tank!");

        // 3) Jump along each axis until it hits a mountain or the edge
        for (Direction dir : axes) {
            Point2D current = tankPos;
            for (int stepCount = 1; stepCount <= ap; stepCount++) {
                current = step(current, dir);
                if (MapService.hasDestroyedTileAt(current) || !MapService.isWithinMapLimits(current) || MapService.hasMountainAt(current)) {
                    break;
                }
                if (!MapService.hasTankAt(current) && !MapService.hasCityAt(current)) {
                    moveTargets.add(current);
                }
            }
        }
        //move2Targets.add(moveTargets);
        return moveTargets;
    }

    public static Point2D step(Point2D pos, Direction dir) {
        return switch (dir) {
            case UP -> new Point2D(pos.getX(), pos.getY() - 1);
            case DOWN -> new Point2D(pos.getX(), pos.getY() + 1);
            case LEFT -> new Point2D(pos.getX() - 1, pos.getY());
            case RIGHT -> new Point2D(pos.getX() + 1, pos.getY());
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Updates the Z-index of the inputEntity (and movement tiles) and surrounding mountain entities based on
     * their positions. The method also adjusts the opacity and mouse interactivity of mountain entities
     * near the inputEntity.
     *
     * @param inputEntity the entity whose Z-index and interactions with mountain entities are to be updated
     */
    public static void changeMountainLayer(Entity inputEntity) {
        Point2D entityPosition = MapService.isoScreenToGrid(inputEntity.getCenter());
        int zIndexEntity = (int) (entityPosition.getX() + entityPosition.getY());
        inputEntity.setZIndex(zIndexEntity);

        FXGL.getGameWorld().getEntitiesByType(MOUNTAIN).forEach(mountain -> {
            Point2D mountainPos = MapService.isoScreenToGrid(mountain.getCenter());
            int zIndexMountain = (int) (mountainPos.getX() + mountainPos.getY());
            mountain.setZIndex(zIndexMountain);

            if (entityPosition.add(1, 0).equals(mountainPos)
                    || entityPosition.add(0, 1).equals(mountainPos)
                    || entityPosition.add(1, 1).equals(mountainPos)) {
                mountain.setOpacity(0.5);
                mountain.getViewComponent().getChildren().forEach(node -> node.setMouseTransparent(true));
            } else if (inputEntity.isType(TANK)) {
                mountain.setOpacity(1);
                mountain.getViewComponent().getChildren().forEach(node -> node.setMouseTransparent(false));
            }
        });
    }

    /**
     * Calculates the Manhattan distance between two points on a grid.
     * The Manhattan distance is the sum of the absolute differences of
     * their x and y coordinates.
     *
     * @param fromGrid the starting point on the grid
     * @param toGrid   the target point on the grid
     * @return the Manhattan distance as an integer between the two points
     */
    public static int gridDistance(Point2D fromGrid, Point2D toGrid) {
        double dx = Math.abs(toGrid.getX() - fromGrid.getX());
        System.out.println("FromX = " + fromGrid.getX() + ", ToX = " + toGrid.getX());
        System.out.println(dx);
        double dy = Math.abs(toGrid.getY() - fromGrid.getY());
        return (int) (dx + dy);
    }
}


