package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.Required;
import com.robustgames.robustclient.application.RobustApplication;
import com.robustgames.robustclient.business.logic.GameState;
import com.robustgames.robustclient.business.logic.MapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.robustgames.robustclient.business.entitiy.EntityType.TANK;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This is needed since, apparently, Java classes have slightly different behavior
 * compared to Kotlin classes in terms of reflection.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class JavaComponentsTest {

    private Entity entity;

    @BeforeEach
    public void setUp() {

        entity = spawnTank(new SpawnData(100, 100, 0));
    }

    @Test
    public void testRequiredPartial() {
        assertThrows(IllegalStateException.class, () -> entity.addComponent(new RComponent()));

        entity.addComponent(new MovementComponent());

        assertThrows(IllegalStateException.class, () -> entity.addComponent(new RComponent()));

        entity.addComponent(new BComponent());

        assertDoesNotThrow(() -> entity.addComponent(new RComponent()));
    }

    private static class AComponent extends Component { }
    private static class BComponent extends Component { }

    @Required(AComponent.class)
    @Required(BComponent.class)
    private static class RComponent extends Component { }

    public Entity spawnTank(SpawnData data) {
        var hpComp = new HealthIntComponent(3);

        return FXGL.entityBuilder(data)
                .type(TANK)
                .with(hpComp)
                .viewWithBBox("tank_top_left.png")
                .with(new RotateComponent())
                .with(new APComponent(5))
                .onClick(tank ->{
                    //TODO Make the tile that the tank is standing on, also select the tank. i.e. add a tank property to hovertile
                    MapService.deSelectTank();
                    tank.addComponent(new SelectableComponent());
                    FXGL.<RobustApplication>getAppCast().onTankClicked(tank);

                })
                .build();
    }
}
