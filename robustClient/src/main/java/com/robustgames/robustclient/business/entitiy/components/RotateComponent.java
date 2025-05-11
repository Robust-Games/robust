package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

public class RotateComponent extends Component {

    double currentAngle = 0;

    public void rotateTowards(Point2D direction) {
        double dx = direction.getX(); // horizontale Bewegung
        double dy = direction.getY(); // vertikale Bewegung
    
        double targetAngle;

        if (Math.abs(dx) > Math.abs(dy)) { // horizontale Bewegung überwiegt
            targetAngle = dx > 0 ? 90 : 270; // rechts oder links

        } else if (Math.abs(dx) < Math.abs(dy)){ // vertikale Bewegung überwiegt
            targetAngle = dy > 0 ? 180 : 0; // unten oder oben

        } else return; // gleich -> keine Änderung

        if (targetAngle != currentAngle) {
            entity.setRotation(targetAngle);   // optisch drehen
            currentAngle = targetAngle;        // internen Zustand updaten
        }
    }

    public void setCurrentAngle(double angle) {
        this.currentAngle = angle;
    }



}
    /*
    private static final Point2D UP = new Point2D(0, -1);
    private static final Point2D DOWN = new Point2D(0, 1);
    private static final Point2D LEFT = new Point2D(-1, 0);
    private static final Point2D RIGHT = new Point2D(1, 0);

    public void rotateTowards(Point2D direction) {
        double angle;

        if (direction.equals(UP)) angle = 0;
        else if (direction.equals(RIGHT)) angle = 90;
        else if (direction.equals(DOWN)) angle = 180;
        else if (direction.equals(LEFT)) angle = 270;
        else return;

        entity.setRotation(angle);
    }


     */