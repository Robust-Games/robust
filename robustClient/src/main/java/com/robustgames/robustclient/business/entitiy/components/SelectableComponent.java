package com.robustgames.robustclient.business.entitiy.components;


import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.robustgames.robustclient.business.entitiy.components.animations.AnimSelectionComponent;
import com.robustgames.robustclient.business.entitiy.components.animations.AnimTankComponent;
import com.robustgames.robustclient.business.logic.MapService;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGL.byType;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.robustgames.robustclient.business.entitiy.EntityType.ACTIONSELECTION;

public class SelectableComponent extends Component {
    Entity tileOfTank;

    @Override
    public void onAdded() {
        //entity.addComponent(new AnimTankComponent()); //TODO Update Animation for every direction
        Point2D tankPos = MapService.isoScreenToGrid(entity.getCenter());
        tileOfTank = getGameWorld().getEntitiesAt(MapService.isoGridToScreen(tankPos)).getFirst();
        tileOfTank.addComponent(new AnimSelectionComponent());
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        tileOfTank.removeComponent(AnimSelectionComponent.class);
        entity.removeComponent(AnimTankComponent.class);
        getGameWorld().removeEntities(byType(ACTIONSELECTION));
    }

}