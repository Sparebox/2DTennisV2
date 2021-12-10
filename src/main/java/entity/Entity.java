package entity;

import java.awt.Graphics2D;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;

import game.Game;

/**
 * Base class for all entities
 */
public abstract class Entity {
    
    protected static Game currentGame;

    protected Body body;
    protected BodyDef bd;
    protected FixtureDef fd;
    protected float width; // In meters
    protected float height; // In meters

    /**
     * Renders this entity
     * @param g the graphics context to be used
     */
    public abstract void render(Graphics2D g);

    /**
     * Updates this entity
     */
    public abstract void update();

    // Getters and setters

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public BodyDef getBd() {
        return bd;
    }

    public FixtureDef getFd() {
        return fd;
    }
    
    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public static void setCurrentGame(Game currentGame) {
        Entity.currentGame = currentGame;
    }
}
