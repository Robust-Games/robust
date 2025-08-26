/**
 * @author Nico Steiner
 */
package com.robustgames.robustclient.presentation.UIElements;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.ui.FontType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class ConnectionView {
    private final VBox container;
    private final int xSIZE = 700;
    private final int ySIZE = 500;

    private TextField ipTextField;
    private Button connectButton;
    private Button backButton;
    private Text statusText;

    public ConnectionView() {
        // Title
        Text title = FXGL.getUIFactoryService().newText("Connect to Server", Color.WHITE, FontType.GAME, 36);
        title.setEffect(new DropShadow(8, Color.BLACK));

        // IP Input
        Text ipLabel = FXGL.getUIFactoryService().newText("Server IP:", Color.WHITE, FontType.GAME, 20);
        ipTextField = new TextField();
        ipTextField.setPromptText("localhost");
        ipTextField.setMaxWidth(200);
        ipTextField.setText("localhost");

        VBox ipSection = new VBox(5, ipLabel, ipTextField);
        ipSection.setAlignment(Pos.CENTER_LEFT);

        // Status text
        statusText = FXGL.getUIFactoryService().newText("", Color.WHITE, FontType.UI, 16);

        // Buttons
        connectButton = new RobustButton("Connect", () -> {}, false);
        connectButton.setStyle("-fx-min-width: 100px;");

        backButton = new RobustButton("Back", () -> {}, false);
        backButton.setStyle("-fx-min-width: 100px;");

        HBox buttonRow = new HBox(10, connectButton, backButton);
        buttonRow.setAlignment(Pos.CENTER);

        // Main container
        container = new VBox(30, title, ipSection, statusText, buttonRow);
        container.setPrefSize(Math.max(xSIZE, FXGL.getAppHeight() / 2.0), Math.max(ySIZE, FXGL.getAppHeight() / 2.0));
        container.setAlignment(Pos.TOP_CENTER);
        container.setPadding(new Insets(20));
        container.getStyleClass().add("robust-sub-menu");
    }

    public VBox getContainer() {
        return container;
    }

    public String getServerIP() {
        return ipTextField.getText();
    }

    public Button getConnectButton() {
        return connectButton;
    }

    public Button getBackButton() {
        return backButton;
    }

    public void setStatus(String status, Color color) {
        statusText.setText(status);
        statusText.setFill(color);
    }

    public void clearStatus() {
        statusText.setText("");
    }
}