package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import game.Game;
import utils.Utils;
import window.MainMenu;
/**
 * Physics tile
 */
public class Tile extends Entity {
    
    private PolygonShape ps;
    private boolean dropped;

    /**
     * Creates and initializes JBox2D physics for a new tile
     * @param x the initial x-coordinate of the tile
     * @param y the initial y-coordinate of the tile
     * @param width the width of the tile in pixels
     * @param height the height of the tile in pixels
     */
    public Tile(int x, int y, int width, int height) {
        this.width = Utils.toWorld(width);
        this.height = Utils.toWorld(height);
        this.bd = new BodyDef();
        this.bd.type = BodyType.DYNAMIC;
        this.bd.position.set(Utils.toWorld(x), Utils.toWorld(y));
        this.bd.allowSleep = true;
        this.bd.awake = false;
        this.bd.gravityScale = 0f;

        this.ps = new PolygonShape();
        this.ps.setAsBox(this.width / 2, this.height / 2);

        this.fd = new FixtureDef();
        this.fd.filter.categoryBits = CollisionCategory.TILE.BIT;
        this.fd.filter.maskBits = CollisionCategory.BALL.BIT;
        this.fd.shape = ps;
        this.fd.density = 10f;
        this.fd.friction = 0f;
        this.fd.restitution = 1f;
    }

    @Override
    public void update() {
        if(currentGame == null)
            return;
        if(Utils.toPixel(body.getPosition().y - width/2) > Game.HEIGHT) {
            currentGame.getPhysWorld().destroyBody(body);
            MainMenu.currentGame.getEntitiesToDelete().add(this);
        }
        if(body.getLinearVelocity().length() > 0.001f && !dropped) {
            body.setGravityScale(1f);
            Fixture fixtureList = body.getFixtureList();
            if(fixtureList != null) {
                fixtureList.getFilterData().categoryBits = CollisionCategory.NO_COLLISION.BIT;
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
