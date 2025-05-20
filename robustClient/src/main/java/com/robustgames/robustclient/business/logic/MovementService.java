package com.robustgames.robustclient.business.logic;

import com.almasb.fxgl.entity.Entity;
import com.robustgames.robustclient.business.entitiy.components.RotateComponent;
import com.robustgames.robustclient.business.entitiy.components.SelectableComponent;
import javafx.geometry.Point2D;

public class MovementService {

    public static void moveTank(Entity clickedCell) {
        Entity selectedTank = MapService.findSelectedTank();
        if (selectedTank != null) {
            Point2D target = clickedCell.getPosition(); //TODO hier checken, ob man selecten darf
            selectedTank.setPosition(target.getX()-64, target.getY()-64);
            selectedTank.removeComponent(SelectableComponent.class); //Für später (dann auch bei shoot)
        }
    }

    //@burak für später, wenn der Spieler den weg zeichnet
    public static void rotateAutomatically(Entity tile) {
        Entity selectedTank = MapService.findSelectedTank();
        if (selectedTank != null) { // prüft ob was gewählt wurde

            Point2D target = tile.getPosition();
            Point2D gridTarget = MapService.orthScreenToGrid(target);

            Point2D from = selectedTank.getPosition();
            Point2D gridFrom = MapService.orthScreenToGrid(from);

            Point2D dir = gridTarget.subtract(gridFrom); // Richtung als Vektor in Weltkoordinaten

            // debug
            System.out.println("Von:  " + from + " bzw.: " + gridFrom);
            System.out.println("Nach: " + target + " bzw.: " + gridTarget);
            System.out.println("Differenz: " + dir);

            selectedTank.getComponent(RotateComponent.class).rotateTowards(dir);
            selectedTank.setPosition(target);

        }
    }
    public static void rotateManually() {}

    }

