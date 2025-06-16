package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.entity.action.ActionComponent;
import com.almasb.fxgl.entity.component.Component;
import com.robustgames.robustclient.business.actions.RotateAction;
import com.robustgames.robustclient.business.logic.Direction;
import com.robustgames.robustclient.business.logic.tankService.RotateService;

public class RotateComponent extends Component {
    String newTankTexture;
    public void rotateLeft(){
        newTankTexture = RotateService.rotateTank(entity, Direction.LEFT);
        ActionComponent ac = entity.getComponent(ActionComponent.class);
        ac.addAction(new RotateAction(newTankTexture));
        ac.pause();
    }

    public void rotateRight() {
        newTankTexture = RotateService.rotateTank(entity, Direction.RIGHT);

        ActionComponent ac = entity.getComponent(ActionComponent.class);
        ac.addAction(new RotateAction(newTankTexture));
        ac.pause();
    }

}