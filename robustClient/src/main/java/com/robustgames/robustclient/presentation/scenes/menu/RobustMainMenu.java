package com.robustgames.robustclient.presentation.scenes.menu;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.robustgames.robustclient.application.RobustApplication;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class RobustMainMenu extends FXGLMenu {

    private final RobustApplication app;

    private final Text waitingText = new Text("Waiting for opponent...");

    public RobustMainMenu(RobustApplication app) {
        super(MenuType.MAIN_MENU);
        this.app = app;

        Text title = new Text("Choose Gamemode");

        Button btnLocal = new Button("Local");
        btnLocal.setOnAction(e -> {
            app.startLocalFromMenu();
            fireNewGame();
        });

        Text ipLabel = new Text("Server IP (for Online Multiplayer):");
        TextField ipField = new TextField("localhost");
        ipField.setPromptText("Enter server IP here");

        waitingText.setVisible(false);

        Button btnOnline = new Button("Online Multiplayer");
        btnOnline.setOnAction(e -> {
            String ip = ipField.getText().isBlank() ? "localhost" : ipField.getText();
            waitingText.setVisible(true);
            app.startOnlineFromMenu(ip, 55555);
            fireNewGame();
        });

        VBox box = new VBox(15, title, btnLocal, ipLabel, ipField, btnOnline, waitingText);
        box.setAlignment(Pos.CENTER);

        getContentRoot().getChildren().add(box);
    }
}
