package com.robustgames.robustclient.presentation.UIElements;

import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.function.Consumer;

public class ConnectionView {
    private Pane connectionPane;
    private VBox connectionBox;
    private TextField ipTextField;
    private Button connectButton;
    private Button cancelButton;
    private Text statusText;

    private Consumer<String> connectHandler;
    private Runnable cancelHandler;

    public ConnectionView() {
        createConnectionUI();
    }

    private void createConnectionUI() {
        connectionPane = new Pane();
        connectionBox = new VBox(10);
        connectionBox.setAlignment(Pos.CENTER);
        connectionBox.setTranslateX(FXGL.getAppWidth() / 2 - 150);
        connectionBox.setTranslateY(FXGL.getAppHeight() / 2 - 100);

        Text titleText = new Text("Connect to Server");
        titleText.getStyleClass().add("robust-btn-menu-text");

        ipTextField = new TextField();
        ipTextField.setPromptText("Enter server IP");
        ipTextField.setMaxWidth(200);
        ipTextField.setText("localhost");

        connectButton = new Button("Connect");
        connectButton.getStyleClass().add("robust-btn-menu");

        cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("robust-btn-menu");

        statusText = new Text();
        statusText.getStyleClass().add("robust-btn-menu-text");

        connectionBox.getChildren().addAll(titleText, ipTextField, connectButton, cancelButton, statusText);
        connectionPane.getChildren().add(connectionBox);
    }

    public void setConnectHandler(Consumer<String> connectHandler) {
        this.connectHandler = connectHandler;
        connectButton.setOnAction(e -> connectHandler.accept(ipTextField.getText()));
    }

    public void setCancelHandler(Runnable cancelHandler) {
        this.cancelHandler = cancelHandler;
        cancelButton.setOnAction(e -> cancelHandler.run());
    }

    public Pane getConnectionPane() {
        return connectionPane;
    }

    public void setStatus(String status) {
        statusText.setText(status);
    }

    public void show() {
        FXGL.getGameScene().addUINode(connectionPane);
    }

    public void hide() {
        FXGL.getGameScene().removeUINode(connectionPane);
    }
}