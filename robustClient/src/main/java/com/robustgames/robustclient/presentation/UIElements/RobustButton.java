package com.robustgames.robustclient.presentation.UIElements;

import com.almasb.fxgl.ui.FXGLButton;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class RobustButton extends FXGLButton {

    public RobustButton(String text, Runnable action, boolean isMenu) {
        Label btnText = new Label(text);
        if (isMenu) {
            btnText.getStyleClass().add("robust-btn-menu-text");
        }
        else btnText.getStyleClass().add("robust-btn-text");


        this.setGraphic(btnText);
        this.getStyleClass().add("robust-btn-menu");

        this.setOnMouseClicked(e -> action.run());
    }
}
