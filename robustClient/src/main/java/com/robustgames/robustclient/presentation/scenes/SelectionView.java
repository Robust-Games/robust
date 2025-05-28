package com.robustgames.robustclient.presentation.scenes;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.robustgames.robustclient.business.entitiy.components.MovementComponent;
import com.robustgames.robustclient.business.entitiy.components.RotateComponent;
import com.robustgames.robustclient.business.entitiy.components.ShootComponent;
import com.robustgames.robustclient.business.logic.MapService;
import javafx.util.Duration;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameWorld;
import static com.robustgames.robustclient.business.entitiy.EntityType.ACTIONSELECTION;
import static com.almasb.fxgl.dsl.FXGL.byType;


public class SelectionView extends Pane {
    Button btnMove;
    Button btnShoot;
    Button btnRotateLeft;
    Button btnRotateRight;




    public SelectionView() {
        String cssPath = getClass().getResource("/style.css").toExternalForm();
        this.getStylesheets().add(cssPath);


        btnMove = new Button("Move");
        btnMove.getStyleClass().add("robust-btn");
        btnMove.setOnAction(e -> {
            System.out.println("Movy groovy");
            Entity tank = MapService.findSelectedTank();
            if (tank != null) {
                //hier rotieren methode
                resetActionComponents(tank);
                FXGL.runOnce(() -> tank.addComponent(new MovementComponent()), Duration.seconds(0.01));
            }

        });

        btnShoot = new Button("Shoot");
        btnShoot.getStyleClass().add("robust-btn");
        btnShoot.setOnAction(e -> {
            System.out.println("Shooty tooty");
            Entity tank = MapService.findSelectedTank();
            if (tank != null) {
                resetActionComponents(tank);
                FXGL.runOnce(() -> tank.addComponent(new ShootComponent()), Duration.seconds(0.01));
            }
        });

        btnRotateLeft = new Button("Rotate Left");
        btnRotateLeft.getStyleClass().add("robust-btn");
        btnRotateLeft.setOnAction(e -> {
            System.out.println("Turn left");
            Entity tank = MapService.findSelectedTank();
            if (tank != null) {
                resetActionComponents(tank);
                FXGL.runOnce(() -> tank.addComponent(new RotateComponent()), Duration.seconds(0.01));
                FXGL.runOnce(() -> tank.getComponent(RotateComponent.class).rotateLeft(), Duration.seconds(0.01));
                // Richtung left an Component übergeben
            }

        });
        btnRotateRight = new Button("Rotate Right");
        btnRotateRight.getStyleClass().add("robust-btn");
        btnRotateRight.setOnAction(e -> {
            System.out.println("Turn right");
            Entity tank = MapService.findSelectedTank();
            if (tank != null) {
                resetActionComponents(tank);
                FXGL.runOnce(() -> tank.addComponent(new RotateComponent()), Duration.seconds(0.01));
                FXGL.runOnce(() -> tank.getComponent(RotateComponent.class).rotateRight(), Duration.seconds(0.01));
                // Richtung right an Component übergeben
            }

        });

        HBox box = new HBox(10);
        box.getChildren().addAll(
                btnMove, btnShoot, btnRotateLeft, btnRotateRight
        );
        box.setAlignment(Pos.CENTER);
        box.setTranslateX(getAppWidth() / 4.0 - 300);
        box.setTranslateY(getAppHeight() - 50);

        this.getChildren().add(box);


    }

    // Einheitliches Entfernen der Components
    private void resetActionComponents(Entity tank) {
        tank.removeComponent(MovementComponent.class);
        tank.removeComponent(RotateComponent.class);
        tank.removeComponent(ShootComponent.class);
        System.out.println("Components after removal:");
        System.out.println("  Has Movement: " + tank.hasComponent(MovementComponent.class));
        System.out.println("  Has Rotate: " + tank.hasComponent(RotateComponent.class));
        System.out.println("  Has Shoot: " + tank.hasComponent(ShootComponent.class));
        // Entities entfernen
        getGameWorld().removeEntities(byType(ACTIONSELECTION));
    }
}
