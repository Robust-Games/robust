/**
 * @author Burak Altun, Carolin Scheffler, Ersin Yesiltas, Nico Steiner
 */
package com.robustgames.robustclient.presentation.scenes;

import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.robustgames.robustclient.application.RobustApplication;
import com.robustgames.robustclient.business.entitiy.components.APComponent;
import com.robustgames.robustclient.business.entitiy.components.SelectableComponent;
import com.robustgames.robustclient.business.entitiy.components.TankDataComponent;
import com.robustgames.robustclient.business.factories.BundleFactory;
import com.robustgames.robustclient.business.logic.Gamemode;
import com.robustgames.robustclient.business.logic.Player;
import com.robustgames.robustclient.business.logic.gameService.MapService;
import com.robustgames.robustclient.business.logic.gameService.TurnService;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getAudioPlayer;

public class EndTurnView extends Pane {
    Button btnEndTurn;
    Label btnEndTurnText;

    public EndTurnView() {
        btnEndTurnText = new Label("End Turn");
        btnEndTurnText.getStyleClass().add("robust-end-btn-text");


        btnEndTurn = new Button();
        btnEndTurn.setGraphic(btnEndTurnText);
        btnEndTurn.getStyleClass().add("robust-end-btn");
        btnEndTurn.setPrefSize(225, 118);
        this.getChildren().add(btnEndTurn);
        this.setTranslateX(getAppWidth() - 260);
        this.setTranslateY(getAppHeight() - 150);

        Sound endTurn = FXGL.getAssetLoader().loadSound("UIEndTurn.mp3");

        btnEndTurn.setOnAction(event -> {
            Platform.runLater(()->getAudioPlayer().playSound(endTurn));
            Player currentPlayer = TurnService.currentPlayer;
            Entity playerTank = MapService.findTankOfPlayer(currentPlayer);

            playerTank.removeComponent(SelectableComponent.class);
            playerTank.getComponent(APComponent.class).setCurrentAP(0);
            if (FXGL.<RobustApplication>getAppCast().getSelectedGamemode().equals(Gamemode.ONLINE)) {
                Platform.runLater(() -> FXGL.<RobustApplication>getAppCast().getEndTurnView().disableProperty().setValue(true));
                BundleFactory.signalTurnFinished();
            }
            if (FXGL.<RobustApplication>getAppCast().getSelectedGamemode().equals(Gamemode.LOCAL)) {
                playerTank.getComponent(TankDataComponent.class).resetBeforeTurn();
                TurnService.nextPlayer();
            }
        });
    }
}

