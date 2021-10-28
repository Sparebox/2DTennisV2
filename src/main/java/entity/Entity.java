package entity;

import java.awt.Graphics2D;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;

import game.Game;
import window.MainMenu;

public abstract class Entity {
    
    protected Body body;
    protected BodyDef bd;
    protected FixtureDef fd;
    protected float width;
    protected float height;

    public abstract void render(Graphics2D g);

    public abstract void update();

    public void destroy() {
        Game.physWorld.destroyBody(this.body);
        MainMenu.currentGame.getEntitiesToDelete().add(this);
    }

    public Body getBody() {
        return body;
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

    public float getWidth() {
        return width;
    }
}
