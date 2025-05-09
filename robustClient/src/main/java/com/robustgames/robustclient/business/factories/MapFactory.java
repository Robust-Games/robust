package com.robustgames.robustclient.business.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Point2D; // zum speichern
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * For destructible entities on our map. May later include tiles
 */
public class MapFactory implements EntityFactory {
    @Spawns("mountain")
    public Entity spawnMountain(SpawnData data) {
        return FXGL.entityBuilder(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .viewWithBBox("mountain2D.png").onClick(tile -> { // wenn tile geklickt wird -> lambda
                    Entity selectedTank = FXGL.getGameWorld().getProperties().getObject("selectedTank"); // nimmt das Objekt entgegen
                    if (selectedTank != null) { // prüft ob was gewähl wurde
                        Point2D target = tile.getCenter(); // muss angepasst werden glaube ich
                        selectedTank.setPosition(target);
                    }
                })
                .build();
    }

    @Spawns("Background")
    public Entity spawnBackground(SpawnData data) {
        return FXGL.entityBuilder(data)
                .view(new Rectangle(data.<Integer>get("width"), data.<Integer>get("height"), Color.GREY))
                .with(new IrremovableComponent())
                .zIndex(-100)
                .build();
    }

    /*
    @Spawns("walkable") // laufbare fläche in tiled machen
    public Entity spawnTile(SpawnData data) {
        return FXGL.entityBuilder(data)
                .zIndex(-100)
                .onClick(tile -> {
                    Entity selectedTank = FXGL.getGameWorld().getProperties().getObject("selectedTank");
                    if (selectedTank != null) {
                        Point2D target = tile.getCenter();
                        selectedTank.setPosition(target);
                    }
                })
                .build();
    }
    */
}
