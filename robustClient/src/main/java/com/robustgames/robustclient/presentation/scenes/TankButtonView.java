package com.robustgames.robustclient.presentation.scenes;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.net.Connection;
import com.robustgames.robustclient.business.entitiy.components.MovementComponent;
import com.robustgames.robustclient.business.entitiy.components.RotateComponent;
import com.robustgames.robustclient.business.entitiy.components.ShootComponent;
import com.robustgames.robustclient.business.logic.MapService;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameWorld;
import static com.robustgames.robustclient.business.entitiy.EntityType.ACTIONSELECTION;


public class TankButtonView extends Pane {
    Button btnMove;
    Button btnShoot;
    Button btnRotateLeft;
    Button btnRotateRight;
    Label btnMoveText;
    Label btnShootText;
    Label btnRotateLeftText;
    Label btnRotateRightText;

    private Connection<Bundle> connection = null;

    public TankButtonView() {
        String cssPath = getClass().getResource("/assets/ui/css/style.css").toExternalForm();
        this.getStylesheets().add(cssPath);
        btnMoveText = new Label("Move -1AP/Tile");
        btnShootText = new Label("Shoot -3AP");
        btnRotateLeftText = new Label("Rotate Left");
        btnRotateRightText = new Label("Rotate Right");
        btnMoveText.getStyleClass().add("robust-btn-text");
        btnShootText.getStyleClass().add("robust-btn-text");
        btnRotateLeftText.getStyleClass().add("robust-btn-text");
        btnRotateRightText.getStyleClass().add("robust-btn-text");

        btnMove = new Button();
        btnMove.setGraphic(btnMoveText);
        btnMove.getStyleClass().add("robust-btn");
        btnMove.setOnAction(e -> {
            Entity tank = MapService.findSelectedTank();
            if (tank != null) {
                resetActionComponents(tank);
                tank.addComponent(new MovementComponent());
            }

            if (connection != null) {
                Bundle bundle = new Bundle("UserAction");
                bundle.put("move", "MOVE CLICKED!");
                connection.send(bundle);
            } else {
                System.out.println("No connection yet!");
            }

        });

        btnShoot = new Button();
        btnShoot.setGraphic(btnShootText);
        btnShoot.getStyleClass().add("robust-btn");
        btnShoot.setOnAction(e -> {
            Entity tank = MapService.findSelectedTank();
            if (tank != null) {
                resetActionComponents(tank);
                tank.addComponent(new ShootComponent());

            }
        });

        btnRotateLeft = new Button();
        btnRotateLeft.setGraphic(btnRotateLeftText);
        btnRotateLeft.getStyleClass().add("robust-btn");
        btnRotateLeft.setOnAction(e -> {
            Entity tank = MapService.findSelectedTank();
            if (tank != null) {
                if (tank.hasComponent(MovementComponent.class)) {
                    tank.getComponent(RotateComponent.class).rotateLeft();
                    tank.removeComponent(MovementComponent.class);
                    tank.addComponent(new MovementComponent());
                } else {
                    resetActionComponents(tank);
                    tank.getComponent(RotateComponent.class).rotateLeft();

                }
            }

        });
        btnRotateRight = new Button();
        btnRotateRight.setGraphic(btnRotateRightText);
        btnRotateRight.getStyleClass().add("robust-btn");
        btnRotateRight.setOnAction(e -> {
            Entity tank = MapService.findSelectedTank();
            if (tank != null) {
                if (tank.hasComponent(MovementComponent.class)) {
                    tank.getComponent(RotateComponent.class).rotateRight();
                    tank.removeComponent(MovementComponent.class);
                    tank.addComponent(new MovementComponent());
                } else {
                    {
                        resetActionComponents(tank);
                        tank.getComponent(RotateComponent.class).rotateRight();
                    }
                }
            }

        });

        VBox buttonBox = new VBox(10);
        buttonBox.getChildren().addAll(
                btnMove, btnShoot, btnRotateLeft, btnRotateRight
        );
        //buttonBox.setAlignment(Pos.CENTER);
        this.setTranslateX(getAppWidth() / 32.0);
        this.setTranslateY(getAppHeight() - buttonBox.getHeight() - 200);
        this.getChildren().add(buttonBox);

    }

    // Einheitliches Entfernen der Components
    private void resetActionComponents(Entity tank) {
        tank.removeComponent(MovementComponent.class);
        tank.removeComponent(ShootComponent.class);
        getGameWorld().removeEntities(byType(ACTIONSELECTION));
    }

    // Zum Nachliefern der Connection
    public void setConnection(Connection<Bundle> conn) {
        this.connection = conn;
    }
}