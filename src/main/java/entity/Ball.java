package entity;

import java.awt.Color;
import java.awt.Graphics2D;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import game.Game;
import utils.Utils;
import window.MainMenu;

public final class Ball extends Entity implements DynamicEntity {

    private CircleShape cs;
    private float radius;

    public Ball(int x, int y, int radius) {
        this.radius = Utils.toWorld(radius);
        this.bd = new BodyDef();
        this.bd.type = BodyType.DYNAMIC;
        this.bd.position.set(Utils.toWorld(x), Utils.toWorld(y));
        this.bd.allowSleep = true;
        this.bd.gravityScale = 0f;

        this.cs = new CircleShape();
        this.cs.setRadius(this.radius/2);

        this.fd = new FixtureDef();
        this.fd.filter.categoryBits = CollisionCategory.BALL.BIT;
        this.fd.filter.maskBits = CollisionCategory.TILE.BIT;
        this.fd.shape = cs;
        this.fd.density = 0.1f;
        this.fd.friction = 0f;
        this.fd.restitution = 1f;

        this.body = Game.physWorld.createBody(this.bd);
        this.body.createFixture(this.fd);
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillOval(Utils.toPixel(body.getPosition().x - radius/2), Utils.toPixel(body.getPosition().y - radius/2), Utils.toPixel(radius), Utils.toPixel(radius));
    }

    @Override
    public void update() {
        Vec2 old = body.getLinearVelocity();
        if(old.length() >= 5f) {
            old.normalize();
            body.setLinearVelocity(old.mul(5f));
        }
        if(Utils.toPixel(body.getPosition().y + radius) > Game.HEIGHT) {
            if(MainMenu.currentGame != null) {
                MainMenu.currentGame.stop();
                MainMenu.currentGame = null;
                new MainMenu();
            }
        }
    }
    
}
