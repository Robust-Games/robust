/**
 * @author Nico Steiner
 */
package com.robustgames.robustclient.presentation.UIElements;

import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getAudioPlayer;

public class RobustButton extends Button {

    public RobustButton(String text, Runnable action, boolean isMenu) {
        Label btnText = new Label(text);
        if (isMenu) {
            btnText.getStyleClass().add("robust-btn-menu-text");
        } else btnText.getStyleClass().add("robust-btn-text");

        this.setGraphic(btnText);
        this.getStyleClass().add("robust-btn-menu");

        Sound hover = FXGL.getAssetLoader().loadSound("UIHover.mp3");
        Sound accept = FXGL.getAssetLoader().loadSound("UIAccept.mp3");

        this.setOnMouseEntered(event -> {
            getAudioPlayer().playSound(hover);
        });
        this.setOnMouseClicked(e -> {
            getAudioPlayer().playSound(accept);
            action.run();
        });
    }
}

