/**
 * @author Nico Steiner
 */
package com.robustgames.robustclient.presentation.UIElements;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class RobustButton extends Button {

    public RobustButton(String text, Runnable action, boolean isMenu) {
        super();
        Label btnText = new Label(text);
        if (isMenu) {
            btnText.getStyleClass().add("robust-btn-menu-text");
        } else btnText.getStyleClass().add("robust-btn-text");

        this.setGraphic(btnText);
        this.getStyleClass().add("robust-btn-menu");

        // No audio on hover or click; simply run the provided action.
        this.setOnMouseEntered(event -> {});
        this.setOnAction(e -> action.run());
    }
}

