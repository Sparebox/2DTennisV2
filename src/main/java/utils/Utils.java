package utils;

import window.MainMenu;

public final class Utils {
    
    public static final float PPM = 150f; // Pixels per meter

    public static float toWorld(float length) {
        return (length / PPM);
    }

    public static int toPixel(float length) {
        return (int) (length * PPM);
    }

}
