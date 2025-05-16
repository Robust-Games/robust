package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import java.util.List;

public class RotateComponent extends Component {

    public void rotateUp()    {
        rotateTo("tank2D_up.png");
    }
    public void rotateDown()  {
        rotateTo("tank2D_down.png");
    }
    public void rotateLeft()  {
        rotateTo("tank2D_left.png");
    }
    public void rotateRight() {
        rotateTo("tank2D_right.png");
    }

    void rotateTo(String png ){ // Uneffizient ?
        List<Node> ch = entity.getViewComponent().getChildren();

        for(Node e: ch) {
            if(e instanceof ImageView iv){
                if(iv.getImage().getUrl().contains("tank2D")){
                    entity.getViewComponent().removeChild(e);
                }

                }
        }

        Node img = FXGL.getAssetLoader().loadTexture(png);
        entity.getViewComponent().addChild(img);
    }
}