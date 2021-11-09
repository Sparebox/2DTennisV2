package window;

import java.io.Serializable;
import java.util.HashMap;

public final class Settings implements Serializable {
    
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
