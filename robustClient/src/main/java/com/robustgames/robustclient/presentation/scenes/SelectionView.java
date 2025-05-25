package com.robustgames.robustclient.presentation.scenes;

import com.almasb.fxgl.entity.Entity;
import com.robustgames.robustclient.business.entitiy.components.MovementComponent;
import com.robustgames.robustclient.business.entitiy.components.RotateComponent;
import com.robustgames.robustclient.business.entitiy.components.ShootComponent;
import com.robustgames.robustclient.business.logic.MapService;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;


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
                tank.removeComponent(ShootComponent.class);
                tank.removeComponent(RotateComponent.class);
                tank.addComponent(new MovementComponent());
            }

        });

        btnShoot = new Button("Shoot");
        btnShoot.getStyleClass().add("robust-btn");
        btnShoot.setOnAction(e -> {
            System.out.println("Shooty tooty");
            Entity tank = MapService.findSelectedTank();
            if (tank != null) {
                tank.removeComponent(MovementComponent.class);
                tank.removeComponent(RotateComponent.class);
                tank.addComponent(new ShootComponent());
            }
        });

        btnRotateLeft = new Button("Rotate Left");
        btnRotateLeft.getStyleClass().add("robust-btn");
        btnRotateLeft.setOnAction(e -> {
            System.out.println("Turn left");
            Entity tank = MapService.findSelectedTank();
            if (tank != null) {
                tank.removeComponent(MovementComponent.class);
                tank.removeComponent(ShootComponent.class);
                tank.addComponent(new RotateComponent());
            }

        });
        btnRotateRight = new Button("Rotate Right");
        btnRotateRight.getStyleClass().add("robust-btn");
        btnRotateRight.setOnAction(e -> {
            System.out.println("Turn right");
            Entity tank = MapService.findSelectedTank();
            if (tank != null) {
                tank.removeComponent(MovementComponent.class);
                tank.removeComponent(ShootComponent.class);
                tank.addComponent(new RotateComponent());
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

}
