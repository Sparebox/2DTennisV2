package game;

public enum Level {
    LEVEL1(4, "Level 1"),
    LEVEL2(6, "Level 2"),
    LEVEL3(8, "Level 3"),
    LEVEL4(10, "Level 4");

    public final int TILE_ROWS;
    public final String STRING;

    Level(int tileRows, String level) {
        this.TILE_ROWS = tileRows;
        this.STRING = level;
    }
}
