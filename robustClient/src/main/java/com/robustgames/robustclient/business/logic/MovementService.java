package com.robustgames.robustclient.business.logic;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.robustgames.robustclient.business.entitiy.components.MovementComponent;
import com.robustgames.robustclient.business.entitiy.EntityType;
import com.robustgames.robustclient.business.entitiy.components.APComponent;
import com.robustgames.robustclient.business.entitiy.components.RotateComponent;
import com.robustgames.robustclient.business.entitiy.components.SelectableComponent;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

import static com.robustgames.robustclient.business.entitiy.EntityType.MOUNTAIN;
import static com.robustgames.robustclient.business.entitiy.EntityType.TANK;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getNotificationService;

public class MovementService {

    /**
     * Moves the selected tank to the position of the clicked cell if the target is valid.
     *
     * @param clickedCell the cell that was clicked, representing the target position for the tank
     */
    public static void moveTank(Entity clickedCell) {
        Entity selectedTank = MapService.findSelectedTank();
        if (selectedTank != null) {
/*
            //moves tank
            Point2D target = clickedCell.getPosition();
            selectedTank.setPosition(target.getX(), target.getY());
            //updates z coordinate
            changeMountainLayer(selectedTank);
            //removes and adds the SelectableComponent to update animation of tank selection
            //(since the tank selection animation is attached to the tile the tank is standing on)
            selectedTank.removeComponent(SelectableComponent.class);
            selectedTank.addComponent(new SelectableComponent());
            selectedTank.removeComponent(MovementComponent.class);
 */

            Point2D tankPos = MapService.isoScreenToGrid(selectedTank.getCenter()); // Grid Panzer
            Point2D clickedPos = MapService.isoScreenToGrid(clickedCell.getPosition()); // Grid clicked

            Set<Point2D> moveTargets = MapService.getTankMoveTargets(tankPos);
            int distance = gridDistance(tankPos, clickedPos);

            boolean moveable = tileIsMovable(clickedPos, moveTargets);
            boolean enoughAP = selectedTank.getComponent(APComponent.class).use(distance);

            if(!enoughAP){
                getNotificationService().pushNotification("Nicht genug Action Points!");
            }
            else if(!moveable){
                getNotificationService().pushNotification("Bewgung nur auf die Blau markierten Felder");}

            if(moveable && enoughAP) {
                Point2D target = clickedCell.getPosition();
                selectedTank.setPosition(target.getX() - 64, target.getY() - 64);
                selectedTank.removeComponent(SelectableComponent.class);
            }
        }
    }

    //@burak für später, wenn der Spieler den weg zeichnet
//    public static void rotateAutomatically(Entity tile) {
//        Entity selectedTank = MapService.findSelectedTank();
//        if (selectedTank != null)  { // prüft ob was gewählt wurde
//
//            Point2D target = tile.getPosition();
//            Point2D gridTarget = MapService.orthScreenToGrid(target);
//
//            Point2D from = selectedTank.getPosition();
//            Point2D gridFrom = MapService.orthScreenToGrid(from);
//
//            Point2D dir = gridTarget.subtract(gridFrom); // Richtung als Vektor in Weltkoordinaten
//
//            selectedTank.setPosition(target);
//
//        }
//    }

    /**
     * Checks if a tile, represented by the clicked cell, is a valid move target.
     *
     * @param clickedCell the position of the clicked tile on the grid
     * @param moveTargets the set of valid move target positions
     * @return true if the clicked cell is present in the set of valid move targets, false otherwise
     */
    static boolean tileIsMovable(Point2D clickedCell, Set<Point2D> moveTargets) {
        for(var t: moveTargets){
            if(clickedCell.equals(t)){
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates the Manhattan distance between two points on a grid.
     * The Manhattan distance is the sum of the absolute differences of
     * their x and y coordinates.
     *
     * @param fromGrid the starting point on the grid
     * @param toGrid the target point on the grid
     * @return the Manhattan distance as an integer between the two points
     */
    public static int gridDistance(Point2D fromGrid, Point2D toGrid) {
        double dx = Math.abs(toGrid.getX() - fromGrid.getX());
        System.out.println("FromX = " + fromGrid.getX() + ", ToX = " + toGrid.getX());
        System.out.println(dx);
        double dy = Math.abs(toGrid.getY() - fromGrid.getY());
        return (int)(dx + dy);
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

            if (entityPosition.add(1,0).equals(mountainPos)
                    || entityPosition.add(0,1).equals(mountainPos)
                    || entityPosition.add(1,1).equals(mountainPos)) {
                mountain.setOpacity(0.5);
                mountain.getViewComponent().getChildren().forEach(node -> node.setMouseTransparent(true));
            }
            else if( inputEntity.isType(TANK)){
                mountain.setOpacity(1);
                mountain.getViewComponent().getChildren().forEach(node -> node.setMouseTransparent(false));
            }



        });
    }
}

