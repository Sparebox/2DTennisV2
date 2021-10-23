package entity;

import java.awt.Graphics2D;
import java.awt.Polygon;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;

import utils.Utils;

public abstract class Entity {
    
    protected Body body;
    protected BodyDef bd;
    protected PolygonShape ps;
    protected FixtureDef fd;
    protected float width;
    protected float height;

    public abstract void render(Graphics2D g);

    public abstract void update();

    public Polygon makeShapePolygon() {
        Vec2[] vertices = this.ps.getVertices();
        int[] xPoints = new int[vertices.length];
        int[] yPoints = new int[vertices.length];
        
        for(int i = 0; i < vertices.length; i++) {
            xPoints[i] = (int) vertices[i].x;
            yPoints[i] = Utils.toViewY(vertices[i].y);
        }
        return new Polygon(xPoints, yPoints, vertices.length);
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
