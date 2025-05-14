package com.robustgames.robustclient.business.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.robustgames.robustclient.business.logic.MovementService;
import javafx.beans.binding.Bindings;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * For destructible entities on our map. May later include tiles
 */
public class MapFactory implements EntityFactory {
    @Spawns("Background")
    public Entity spawnBackground(SpawnData data) {
        return FXGL.entityBuilder(data)
                .view(new Rectangle(data.<Integer>get("width"), data.<Integer>get("height"), Color.GREY))
                .with(new IrremovableComponent())
                .zIndex(-100)
                .build();
    }

    @Spawns("mountain")
    public Entity spawnMountain(SpawnData data) {
        return FXGL.entityBuilder(data)
                .viewWithBBox("mountain2D.png")
                .build();
    }

    //Tile Grafik
    @Spawns("floorTile")
    public Entity spawnFloor(SpawnData data) {
        return FXGL.entityBuilder(data)
                .zIndex(-10)
                .build();
    }

    //Aufleuchtende Tiles, die können auch dann für die visualisierung von move und shoot verwendet werden
    @Spawns("hoverTile")
    public Entity spawnHoverFloor(SpawnData data) {
        Rectangle rect = new Rectangle(64, 64);
        rect.setOpacity(0.40);

        var cell = FXGL.entityBuilder(data).viewWithBBox(rect)
                .onClick(tile -> {
                    MovementService.moveTank(tile);
                    MovementService.rotateAutomatically(tile);
                })
                .build();
        rect.fillProperty().bind(
                Bindings.when(cell.getViewComponent().getParent().hoverProperty())
                        .then(Color.DARKGREEN)
                        .otherwise(Color.TRANSPARENT)
        );

        return cell;
    }
}
