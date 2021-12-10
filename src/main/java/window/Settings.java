package window;

import java.io.Serializable;
import java.util.HashMap;
/**
 * Settings class holds the settings information in a string based hash map
 */
public final class Settings implements Serializable {
    
    transient public static final String ROW = "tile_rows";
    transient public static final String FPS = "target_fps";
    transient public static final String MODE = "game_mode";
    transient public static final String LEVEL = "level";

    private HashMap<String, String> settings;

    public Settings() {
        this.settings = new HashMap<>();
    }

    /**
     * A wrapper method for the HashMap class method
     * @param key the setting to be modified
     * @param value the value for the setting
     */
    public void put(String key, String value) {
        this.settings.put(key, value);
    }

    /**
     * A wrapper method for the HashMap class method
     * @param key the setting whose value to get
     * @return the value of the setting as a string
     */
    public String get(String key) {
        return (String) this.settings.get(key);
    }
}
