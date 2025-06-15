package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.action.ActionComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import com.robustgames.robustclient.business.actions.RotateAction;
import com.robustgames.robustclient.business.logic.Direction;
import com.robustgames.robustclient.business.logic.tankService.RotateService;

public class RotateComponent extends Component {
    Texture newTankTexture;

    public void rotateRight() {
        newTankTexture = RotateService.rotateTankRight(entity);
        ActionComponent ac = entity.getComponent(ActionComponent.class);
        ac.addAction(new RotateAction(newTankTexture, Direction.RIGHT));
        ac.pause();
    }
    public void rotateLeft(){
        newTankTexture = RotateService.rotateTankLeft(entity);
        ActionComponent ac = entity.getComponent(ActionComponent.class);
        ac.addAction(new RotateAction(newTankTexture, Direction.LEFT));
        ac.pause();
    }
}