package utils;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

/**
 * For JBox2D unit conversion and physics manipulation
 */
public final class Utils {
    
    public static final float PPM = 150f; // Pixels per meter

    /**
     * Converts pixels into meters
     * @param length the length in pixels to convert
     * @return the length in meters
     */
    public static float toWorld(float length) { // Converts pixels to meters 
        return (length / PPM);
    }

    /**
     * Converts meters into pixels
     * @param length the length in meters to convert
     * @return the length in pixels
     */
    public static int toPixel(float length) { // Converts meters to pixels
        return (int) (length * PPM);
    }

    /**
     * Applies force to a physics object with the inverse square law
     * @param b the body of the physics object to apply force to
     * @param forceSource the source of the force as a 2D vector
     * @param applyPoint the point where the force is applied as a 2D vector
     * @param strength the base strength of the force
     */
    public static void applyForce(Body b, Vec2 forceSource, Vec2 applyPoint, float strength) {
        Vec2 blastDir = applyPoint.sub(forceSource);
        float distance = blastDir.normalize();
        if(distance == 0)
            return;
        float invDistance = 1 / distance;
        float impulseMag = strength * invDistance * invDistance;
        b.applyLinearImpulse(blastDir.mul(impulseMag), applyPoint);
    }

}