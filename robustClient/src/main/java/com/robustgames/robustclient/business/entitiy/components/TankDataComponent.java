package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import com.robustgames.robustclient.application.RobustApplication;
import com.robustgames.robustclient.business.logic.MapService;
import com.robustgames.robustclient.business.logic.Player;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGL.byType;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.robustgames.robustclient.business.entitiy.EntityType.ACTIONSELECTION;

public class TankDataComponent extends Component {
    private Point2D initialPos;
    private Texture initialTankTexture;
    private Texture newTankTexture;
    private final Player owner;
    private Texture turretTexture;

    public Texture getTurretTexture() {
        return turretTexture;
    }

    public void setTurretTexture(Texture texture) {
        this.turretTexture = texture;
    }

    public TankDataComponent(Player player, Texture view) {
        owner = player;
        initialTankTexture = view;
        newTankTexture = view;
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
        this.initialTankTexture = initialTankTexture;
    }

    public Texture getNewTankTexture() {
        return newTankTexture;
    }

    public void setNewTankTexture(Texture newTankTexture) {
        this.newTankTexture = newTankTexture;
    }

    public Player getOwner() {
        return owner;
    }
    public void resetBeforeTurn(){
        entity.setPosition(initialPos);
        getGameWorld().removeEntities(byType(ACTIONSELECTION));
        FXGL.<RobustApplication>getAppCast().deSelectTank();
        if (entity.getViewComponent().getChildren().contains(newTankTexture)) {
            entity.getViewComponent().removeChild(newTankTexture);
        }
        if (entity.getViewComponent().getChildren().contains(initialTankTexture)) {
            entity.getViewComponent().removeChild(initialTankTexture);
            entity.getViewComponent().addChild(initialTankTexture);
        }
        else entity.getViewComponent().addChild(initialTankTexture);
    }

}
