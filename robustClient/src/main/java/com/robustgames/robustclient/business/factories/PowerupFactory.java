package com.robustgames.robustclient.business.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.robustgames.robustclient.business.logic.PowerUp;
import javafx.geometry.Point2D;

import com.robustgames.robustclient.business.logic.gameService.MapService;

public class PowerupFactory {

    static int count = 0;

    public static void spawnRandomHpPowerup() {
        if(count > 1){
            return;
        }

        while (true) {
            int x = (int)(Math.random() * 8);
            int y = (int)(Math.random() * 8);

            Point2D gridPos = new Point2D(x, y);

            if (!MapService.hasMountainAt(gridPos) && !MapService.hasMountainAt(gridPos)) {
                Point2D worldPos = MapService.isoGridToScreen(gridPos);

                FXGL.getGameWorld().spawn("hpPowerup", worldPos.getX() -64 , worldPos.getY() -64 );
                count++;
                break;
            }
        }
    }
}
