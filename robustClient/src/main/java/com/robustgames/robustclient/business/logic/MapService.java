package com.robustgames.robustclient.business.logic;

import javafx.geometry.Point2D;

public class MapService {
    private static final int TILE_WIDTH = 64;
    private static final int TILE_HEIGHT = 64;

    public static Point2D screenToGrid(Point2D screenPos) {
        int gridX = (int) (screenPos.getX() / TILE_WIDTH);
        int gridY = (int) (screenPos.getY() / TILE_HEIGHT);
        return new Point2D(gridX, gridY);
    }

    // grid indices to world coordinates for spawning stuff
    public static Point2D gridToScreen(int gridX, int gridY) {
        double worldX = gridX * TILE_WIDTH + TILE_WIDTH / 2.0;
        double worldY = gridY * TILE_HEIGHT + TILE_HEIGHT / 2.0;
        return new Point2D(worldX, worldY);
    }
}
