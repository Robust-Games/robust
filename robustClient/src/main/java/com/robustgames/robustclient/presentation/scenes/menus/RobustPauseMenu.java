/**
 * @author Nico Steiner
 */
package com.robustgames.robustclient.presentation.scenes.menus;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.core.util.EmptyRunnable;
import com.almasb.fxgl.dsl.FXGL;
import com.robustgames.robustclient.presentation.UIElements.OptionsView;
import com.robustgames.robustclient.presentation.UIElements.RobustButton;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


public class RobustPauseMenu extends FXGLMenu {
    private static final double BUTTON_SPACING = 14.0;
    private final Pane subMenu = new Pane();
    private final OptionsView optionsView;
    private final RobustButton btnContinue = new RobustButton("Continue", () -> fireResume(), true);
    private final RobustButton btnOptions = new RobustButton("Options",
            () -> switchToOptions(), true);
    private final RobustButton btnExit = new RobustButton("Exit", () -> fireExitToMainMenu(), true);
    private final Animation<?> animation;

    /**
     * Creates the in-game pause menu with Continue, Options, and Exit buttons,
     * and sets up a scale-in animation.
     */
    public RobustPauseMenu() {
        super(MenuType.GAME_MENU);
        Rectangle background = new Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight());
        background.setFill(Color.TRANSPARENT);
        VBox menuBox = new VBox(BUTTON_SPACING);
        optionsView = new OptionsView(subMenu);
        Point2D optionsDimension = optionsView.getSize();
        menuBox.getChildren().addAll(btnContinue, btnOptions, btnExit);

        menuBox.setTranslateX(FXGL.getAppWidth() / 2.0 - 175); // button width / 2
        menuBox.setTranslateY(FXGL.getAppHeight() / 2.0 - 114.5); //(3 buttons + 14 BUTTON_SPACING) / 2
        subMenu.setTranslateX(FXGL.getAppWidth() / 2.0 - optionsDimension.getX() / 2);
        subMenu.setTranslateY(FXGL.getAppHeight() / 2.0 - optionsDimension.getY() / 2);


        animation = FXGL.animationBuilder()
                .duration(Duration.seconds(0.66))
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .scale(getContentRoot())
                .from(new Point2D(0, 0))
                .to(new Point2D(1, 1))
                .origin(new Point2D(FXGL.getAppWidth() / 2.0, FXGL.getAppHeight() / 2.0))
                .build();


        getContentRoot().getChildren().addAll(background, menuBox, subMenu);

    }

    /**
     * Switches the subMenu content to show the options view.
     */
    private void switchToOptions() {
        subMenu.getChildren().setAll(optionsView.getContainer());
    }

    /**
     * Starts the opening animation when the pause menu is created.
     */
    @Override
    public void onCreate() {
        animation.setOnFinished(EmptyRunnable.INSTANCE);
        animation.stop();
        animation.start();
    }

    /**
     * Updates the pause menu opening animation.
     *
     * @param tpf time per frame
     */
    @Override
    protected void onUpdate(double tpf) {
        animation.onUpdate(tpf);
    }

}
