package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import simulation.Simulation;
import utils.Utils;

public class Box extends Entity implements DynamicEntity {
    
    private PolygonShape ps;

    public Box(int x, int y, int width, int height) {
        this.width = Utils.toWorld(width);
        this.height = Utils.toWorld(height);
        this.bd = new BodyDef();
        this.bd.type = BodyType.DYNAMIC;
        this.bd.position.set(Utils.toWorld(x), Utils.toWorld(y));
        this.bd.allowSleep = true;

        this.ps = new PolygonShape();
        this.ps.setAsBox(this.width / 2, this.height / 2);

        this.fd = new FixtureDef();
        this.fd.shape = ps;
        this.fd.density = 1f;
        this.fd.friction = 0.1f;
        this.fd.restitution = 0.1f;

        this.body = Simulation.world.createBody(this.bd);
        this.body.createFixture(this.fd);

    }

    @Override
    public void update() {
        
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.WHITE);
        AffineTransform old = g.getTransform();
        g.rotate(body.getAngle(), Utils.toPixel(body.getPosition().x), Utils.toPixel(body.getPosition().y));
        g.drawRect(Utils.toPixel(body.getPosition().x - width/2), Utils.toPixel(body.getPosition().y) - Utils.toPixel(height/2), Utils.toPixel(this.width), Utils.toPixel(this.height));
        g.setTransform(old);
    }

}
