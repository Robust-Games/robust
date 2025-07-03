package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.entity.action.ActionComponent;
import com.almasb.fxgl.entity.component.Component;
import com.robustgames.robustclient.business.actions.RotateAction;
import com.robustgames.robustclient.business.logic.Direction;
import com.robustgames.robustclient.business.logic.tankService.RotateService;

public class RotateComponent extends Component {
    String newTankTexture;
    public void rotateLeft(){
        if (entity.getComponent(APComponent.class).canUse(1)) {
            newTankTexture = RotateService.rotateTank(entity, Direction.LEFT);
            entity.getComponent(APComponent.class).use(1);
            ActionComponent ac = entity.getComponent(ActionComponent.class);
            ac.addAction(new RotateAction(newTankTexture));
            ac.pause();
        }
    }

    public void rotateRight() {
        if (entity.getComponent(APComponent.class).canUse(1)) {
            newTankTexture = RotateService.rotateTank(entity, Direction.RIGHT);
            entity.getComponent(APComponent.class).use(1);
            ActionComponent ac = entity.getComponent(ActionComponent.class);
            ac.addAction(new RotateAction(newTankTexture));
            ac.pause();
        }
    }

}