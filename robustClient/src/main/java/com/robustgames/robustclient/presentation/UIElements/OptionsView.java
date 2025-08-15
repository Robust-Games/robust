/**
 * @author Nico Steiner
 */
package com.robustgames.robustclient.presentation.UIElements;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.ui.FontType;
import javafx.geometry.Point2D;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class OptionsView {
    private final VBox container;
    private final int xSIZE = 700;
    private final int ySIZE = 500;

    public OptionsView(Pane subMenuInput) {
        // Title
        Text optionsTitle = FXGL.getUIFactoryService().newText("OPTIONS", Color.WHITE, FontType.GAME, 36);
        optionsTitle.setEffect(new DropShadow(8, Color.BLACK));

        // Fullscreen toggle
        RobustButton btnFullscreen = new RobustButton("Toggle Fullscreen",
                () -> {
                    var stage = FXGL.getPrimaryStage();
                    stage.setFullScreen(!stage.isFullScreen());
                }, false);
        btnFullscreen.setStyle("-fx-min-width: 150px;");

        RobustButton btnBack = new RobustButton("Back", () -> subMenuInput.getChildren().clear(), false);
        btnBack.setStyle("-fx-min-width: 100px;");


        // Music / Sound sliders bound to settings
        Slider musicSlider = FXGL.getUIFactoryService().newSlider();
        musicSlider.setMin(0);
        musicSlider.setMax(1);
        musicSlider.valueProperty().bindBidirectional(FXGL.getSettings().globalMusicVolumeProperty());

        Slider soundSlider = FXGL.getUIFactoryService().newSlider();
        soundSlider.setMin(0);
        soundSlider.setMax(1);
        soundSlider.valueProperty().bindBidirectional(FXGL.getSettings().globalSoundVolumeProperty());

        Text musicLabel = FXGL.getUIFactoryService().newText("Music: ", Color.WHITE, FontType.GAME, 20);
        Text musicPercent = FXGL.getUIFactoryService().newText("", Color.WHITE, FontType.GAME, 20);
        musicPercent.textProperty().bind(musicSlider.valueProperty().multiply(100).asString("%.0f%%"));

        Text soundLabel = FXGL.getUIFactoryService().newText("Sound: ", Color.WHITE, FontType.GAME, 20);
        Text soundPercent = FXGL.getUIFactoryService().newText("", Color.WHITE, FontType.GAME, 20);
        soundPercent.textProperty().bind(soundSlider.valueProperty().multiply(100).asString("%.0f%%"));

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(20);
        grid.addRow(0, btnFullscreen);
        grid.addRow(1, musicLabel, musicSlider, musicPercent);
        grid.addRow(2, soundLabel, soundSlider, soundPercent);
        grid.addRow(9, btnBack);

        container = new VBox(16, optionsTitle, grid);
        container.setPrefSize(Math.max(xSIZE, FXGL.getAppHeight() / 2.0), Math.max(ySIZE, FXGL.getAppHeight() / 2.0));
        container.getStyleClass().add("robust-sub-menu");
    }

    public VBox getContainer() {
        return container;
    }

    public Point2D getSize() {
        return new Point2D(xSIZE, ySIZE);
    }
}

