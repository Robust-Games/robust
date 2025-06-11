package com.robustgames.robustclient.presentation.scenes;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.robustgames.robustclient.business.entitiy.components.APComponent;
import com.robustgames.robustclient.business.logic.MapService;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGL.getAppWidth;

public class TankDataView extends Pane {
    private Text apText;
    private Entity selectedTank;
    private APComponent apComponent;

    public TankDataView() {
        String cssPath = getClass().getResource("/assets/ui/css/style.css").toExternalForm();
        this.getStylesheets().add(cssPath);

        apText = new Text();
        apText.getStyleClass().add("apText");
        apText.setTranslateY(40);
        apText.setTranslateX(22.5);

        ImageView iw = new ImageView(FXGL.getAssetLoader().loadTexture("frame1.png").getImage());

        this.getChildren().addAll(iw, apText);
        this.setTranslateX(getAppWidth() - 220);
        this.setTranslateY(30);

        // Initialize with currently selected tank
        updateSelectedTank();
    }

    public void updateSelectedTank() {
        Entity newSelectedTank = MapService.findSelectedTank();
        setSelectedTank(newSelectedTank);
    }

    public void setSelectedTank(Entity tank) {
        if (apComponent != null) {
            apText.textProperty().unbind();
        }

        selectedTank = tank;
        if (selectedTank != null && selectedTank.hasComponent(APComponent.class)) {
            apComponent = selectedTank.getComponent(APComponent.class);
            // Bind directly to the component's value property, just like HP
            apText.textProperty().bind(
                Bindings.format("AP:%d/%d",
                    apComponent.valueProperty(),
                    apComponent.maxValueProperty())
            );
        } else {
            apComponent = null;
            apText.setText("AP: -/-");
        }
    }
}