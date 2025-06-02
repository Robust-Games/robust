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
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameWorld;
import static com.robustgames.robustclient.business.entitiy.EntityType.ACTIONSELECTION;
import static com.almasb.fxgl.dsl.FXGL.byType;


public class SelectionView extends Pane {
    Button btnMove;
    Button btnShoot;
    Button btnRotateLeft;
    Button btnRotateRight;


    public SelectionView() {
        String cssPath = getClass().getResource("/assets/ui/css/style.css").toExternalForm();
        this.getStylesheets().add(cssPath);


        btnMove = new Button("Move");
        btnMove.getStyleClass().add("robust-btn");
        btnMove.setOnAction(e -> {
            Entity tank = MapService.findSelectedTank();
            if (tank != null) {
                resetActionComponents(tank);
                tank.addComponent(new MovementComponent());
            }

        });

        btnShoot = new Button("Shoot");
        btnShoot.getStyleClass().add("robust-btn");
        btnShoot.setOnAction(e -> {
            Entity tank = MapService.findSelectedTank();
            if (tank != null) {
                resetActionComponents(tank);
                tank.addComponent(new ShootComponent());

            }
        });

        btnRotateLeft = new Button("Rotate Left");
        btnRotateLeft.getStyleClass().add("robust-btn");
        btnRotateLeft.setOnAction(e -> {
            Entity tank = MapService.findSelectedTank();
            if (tank != null) {
                if (tank.hasComponent(MovementComponent.class)){
                    tank.getComponent(RotateComponent.class).rotateLeft();
                    tank.removeComponent(MovementComponent.class);
                    tank.addComponent(new MovementComponent());
                }
                else {
                    resetActionComponents(tank);
                    tank.getComponent(RotateComponent.class).rotateLeft();

                }
            }

        });
        btnRotateRight = new Button("Rotate Right");
        btnRotateRight.getStyleClass().add("robust-btn");
        btnRotateRight.setOnAction(e -> {
            Entity tank = MapService.findSelectedTank();
            if (tank != null) {
                if (tank.hasComponent(MovementComponent.class)){
                    tank.getComponent(RotateComponent.class).rotateRight();
                    tank.removeComponent(MovementComponent.class);
                    tank.addComponent(new MovementComponent());
                }
                else {
                    {
                        resetActionComponents(tank);
                        tank.getComponent(RotateComponent.class).rotateRight();
                    }
                }
            }

        });

        HBox box = new HBox(10);
        box.getChildren().addAll(
                btnMove, btnShoot, btnRotateLeft, btnRotateRight
        );
        box.setAlignment(Pos.CENTER);
        this.setTranslateX(getAppWidth() / 4.0 - 300);
        this.setTranslateY(getAppHeight() - 50);

        this.getChildren().add(box);


    }

    // Einheitliches Entfernen der Components
    private void resetActionComponents(Entity tank) {
        tank.removeComponent(MovementComponent.class);
        tank.removeComponent(ShootComponent.class);
        getGameWorld().removeEntities(byType(ACTIONSELECTION));
    }
}
//FXGL.runOnce(() -> tank.getComponent(RotateComponent.class).rotateLeft(), Duration.seconds(0.01));
