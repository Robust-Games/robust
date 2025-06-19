package com.robustgames.robustclient.presentation.scenes;

import com.almasb.fxgl.entity.Entity;
import com.robustgames.robustclient.business.entitiy.components.TankDataComponent;
import com.robustgames.robustclient.business.logic.gameService.MapService;
import com.robustgames.robustclient.business.logic.Player;
import com.robustgames.robustclient.business.logic.gameService.TurnService;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import static com.almasb.fxgl.dsl.FXGL.*;

public class EndTurnView extends Pane {
    Button btnEndTurn;
    Label btnEndTurnText;

    public EndTurnView(){
        btnEndTurnText = new Label("End Turn");
        btnEndTurnText.getStyleClass().add("robust-end-btn-text");


        btnEndTurn = new Button();
        btnEndTurn.setGraphic(btnEndTurnText);
        btnEndTurn.getStyleClass().add("robust-end-btn");
        btnEndTurn.setPrefSize(225, 118);
        this.getChildren().add(btnEndTurn);
        this.setTranslateX(getAppWidth() - 260);
        this.setTranslateY(getAppHeight() - 150);

        //btnEndTurn.setBackground(new BackgroundImage(FXGL.getAssetLoader().loadTexture("frame1.png").getImage()));
        btnEndTurn.setOnAction(event -> {
            Player currentPlayer = TurnService.currentPlayer;
            Entity playerTank = MapService.findTankOfPlayer(currentPlayer);
            playerTank.getComponent(TankDataComponent.class).resetBeforeTurn();

            TurnService.nextPlayer();
        });



    }

}
