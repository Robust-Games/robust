/**
 * @author Burak Altun, Nico Steiner
 */
package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import com.robustgames.robustclient.application.RobustApplication;
import com.robustgames.robustclient.business.logic.Player;
import com.robustgames.robustclient.business.logic.tankService.MovementService;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGL.byType;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.robustgames.robustclient.business.entitiy.EntityType.ACTIONSELECTION;

public class TankDataComponent extends Component {
    private final Player owner;
    private Point2D initialPos;
    private Texture initialTankTexture;
    private String initialTankView;
    private String newTankView;
    private Texture newTankTexture;
    private Texture turretTexture;
    private String turretTextureName = "";
    private Texture hullTexture;

    public TankDataComponent(Player player, Texture view) {
        owner = player;
        initialTankTexture = view;
        newTankTexture = view;
        initialTankView = view.getImage().getUrl();
        initialTankView = initialTankView.substring(initialTankView.lastIndexOf("/") + 1);
        newTankView = initialTankView;
    }

    public String getInitialTankView() {
        return initialTankView;
    }

    public Texture getHullTexture() {
        return hullTexture;
    }

    public void setHullTexture(Texture texture) {
        this.hullTexture = texture;
    }

    public Texture getTurretTexture() {
        return turretTexture;
    }

    public void setTurretTexture(Texture texture) {
        this.turretTexture = texture;
    }

    public Point2D getInitialPos() {
        return initialPos;
    }

    public void setInitialPos() {
        initialPos = entity.getPosition();
    }

    public Texture getInitialTankTexture() {
        return initialTankTexture;
    }

    public void setInitialTankTexture(Texture initialTankTexture) {
        this.initialTankTexture.set(initialTankTexture);
        this.initialTankTexture = initialTankTexture;
        initialTankView = initialTankTexture.getImage().getUrl();
        initialTankView = initialTankView.substring(initialTankView.lastIndexOf("/") + 1);

    }

    public Texture getNewTankTexture() {
        return newTankTexture;
    }

    public void setNewTankTexture(Texture newTankTexture) {
        this.newTankTexture = newTankTexture;
        newTankView = newTankTexture.getImage().getUrl();
        newTankView = newTankView.substring(newTankView.lastIndexOf("/") + 1);
    }

    public Player getOwner() {
        return owner;
    }

    public String getTurretTextureName() {
        return turretTextureName;
    }

    public void setTurretTextureName(String turretTextureName) {
        this.turretTextureName = turretTextureName;
    }

    public String getNewTankView() {
        return newTankView;
    }

    public void resetBeforeTurn() {
        // Clean up all textures
        Texture turret = getTurretTexture();
        if (turret != null && entity.getViewComponent().getChildren().contains(turret)) {
            entity.getViewComponent().removeChild(turret);
            setTurretTexture(null);
        }

        Texture tankHullTexture = getHullTexture();
        if (tankHullTexture != null && entity.getViewComponent().getChildren().contains(tankHullTexture)) {
            entity.getViewComponent().removeChild(tankHullTexture);
            setHullTexture(null);
        }

        // Reset position and remove entities
        entity.setPosition(initialPos);
        getGameWorld().removeEntities(byType(ACTIONSELECTION));
        FXGL.<RobustApplication>getAppCast().deSelectTank();

        // Remove components
        if (entity.hasComponent(ShootComponent.class))
            entity.removeComponent(ShootComponent.class);
        if (entity.hasComponent(MovementComponent.class))
            entity.removeComponent(MovementComponent.class);

        // Remove any other textures that might be in the view component
        if (entity.getViewComponent().getChildren().contains(newTankTexture)) {
            entity.getViewComponent().removeChild(newTankTexture);
        }

        // Add the initial tank texture back if it's not already there
        if (!entity.getViewComponent().getChildren().contains(initialTankTexture)) {
            entity.getViewComponent().addChild(initialTankTexture);
        }

        // Reset the newTankTexture to be the same as initialTankTexture
        newTankTexture = initialTankTexture;

        MovementService.changeMountainLayer(entity);
    }
}

