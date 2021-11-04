package entity;

import java.awt.Color;
import java.awt.Graphics2D;

import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import utils.Utils;

public class LineBoundary extends Entity {

    private boolean visible;
    private EdgeShape es;
    private Vec2 start;
    private Vec2 end;

    public LineBoundary(int x1, int y1, int x2, int y2, boolean visible) {
        this.start = new Vec2(Utils.toWorld(x1), Utils.toWorld(y1));
        this.end = new Vec2(Utils.toWorld(x2), Utils.toWorld(y2));
        this.visible = visible;
        this.bd = new BodyDef();
        this.bd.type = BodyType.STATIC;
        this.bd.position.set(0, 0);

        this.es = new EdgeShape();
        this.es.set(this.start, this.end);
        
        this.fd = new FixtureDef();
        this.fd.shape = es;
        this.fd.friction = 0f;
        this.fd.restitution = 1f;
        this.fd.filter.categoryBits = CollisionCategory.BOUNDARY.BIT;
    }

    @Override
    public void render(Graphics2D g) {
        if(!visible)
            return;
        g.setColor(Color.WHITE);
        g.drawLine(Utils.toPixel(start.x), Utils.toPixel(start.y), Utils.toPixel(end.x), Utils.toPixel(end.y));
    }

    @Override
    public void update() {
        
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

}
