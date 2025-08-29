/**
 * @author Nico Steiner
 */
package com.robustgames.robustclient.presentation.scenes.menus;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.FontType;
import com.robustgames.robustclient.application.RobustApplication;
import com.robustgames.robustclient.business.logic.Gamemode;
import com.robustgames.robustclient.business.logic.networkService.ConnectionService;
import com.robustgames.robustclient.presentation.UIElements.ConnectionView;
import com.robustgames.robustclient.presentation.UIElements.OptionsView;
import com.robustgames.robustclient.presentation.UIElements.RobustButton;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.animationBuilder;
import static com.almasb.fxgl.dsl.FXGL.texture;


public class RobustMainMenu extends FXGLMenu {
    private static final double LEFT_MARGIN = 50.0;
    private static final double BUTTON_SPACING = 14.0;
    private final ConnectionView connectionView;
    private final VBox menuBox = new VBox(BUTTON_SPACING);
    private final List<Node> buttons = new ArrayList<>();

    private final Pane subMenu = new Pane();


    /**
     * Creates the main menu scene and initializes background, title and menu buttons.
     *
     * @param type the FXGL menu type
     */
    public RobustMainMenu(MenuType type) {
        super(type);
        connectionView = new ConnectionView();

        Music menuMusic = FXGL.getAssetLoader().loadMusic("Gear_Up.mp3");
        FXGL.getAudioPlayer().loopMusic(menuMusic);

        //Background
        ImageView background = texture("tank_assembly.gif", getAppWidth(), getAppHeight());
        getContentRoot().getChildren().add(background);

        // Title
        Texture title = texture("robustTitleText.png");
        title.setTranslateX(20);
        title.setTranslateY(20);
        getContentRoot().getChildren().add(title);

        // MenuBox (left)
        menuBox.setAlignment(Pos.TOP_LEFT);
        double startY = getAppHeight() / 4.0;
        menuBox.setTranslateX(LEFT_MARGIN);
        menuBox.setTranslateY(startY);

        Button btnNew = createActionButton("Start Game", () -> subMenu.getChildren().setAll(createStartContent()), true);
        Button btnOptions = createActionButton("Options", () -> subMenu.getChildren().setAll(createOptionsContent()), true);
        Button btnCredits = createActionButton("Credits", () -> subMenu.getChildren().setAll(createCreditsContent()), true);
        Button btnExit = createActionButton("Exit", () -> {
            try {
                FXGL.getService(ConnectionService.class).robustDisconnect();
            } catch (Exception ignored) { }
            this.fireExit();
        }, true);

        menuBox.getChildren().addAll(btnNew, btnOptions, btnCredits, btnExit);
        subMenu.setTranslateX(menuBox.getTranslateX() + 400); //button width in style.css + extra 50
        subMenu.translateYProperty().bind(menuBox.translateYProperty());

        getContentRoot().getChildren().addAll(menuBox, subMenu);
    }


    /**
     * Creates a styled button used in menus and sub-menus, tracks it for enter animations.
     *
     * @param text   the button text
     * @param action the action to execute on click
     * @param isMenu whether the button should use menu styling
     * @return a configured JavaFX Button
     */
    public Button createActionButton(String text, Runnable action, boolean isMenu) {
        RobustButton btn = new RobustButton(text, action, isMenu);
        // add to the button list for animation in onCreate()
        buttons.add(btn);
        return btn;
    }

    // Called by FXGL when the menu scene is created/activated
    /**
     * Plays entry animations for menu buttons when the menu is created.
     */
    @Override
    public void onCreate() {
        int animIndex = 0;

        for (Node btn : buttons) {
            btn.setOpacity(0.0);
            btn.setTranslateX(-200);

            // slide-in translate
            animationBuilder(this)
                    .delay(Duration.seconds(animIndex * 0.1))
                    .interpolator(Interpolators.BACK.EASE_OUT())
                    .translate(btn)
                    .from(new Point2D(-200, 0))
                    .to(new Point2D(0, 0))
                    .buildAndPlay();

            // fade in
            animationBuilder(this)
                    .delay(Duration.seconds(animIndex * 0.1))
                    .fadeIn(btn)
                    .buildAndPlay();

            animIndex++;
        }
    }


    // --------------------------------------------------------------------------------------------
    // sub-menus
    // --------------------------------------------------------------------------------------------
    /**
     * Builds the Options sub-menu content.
     *
     * @return a node containing the options UI
     */
    private Node createOptionsContent() {
        OptionsView options = new OptionsView(subMenu);
        return options.getContainer();
    }

    /**
     * Builds the Credits sub-menu content populated from FXGL settings credits.
     *
     * @return a node containing the credits UI
     */
    private Node createCreditsContent() {
        Text title = FXGL.getUIFactoryService().newText("CREDITS", Color.WHITE, FontType.GAME, 36);
        title.setEffect(new DropShadow(8, Color.BLACK));

        VBox vbox = new VBox(8);
        FXGL.getSettings().getCredits().forEach(line -> {
            var t = FXGL.getUIFactoryService().newText(line, Color.WHITE, FontType.GAME, 20);
            t.setEffect(new DropShadow(3, Color.BLACK));
            vbox.getChildren().add(t);
        });

        VBox container = new VBox(16, title, vbox);
        container.setAlignment(Pos.TOP_LEFT);
        container.setPrefSize(Math.max(700, FXGL.getAppHeight() / 2.0), Math.max(500, FXGL.getAppHeight() / 2.0));
        container.getStyleClass().add("robust-sub-menu");

        return container;
    }

    /**
     * Builds the Start sub-menu with choices for Online and Hotseat play and a Back button.
     *
     * @return a node containing the start options UI
     */
    private Node createStartContent() {
        VBox vbox = new VBox(8);

        var btnOnline = createActionButton("Online", () -> {
            subMenu.getChildren().setAll(createOnlineContent());
        }, false);

        var btnHotseat = createActionButton("Hotseat", () -> {
            FXGL.<RobustApplication>getAppCast().selectedGamemode = Gamemode.LOCAL;
            fireNewGame();
        }, false);

        var btnBack = createActionButton("Back", () -> subMenu.getChildren().clear(), false);

        vbox.getChildren().addAll(btnOnline, btnHotseat, btnBack);

        VBox container = new VBox(16, vbox);
        container.setAlignment(Pos.TOP_LEFT);
        container.getStyleClass().add("robust-sub-menu");

        return container;
    }

    /**
     * Builds the Online sub-menu and wires up connect/back button behavior.
     * On connecting, the selected gamemode is set to ONLINE and a new game is fired.
     *
     * @return a node containing the online connection UI
     */
    private Node createOnlineContent() {
        connectionView.getConnectButton().setOnAction(e -> {
            RobustApplication app = FXGL.getAppCast();
            String ip = connectionView.getServerIP();
            int port = Integer.parseInt(connectionView.getServerPort());

            app.setServerIP(ip);
            app.setServerPort(port);
            app.selectedGamemode = Gamemode.ONLINE;

            fireNewGame();
        });

        connectionView.getBackButton().setOnAction(e -> {
            subMenu.getChildren().setAll(createStartContent());
        });

        return connectionView.getContainer();
    }


}

