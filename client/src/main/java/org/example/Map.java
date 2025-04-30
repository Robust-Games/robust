package org.example;

public class Map {
        private int[][] grid;

        public Map(int colon, int row) {
            if (colon <= 0 || row <= 0) {
                throw new IllegalArgumentException("Bleid Positiv");
            }
            grid = new int[colon][row];
        }
}
