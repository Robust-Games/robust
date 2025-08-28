/**
 * @author Burak Altun, eyesi001, Nico Steiner
 */
package com.robustgames.robustclient.business.actions;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.action.Action;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.texture.Texture;
import com.robustgames.robustclient.application.RobustApplication;
import com.robustgames.robustclient.business.entitiy.components.TankDataComponent;
import com.robustgames.robustclient.business.factories.BundleFactory;
import com.robustgames.robustclient.business.logic.Gamemode;
import com.robustgames.robustclient.business.logic.networkService.ConnectionService;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameTimer;

/**
 * Action that rotates a tank entity and if online notifies the server via a Bundle when the rotation begins.
 */
public class RotateAction extends Action {
    private final Texture newTankTexture;
    private final Gamemode currentGamemode = FXGL.<RobustApplication>getAppCast().getSelectedGamemode();
    private final String textureName;
    private final boolean isLocal;

    /**
     * Constructs a RotateAction for the given texture name.
     *
     * @param newTankTexture name of the tank texture that represents the new direction (e.g. "tank_top_left.png")
     */
    public RotateAction(String newTankTexture) {
        this.textureName = newTankTexture;
        this.newTankTexture = FXGL.getAssetLoader().loadTexture(newTankTexture);
        isLocal = true;
    }

    public RotateAction(String newTankTexture, boolean isLocal) {
        this.textureName = newTankTexture;
        this.newTankTexture = FXGL.getAssetLoader().loadTexture(newTankTexture);
        this.isLocal = isLocal;
    }

    /**
     * Called when the action starts executing during turn processing.
     * Sends a RotateAction bundle to the server, indicating the new direction, via BundleFactory.
     */
    @Override
    protected void onStarted() {
//        RobustApplication app = FXGL.<RobustApplication>getAppCast();
//        Connection<Bundle> conn = app.getConnection();
//        if (conn != null) {
//            Bundle rotateBundle = BundleFactory.createRotateActionBundle(entity, textureName);
//            conn.send(rotateBundle);
//        } else {
//            System.out.println("No connection set – can't send rotate!");
//        }
    }

    @Override
    protected void onQueued() {
        if (currentGamemode.equals(Gamemode.ONLINE)) {
            if (!isLocal) return;

            RobustApplication app = FXGL.getAppCast();
            Connection<Bundle> conn = FXGL.getService(ConnectionService.class).getConnection();
            if (conn != null) {
                Bundle rotateBundle = BundleFactory.createRotateActionBundle(entity, textureName);
                conn.send(rotateBundle);
            }
        }
    }


    @Override
    protected void onUpdate(double tpf) {
        getGameTimer().runOnceAfter(() -> {

            entity.getComponent(TankDataComponent.class).setInitialTankTexture(newTankTexture);
            setComplete();
        }, Duration.millis(200));
    }

    @Override
    protected void onCompleted() {
        super.onCompleted();
        if (currentGamemode.equals(Gamemode.LOCAL))
            entity.getComponent(TankDataComponent.class).resetBeforeTurn();

    }
}
