package window;

import java.io.Serializable;
import java.util.HashMap;

public final class Settings implements Serializable {
    
    public static final String ROW = "tile_rows";
    public static final String FPS = "target_fps";
    public static final String MODE = "game_mode";
    public static final String LEVEL = "level";

    private HashMap<String, String> settings;

    public Settings() {
        this.settings = new HashMap<>();
    }

    public void put(String key, String value) {
        this.settings.put(key, value);
    }

    public String get(String key) {
        return (String) this.settings.get(key);
    }
}
