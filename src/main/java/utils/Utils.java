package utils;

import window.Window;

public final class Utils {
    
    private static float pixelScale = 100f;

    public static int toViewY(float y) {
        return (int) (Window.W_HEIGHT - y);
    }

    public static float toWorldY(float y) {
        return Window.W_HEIGHT - y;
    }

    public static float toWorld(float length) {
        return (length / pixelScale);
    }

    public static int toPixel(float length) {
        return (int) (length * pixelScale);
    }

}
