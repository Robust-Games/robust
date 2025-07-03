package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import com.robustgames.robustclient.application.RobustApplication;
import com.robustgames.robustclient.business.entitiy.components.animations.AnimTankTurret;
import com.robustgames.robustclient.business.logic.Player;
import com.robustgames.robustclient.business.logic.tankService.MovementService;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGL.byType;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.robustgames.robustclient.business.entitiy.EntityType.ACTIONSELECTION;

public class TankDataComponent extends Component {
    private Point2D initialPos;
    private Texture initialTankTexture;
    private String initialTankView;
    private Texture newTankTexture;
    private final Player owner;
    private Texture turretTexture;
    private String turretTextureName = "";
    private Texture hullTexture;

    public TankDataComponent(Player player, Texture view) {
        owner = player;
        initialTankTexture = view;
        newTankTexture = view;
        initialTankView = view.getImage().getUrl();
        initialTankView = initialTankView.substring(initialTankView.lastIndexOf("/") + 1);
    }

    public String getInitialTankView() {return initialTankView;}

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

    public String getTurretTextureName() {
        return turretTextureName;
    }

    public void setTurretTextureName(String turretTextureName) {
        this.turretTextureName = turretTextureName;
    }

    public void resetBeforeTurn(){

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

        entity.setPosition(initialPos);
        getGameWorld().removeEntities(byType(ACTIONSELECTION));
        FXGL.<RobustApplication>getAppCast().deSelectTank();
        if (entity.hasComponent(ShootComponent.class))
            entity.removeComponent(ShootComponent.class);
        if (entity.hasComponent(MovementComponent.class))
            entity.removeComponent(MovementComponent.class);

        if (entity.getViewComponent().getChildren().contains(newTankTexture)) {
            entity.getViewComponent().removeChild(newTankTexture);
        }
        if (!entity.getViewComponent().getChildren().contains(initialTankTexture)) {
            entity.getViewComponent().addChild(initialTankTexture);
        }
        MovementService.changeMountainLayer(entity);
    }
}
