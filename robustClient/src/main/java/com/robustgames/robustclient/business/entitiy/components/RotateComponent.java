package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.robustgames.robustclient.business.logic.MapService;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import java.util.List;

public class RotateComponent extends Component {

    public void rotateLeft()  {
        Entity selectedTank = MapService.findSelectedTank();
        if (selectedTank != null) {
            String aktuell = deleteAndGive(selectedTank);

            switch (aktuell) {
                case "tank_top_right.png" -> {
                    Node img = FXGL.getAssetLoader().loadTexture("tank_top_left.png");
                    selectedTank.getViewComponent().addChild(img);
                }
                case "tank_top_left.png" -> {
                    Node img = FXGL.getAssetLoader().loadTexture("tank_down_left.png");
                    selectedTank.getViewComponent().addChild(img);
                }
                case "tank_down_left.png" -> {
                    Node img = FXGL.getAssetLoader().loadTexture("tank_down_right.png");
                    selectedTank.getViewComponent().addChild(img);
                }
                case "tank_down_right.png" -> {
                    Node img = FXGL.getAssetLoader().loadTexture("tank_top_right.png");
                    selectedTank.getViewComponent().addChild(img);
                }
            }
        }
    }
    public void rotateRight() {
        Entity selectedTank = MapService.findSelectedTank();
        if (selectedTank != null) {
            String aktuell = deleteAndGive(selectedTank);

            switch (aktuell) {
                case "tank_top_right.png" -> {
                    Node img = FXGL.getAssetLoader().loadTexture("tank_down_right.png");
                    selectedTank.getViewComponent().addChild(img);
                }
                case "tank_top_left.png" -> {
                    Node img = FXGL.getAssetLoader().loadTexture("tank_top_right.png");
                    selectedTank.getViewComponent().addChild(img);
                }
                case "tank_down_left.png" -> {
                    Node img = FXGL.getAssetLoader().loadTexture("tank_top_left.png");
                    selectedTank.getViewComponent().addChild(img);
                }
                case "tank_down_right.png" -> {
                    Node img = FXGL.getAssetLoader().loadTexture("tank_down_left.png");
                    selectedTank.getViewComponent().addChild(img);
                }
            }
        }
    }

    String deleteAndGive(Entity tank){
        List<Node> ch = tank.getViewComponent().getChildren();
        String x = "";
        for(Node e: ch) {
            if(e instanceof ImageView iv){
                String url = iv.getImage().getUrl();
                if(url.contains("tank")){
                    x = url.substring(url.lastIndexOf("/") + 1);
                    tank.getViewComponent().removeChild(e);
                    break;
                }
            }
        }
        return x;
    }
}