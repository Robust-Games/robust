package com.robustgames.robustclient.presentation.scenes;

import com.almasb.fxgl.entity.Entity;
import com.robustgames.robustclient.business.entitiy.components.MovementComponent;
import com.robustgames.robustclient.business.entitiy.components.RotateComponent;
import com.robustgames.robustclient.business.entitiy.components.ShootComponent;
import com.robustgames.robustclient.business.logic.gameService.MapService;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameWorld;
import static com.robustgames.robustclient.business.entitiy.EntityType.ACTIONSELECTION;


public class TankButtonView extends Pane {
    Button btnMove;
    Button btnShoot;
    Button btnRotate;
    Button btnRotateLeft;
    Button btnRotateRight;
    Label btnMoveText;
    Label btnShootText;
    Label btnRotateText;
    Label btnRotateLeftText;
    Label btnRotateRightText;
    Tooltip shootingTooltip = new Tooltip("Costs 3AP, but Ends your Turn\nDeal one Damage to whatever you hit");
    Tooltip movingTooltip = new Tooltip("Costs one AP per tile moved");
    Tooltip rotatingTooltip = new Tooltip("Rotate your Tank to change direction to drive in\nCosts one AP");


    public TankButtonView() {
        String cssPath = getClass().getResource("/assets/ui/css/style.css").toExternalForm();
        this.getStylesheets().add(cssPath);

        VBox buttonBox = new VBox(10);
        btnMoveText = new Label("Move");
        btnShootText = new Label("Shoot");
        btnRotateText = new Label("Rotate");
        btnRotateLeftText = new Label("Rotate Left");
        btnRotateRightText = new Label("Rotate Right");
        btnRotateText.getStyleClass().add("robust-btn-text");
        btnMoveText.getStyleClass().add("robust-btn-text");
        btnShootText.getStyleClass().add("robust-btn-text");
        btnRotateLeftText.getStyleClass().add("robust-btn-text");
        btnRotateRightText.getStyleClass().add("robust-btn-text");

        btnMove = new Button();
        btnMove.setGraphic(btnMoveText);
        btnMove.setTooltip(movingTooltip);
        btnMove.getStyleClass().add("robust-btn");
        btnMove.setOnAction(e -> {
            Entity tank = MapService.findSelectedTank();
            if (tank != null) {
                resetActionComponents(tank);
                tank.addComponent(new MovementComponent());

            }

        });

        btnShoot = new Button();
        btnShoot.setGraphic(btnShootText);
        btnShoot.setTooltip(shootingTooltip);
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
        btnRotateRight = new Button();
        btnRotateRight.setGraphic(btnRotateRightText);
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

        buttonBox.getChildren().addAll(
                btnMove, btnShoot
        );
        //buttonBox.setAlignment(Pos.CENTER);
        this.setTranslateX(getAppWidth() / 32.0);
        this.setTranslateY(getAppHeight() - buttonBox.getHeight() - 100);
        this.getChildren().add(buttonBox);

    }

    // Einheitliches Entfernen der Components
    private void resetActionComponents(Entity tank) {
        tank.removeComponent(MovementComponent.class);
        tank.removeComponent(ShootComponent.class);
        getGameWorld().removeEntities(byType(ACTIONSELECTION));
    }
}
//FXGL.runOnce(() -> tank.getComponent(RotateComponent.class).rotateLeft(), Duration.seconds(0.01));
