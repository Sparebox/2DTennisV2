package window;

import java.io.Serializable;
import java.util.HashMap;

public final class Settings implements Serializable {
    
    public static final String ROW_KEY = "tile_rows";
    public static final String FPS_KEY = "target_fps";
    public static final String MODE_KEY = "game_mode";

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
