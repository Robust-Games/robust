package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

public class RotateComponent extends Component {

    public void rotateUp()    {
        rotateTo("tank2D_up.png");    }
    public void rotateDown()  {
        rotateTo("tank2D_down.png");  }
    public void rotateLeft()  {
        rotateTo("tank2D_left.png");  }
    public void rotateRight() {
        rotateTo("tank2D_right.png"); }

    void rotateTo(String png ){
        // altes Tank‐Bild entfernen (aber nicht selection.png)
        entity.getViewComponent().getChildren().removeIf(node ->
                node instanceof ImageView &&
                        ((ImageView) node).getImage().getUrl().endsWith("png")
        );

        // neues Bild laden und hinzufügen
        Node img = FXGL.getAssetLoader().loadTexture(png);
        entity.getViewComponent().addChild(img);
    }
}