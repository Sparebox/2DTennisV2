package utils;

import window.Window;

public final class Utils {
    
    public static final float PPM = 100f; // Pixels per meter

    public static float toWorld(float length) {
        return (length / PPM);
    }

    public static int toPixel(float length) {
        return (int) (length * PPM);
    }

}
