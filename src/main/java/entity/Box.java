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
    
    public Box(int x, int y, float width, float height) {
        this.width = Utils.toWorld(width);
        this.height = Utils.toWorld(height);
        this.bd = new BodyDef();
        this.bd.type = BodyType.DYNAMIC;
        this.bd.position.set(x, Utils.toWorldY(y));
        this.bd.allowSleep = true;

        this.ps = new PolygonShape();
        this.ps.setAsBox(Utils.toPixel(this.width / 2), Utils.toPixel(this.height / 2));
        System.out.println("Width: "+width+" Height: "+height);

        this.fd = new FixtureDef();
        this.fd.shape = ps;
        this.fd.density = 1f;
        this.fd.friction = 0.1f;
        this.fd.restitution = 0.5f;

        this.body = Simulation.world.createBody(this.bd);
        this.body.createFixture(this.fd);

    }

    @Override
    public void update() {
        
    }

    @Override
    public void render(Graphics2D g) {
        //System.out.println(body.getPosition());
        g.setColor(Color.WHITE);
        AffineTransform old = g.getTransform();
        g.rotate(-body.getAngle(), body.getPosition().x, Utils.toViewY(body.getPosition().y));
        g.drawRect((int) body.getPosition().x - Utils.toPixel(width/2), Utils.toViewY(body.getPosition().y) - Utils.toPixel(height/2), Utils.toPixel(width), Utils.toPixel(height));
        g.setTransform(old);
        g.setColor(Color.RED);
        g.drawLine(0, 0, (int) body.getPosition().x, Utils.toViewY(body.getPosition().y));
        // g.setColor(Color.RED);
        // g.drawLine(0, 0, (int) getCenter().x, (int) getCenter().y);
    }

}
