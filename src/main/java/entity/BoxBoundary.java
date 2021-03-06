package entity;

import java.awt.Color;
import java.awt.Graphics2D;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import utils.Utils;
/**
 * Static physics box boundary
 */
public final class BoxBoundary extends Entity {

    private PolygonShape ps;

    /**
     * Creates and initializes JBox2D physics for a new box boundary
     * @param x the initial x-coordinate
     * @param y the initial y-coordinate
     * @param width the width of the box in pixels
     * @param height the height of the box in pixels
     */
    public BoxBoundary(int x, int y, int width, int height) {
        this.width = Utils.toWorld(width);
        this.height = Utils.toWorld(height);
        this.bd = new BodyDef();
        this.bd.type = BodyType.STATIC;
        this.bd.position.set(Utils.toWorld(x), Utils.toWorld(y));

        this.ps = new PolygonShape();
        this.ps.setAsBox(this.width / 2, this.height / 2);

        this.fd = new FixtureDef();
        this.fd.shape = ps;
        this.fd.friction = 0.3f;
        this.fd.restitution = 0.1f;
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.drawRect(Utils.toPixel(body.getPosition().x - width/2), Utils.toPixel(body.getPosition().y) - Utils.toPixel(height/2), Utils.toPixel(width), Utils.toPixel(height));
    }

    @Override
    public void update() {
        
    }
    
}
