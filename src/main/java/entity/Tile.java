package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import game.Game;
import utils.Utils;
import window.MainMenu;

public class Tile extends Entity {
    
    private PolygonShape ps;
    private boolean dropped;

    public Tile(int x, int y, int width, int height) {
        this.width = Utils.toWorld(width);
        this.height = Utils.toWorld(height);
        this.bd = new BodyDef();
        this.bd.type = BodyType.DYNAMIC;
        this.bd.position.set(Utils.toWorld(x), Utils.toWorld(y));
        this.bd.allowSleep = true;
        this.bd.awake = false;

        this.ps = new PolygonShape();
        this.ps.setAsBox(this.width / 2, this.height / 2);

        this.fd = new FixtureDef();
        this.fd.filter.categoryBits = CollisionCategory.TILE.BIT;
        this.fd.filter.maskBits = CollisionCategory.BALL.BIT;
        this.fd.shape = ps;
        this.fd.density = 10f;
        this.fd.friction = 0f;
        this.fd.restitution = 1f;

        this.body = currentGame.getPhysWorld().createBody(this.bd);
        this.body.createFixture(this.fd);
        this.body.setGravityScale(0f);
    }

    @Override
    public void update() {
        if(MainMenu.currentGame == null)
            return;
        if(Utils.toPixel(body.getPosition().y - width/2) > Game.HEIGHT) {
            currentGame.getPhysWorld().destroyBody(body);
            MainMenu.currentGame.getEntitiesToDelete().add(this);
        }
        if(body.getLinearVelocity().length() > 0.001f && !dropped) {
            body.setGravityScale(1f);
            Fixture fixtureList = body.getFixtureList();
            if(fixtureList != null) {
                fixtureList.setSensor(true);
                fixtureList.refilter();
            }
            dropped = true;
            currentGame.setScore(currentGame.getScore() + 1);
        }
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
