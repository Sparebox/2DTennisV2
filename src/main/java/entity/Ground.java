package entity;

import java.awt.Color;
import java.awt.Graphics2D;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import simulation.Simulation;
import utils.Utils;

public final class Ground extends Entity implements StaticEntity {

    public Ground(int x, int y, float width, float height) {
        this.width = Utils.toWorld(width);
        this.height = Utils.toWorld(height);
        this.bd = new BodyDef();
        this.bd.type = BodyType.STATIC;
        this.bd.position.set(x, Utils.toWorldY(y));

        this.ps = new PolygonShape();
        this.ps.setAsBox(Utils.toPixel(this.width / 2), Utils.toPixel(this.height / 2));

        this.fd = new FixtureDef();
        this.fd.shape = ps;
        this.fd.friction = 0.3f;
        this.fd.restitution = 0.1f;

        this.body = Simulation.world.createBody(this.bd);
        this.body.createFixture(fd);
        
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.drawRect((int) body.getPosition().x - Utils.toPixel(width/2), Utils.toViewY(body.getPosition().y) - Utils.toPixel(height/2), Utils.toPixel(width), Utils.toPixel(height));
        g.setColor(Color.RED);
        g.drawLine(0, 0, (int) body.getPosition().x, Utils.toViewY(body.getPosition().y));
        g.drawLine(0, 0, (int) body.getPosition().x - Utils.toPixel(width/2), Utils.toViewY(body.getPosition().y) - Utils.toPixel(height/2));
        // g.drawLine(0, 0, Utils.toPixelX(body.getPosition().x) - Utils.toPixel(width/2), Utils.toPixelY(body.getPosition().y));
    }

    @Override
    public void update() {
        
    }
    
}
