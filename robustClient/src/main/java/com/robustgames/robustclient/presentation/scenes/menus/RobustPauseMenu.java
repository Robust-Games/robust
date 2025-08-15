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
import javafx.util.Duration;


public class RobustPauseMenu extends FXGLMenu {
    private static final double BUTTON_SPACING = 14.0;
    private final Pane subMenu = new Pane();
    private final OptionsView optionsView;
    private final RobustButton btnContinue = new RobustButton("Continue", () -> fireContinue(), true);
    private final RobustButton btnOptions = new RobustButton("Options",
            () -> switchToOptions(), true);
    private final RobustButton btnExit = new RobustButton("Exit", () -> fireExit(), true);
    private final Animation<?> animation;
    int SIZE = 200;

    public RobustPauseMenu() {
        super(MenuType.GAME_MENU);
        VBox menuBox = new VBox(BUTTON_SPACING);
        optionsView = new OptionsView(subMenu);
        Point2D optionsDimension = optionsView.getSize();

        menuBox.setTranslateX(FXGL.getAppWidth() / 2.0 - SIZE);
        menuBox.setTranslateY(FXGL.getAppHeight() / 2.0 - SIZE);
        subMenu.setTranslateX(FXGL.getAppWidth() / 2.0 - optionsDimension.getX() / 2);
        subMenu.setTranslateY(FXGL.getAppHeight() / 2.0 - optionsDimension.getY() / 2);

        animation = FXGL.animationBuilder()
                .duration(Duration.seconds(0.66))
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .scale(getContentRoot())
                .from(new Point2D(0, 0))
                .to(new Point2D(1, 1))
                .build();


        menuBox.getChildren().addAll(btnContinue, btnOptions, btnExit);
        getContentRoot().getChildren().addAll(menuBox, subMenu);

    }

    private void switchToOptions() {
        subMenu.getChildren().setAll(optionsView.getContainer());
    }

    @Override
    public void onCreate() {
        animation.setOnFinished(EmptyRunnable.INSTANCE);
        animation.stop();
        animation.start();
    }

    @Override
    protected void onUpdate(double tpf) {
        animation.onUpdate(tpf);
    }

}
