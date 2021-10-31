package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import game.AreaQueryCallback;
import game.Game;
import utils.Utils;

public final class Rocket extends Entity{

    public static final int BLAST_RADIUS = 50; // In pixels
    public static final float BLAST_POWER = 1f;
    public static final int WIDTH = 25;
    public static final int HEIGHT = 50;

    private PolygonShape ps;
    private Polygon polygon = new Polygon();

    public Rocket(int x, int y, int width, int height) {
        this.width = Utils.toWorld(width);
        this.height = Utils.toWorld(height);
        this.bd = new BodyDef();
        this.bd.type = BodyType.DYNAMIC;
        this.bd.position.set(Utils.toWorld(x), Utils.toWorld(y));
        this.bd.allowSleep = false;

        this.ps = new PolygonShape();
        this.ps.setAsBox(this.width / 2, this.height / 2);

        this.fd = new FixtureDef();
        this.fd.filter.categoryBits = CollisionCategory.ROCKET.BIT;
        this.fd.filter.maskBits = CollisionCategory.TILE.BIT;
        this.fd.shape = ps;
        this.fd.density = 10f;
        this.fd.friction = 0f;
        this.fd.restitution = 1f;

        
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.WHITE);
        int[] xPoints = new int[]{Utils.toPixel(body.getPosition().x),
                                      Utils.toPixel(body.getPosition().x - width/2),
                                      Utils.toPixel(body.getPosition().x - width/2),
                                      Utils.toPixel(body.getPosition().x + width/2),
                                      Utils.toPixel(body.getPosition().x + width/2)};
        int[] yPoints = new int[]{Utils.toPixel(body.getPosition().y - height/2),
                                      Utils.toPixel(body.getPosition().y),
                                      Utils.toPixel(body.getPosition().y + height/2),
                                      Utils.toPixel(body.getPosition().y + height/2),
                                      Utils.toPixel(body.getPosition().y)};
        polygon.xpoints = xPoints;
        polygon.ypoints = yPoints;
        polygon.npoints = 5;
        AffineTransform old = g.getTransform();
        g.rotate(body.getAngle(), Utils.toPixel(body.getPosition().x), Utils.toPixel(body.getPosition().y));
        g.drawPolygon(polygon);
        g.setTransform(old);
    }

    @Override
    public void update() {
        
        
    }

    public void explode() {
        AreaQueryCallback callBack = new AreaQueryCallback();
        AABB area = new AABB(body.getPosition().sub(new Vec2(Utils.toWorld(BLAST_RADIUS), Utils.toWorld(BLAST_RADIUS))),
                             body.getPosition().add(new Vec2(Utils.toWorld(BLAST_RADIUS), Utils.toWorld(BLAST_RADIUS))));
        currentGame.getPhysWorld().queryAABB(callBack, area);
        for(Body b : callBack.getFoundBodies()) {
            if(b == this.body)
                continue;
            Vec2 bCOM = b.getPosition();
            if(bCOM.sub(this.body.getPosition()).length() >= Utils.toWorld(BLAST_RADIUS))
                continue;
            applyBlast(b, this.body.getPosition(), bCOM, Utils.toWorld(BLAST_POWER));
        }
        destroy();
    }

    private void applyBlast(Body b, Vec2 blastCenter, Vec2 applyPoint, float blastPower) {
        Vec2 blastDir = applyPoint.sub(blastCenter);
        float distance = blastDir.normalize();
        if(distance == 0)
            return;
        float invDistance = 1 / distance;
        float impulseMag = blastPower * invDistance * invDistance;
        b.applyLinearImpulse(blastDir.mul(impulseMag), applyPoint);
        System.out.println("Applied blast");
    }
    
}
