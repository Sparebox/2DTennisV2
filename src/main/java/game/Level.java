package game;
/**
 * Enum class for game levels
 */
public enum Level {
    LEVEL1(4, "Level 1"),
    LEVEL2(6, "Level 2"),
    LEVEL3(8, "Level 3"),
    LEVEL4(10, "Level 4");

    public static final Level finalLevel = LEVEL4;

    public final int TILE_ROWS;

    private final String STRING;

    Level(int tileRows, String string) {
        this.TILE_ROWS = tileRows;
        this.STRING = string;
    }

    @Override
    public String toString() {
        return this.STRING;
    }
}
