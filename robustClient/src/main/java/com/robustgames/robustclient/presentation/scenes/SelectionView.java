package com.robustgames.robustclient.presentation.scenes;

import com.almasb.fxgl.entity.Entity;
import com.robustgames.robustclient.business.entitiy.components.MovementComponent;
import com.robustgames.robustclient.business.entitiy.components.RotateComponent;
import com.robustgames.robustclient.business.entitiy.components.ShootComponent;
import com.robustgames.robustclient.business.logic.MapService;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;


public class SelectionView extends HBox {
    Button btnMove;
    Button btnShoot;
    Button btnRotate;



    public SelectionView() {
        btnMove = new Button("Move");
        //btnMove.getStyleClass().add("robust-btn");
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
        //btnShoot.getStyleClass().add("robust-btn");
        btnShoot.setOnAction(e -> {
            System.out.println("Shooty tooty");
            Entity tank = MapService.findSelectedTank();
            if (tank != null) {
                tank.removeComponent(MovementComponent.class);
                tank.removeComponent(RotateComponent.class);
                tank.addComponent(new ShootComponent());
            }
        });

        btnRotate = new Button("Rotate");
        //btnRotate.getStyleClass().add("robust-btn");
        btnRotate.setOnAction(e -> {
            System.out.println("Turny roundy");
            Entity tank = MapService.findSelectedTank();
            if (tank != null) {
                tank.removeComponent(MovementComponent.class);
                tank.removeComponent(ShootComponent.class);
                tank.addComponent(new RotateComponent());
            }

        });

        HBox box = new HBox(btnMove, btnShoot, btnRotate);
        box.setAlignment(Pos.CENTER);
//        box.setTranslateX(getAppWidth() / 2.0 - 100);
//        box.setTranslateY(getAppHeight() / 4.0 - 100);

        this.getChildren().add(box);


    }
}
