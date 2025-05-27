package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.robustgames.robustclient.business.logic.MapService;
import com.robustgames.robustclient.business.logic.Direction;

import javafx.geometry.Point2D;

import java.util.Objects;
import java.util.Set;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.spawn;
import static com.robustgames.robustclient.business.logic.MapService.getTankNeighbours;

public class ShootComponent extends Component {

    @Override
    public void onAdded() {
        System.out.println("ShootComponent ONADDED!! (Entity ID: " + entity.toString() + ")");
        Point2D tankPos = MapService.isoScreenToGrid(entity.getCenter());
        // Für jede der 4 Hauptachsen schießen
        for (Direction dir : Direction.values()) {
            Point2D current = tankPos;
            while (true) {
                current = step(current, dir); // Einen Schritt in die Richtung gehen

                // Prüfe Map-Grenzen
                if (!MapService.isValidTile(current))
                    break;

                // Prüfe, ob dort ein Berg ist
                if (MapService.hasMountainAt(current)) {
                    Point2D pos = MapService.isoGridToScreen(current);
                    FXGL.getGameWorld().spawn("AttackTargetTiles", pos.getX() - 64, pos.getY() - 64);
                    break; // Danach nicht weiter, Schuss endet am Berg
                }

                // Sonst normales Ziel anzeigen
                Point2D pos = MapService.isoGridToScreen(current);
                FXGL.getGameWorld().spawn("AttackTargetTiles", pos.getX() - 64, pos.getY() - 64);

                // Optional: abbrechen, falls dort noch andere Blocker sind (Panzer/City)
            }
        }
    }

    // Hilfsmethode: Einen Schritt in die Richtung gehen
    private Point2D step(Point2D pos, Direction dir) {
        switch (dir) {
            case UP:
                return new Point2D(pos.getX(), pos.getY() - 1);
            case DOWN:
                return new Point2D(pos.getX(), pos.getY() + 1);
            case LEFT:
                return new Point2D(pos.getX() - 1, pos.getY());
            case RIGHT:
                return new Point2D(pos.getX() + 1, pos.getY());
        }
        throw new IllegalArgumentException();
    }
}