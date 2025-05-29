package com.robustgames.robustclient.business.logic;

import com.almasb.fxgl.entity.Entity;
import com.robustgames.robustclient.business.entitiy.EntityType;
import com.robustgames.robustclient.business.entitiy.components.RotateComponent;
import com.robustgames.robustclient.business.entitiy.components.SelectableComponent;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.Set;

public class MovementService {

    /**
     * Moves the selected tank to the position of the clicked cell if the target is valid.
     *
     * @param clickedCell the cell that was clicked, representing the target position for the tank
     */
    public static void moveTank(Entity clickedCell) {
        Entity selectedTank = MapService.findSelectedTank();
        if (selectedTank != null) {
            Point2D tankPos = MapService.isoScreenToGrid(selectedTank.getCenter());
            Set<Point2D> moveTargets = MapService.getTankMoveTargets(tankPos);
            Point2D gridPos = MapService.isoScreenToGrid(clickedCell.getPosition());
            boolean moveable = tileIsMovable(gridPos, moveTargets);
            if(moveable) {
                Point2D target = clickedCell.getPosition(); //TODO hier checken, ob man selecten darf
                selectedTank.setPosition(target.getX() - 64, target.getY() - 64);
                selectedTank.removeComponent(SelectableComponent.class); //Für später (dann auch bei shoot)
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
}

