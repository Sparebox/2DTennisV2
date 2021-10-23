package entity;

import java.awt.Color;
import java.awt.Graphics2D;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import simulation.Simulation;
import utils.Utils;

public final class Ball extends Entity implements DynamicEntity {

    private CircleShape cs;
    private float radius;

    public Ball(int x, int y, int radius) {
        this.radius = Utils.toWorld(radius);
        this.bd = new BodyDef();
        this.bd.type = BodyType.DYNAMIC;
        this.bd.position.set(Utils.toWorld(x), Utils.toWorld(y));
        this.bd.allowSleep = true;

        this.cs = new CircleShape();
        this.cs.setRadius(this.radius/2);

        this.fd = new FixtureDef();
        this.fd.shape = cs;
        this.fd.density = 1f;
        this.fd.friction = 0.1f;
        this.fd.restitution = 0.9f;

        this.body = Simulation.world.createBody(this.bd);
        this.body.createFixture(this.fd);
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.drawOval(Utils.toPixel(body.getPosition().x - radius/2), Utils.toPixel(body.getPosition().y - radius/2), Utils.toPixel(radius), Utils.toPixel(radius));
    }

    @Override
    public void update() {
        
        
    }
    
}
