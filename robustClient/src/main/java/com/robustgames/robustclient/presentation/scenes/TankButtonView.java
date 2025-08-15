/**
 * @author Ersin Yesiltas, Nico Steiner
 */
package com.robustgames.robustclient.presentation.scenes;

import com.almasb.fxgl.entity.Entity;
import com.robustgames.robustclient.business.entitiy.components.APComponent;
import com.robustgames.robustclient.business.entitiy.components.MovementComponent;
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
    Label btnMoveText;
    Label btnShootText;
    Label btnRotateText;
    Label btnRotateLeftText;
    Label btnRotateRightText;
    Tooltip shootingTooltip = new Tooltip("Costs 3AP, but Ends your Turn\nDeal one Damage to whatever you hit");
    Tooltip movingTooltip = new Tooltip("Costs one AP per tile moved");

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
                if (tank.getComponent(APComponent.class).getValue() < 1) {
                    getNotificationService().pushNotification("Not enough Action Points!");
                } else {
                    resetActionComponents(tank);
                    tank.addComponent(new MovementComponent());
                }
            }
        });

        btnShoot = new Button();
        btnShoot.setGraphic(btnShootText);
        btnShoot.setTooltip(shootingTooltip);
        btnShoot.getStyleClass().add("robust-btn");
        btnShoot.setOnAction(e -> {
            Entity tank = MapService.findSelectedTank();
            if (tank != null) {
                if (tank.getComponent(APComponent.class).getValue() < 1) {
                    getNotificationService().pushNotification("Not enough Action Points!");
                } else {
                    resetActionComponents(tank);
                    tank.addComponent(new ShootComponent());
                }
            }
        });

        buttonBox.getChildren().addAll(
                btnMove, btnShoot
        );

        this.setTranslateX(getAppWidth() / 32.0);
        this.setTranslateY(getAppHeight() - buttonBox.getHeight() - 200);
        this.getChildren().add(buttonBox);

    }

    /**
     * Removes movement and shoot components from the specified tank entity,
     * and deletes all action selection entities from the game world.
     *
     * @param tank the tank entity to clear action-related components from
     */
    private void resetActionComponents(Entity tank) {
        tank.removeComponent(MovementComponent.class);
        tank.removeComponent(ShootComponent.class);
        getGameWorld().removeEntities(byType(ACTIONSELECTION));
    }
}
