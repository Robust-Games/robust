/**
 * @author Nico Steiner
 */
package com.robustgames.robustclient.presentation.scenes;

import com.almasb.fxgl.app.scene.StartupScene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class RobustStartupScene extends StartupScene {

    public RobustStartupScene(int appWidth, int appHeight) {
        super(appWidth, appHeight);


        Image bgImage = new Image("assets/textures/background.png");
        ImageView bgImageview = new ImageView(bgImage);


        Image image = new Image("assets/textures/logo.png");
        ImageView imageview = new ImageView(image);

        getContentRoot().getChildren().addAll(new StackPane(bgImageview, imageview));
    }

}

