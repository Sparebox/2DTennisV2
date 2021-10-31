package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import game.Game;
import utils.Utils;
import window.MainMenu;

public class Racquet extends Entity {

    public static int moveSpeed = 3;

    private PolygonShape ps;
   
    public Racquet(int x, int width, int height) {
        this.width = Utils.toWorld(width);
        this.height = Utils.toWorld(height);
        this.bd = new BodyDef();
        this.bd.type = BodyType.KINEMATIC;
        this.bd.position.set(Utils.toWorld(x), Utils.toWorld(Game.HEIGHT - 50));
        this.bd.allowSleep = false;

        this.ps = new PolygonShape();
        this.ps.setAsBox(this.width / 2, this.height / 2);

        this.fd = new FixtureDef();
        this.fd.shape = ps;
        this.fd.density = 1f;
        this.fd.friction = 0f;
        this.fd.restitution = 1f;
        this.fd.filter.categoryBits = CollisionCategory.RACQUET.BIT;
        this.fd.filter.maskBits = CollisionCategory.PICK_UP.BIT + CollisionCategory.BALL.BIT;
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.WHITE);
        AffineTransform old = g.getTransform();
        g.rotate(body.getAngle(), Utils.toPixel(body.getPosition().x), Utils.toPixel(body.getPosition().y));
        g.drawRect(Utils.toPixel(body.getPosition().x - width/2), Utils.toPixel(body.getPosition().y - height/2), Utils.toPixel(width), Utils.toPixel(height));
        g.setTransform(old);
    }

    @Override
    public void update() {
        if(MainMenu.currentGame == null)
            return;
        var keys = MainMenu.currentGame.getKeyManager().getKeys();
        int speed = keys.get(KeyEvent.VK_SHIFT) ? moveSpeed * 3 : moveSpeed;
        if(keys.get(KeyEvent.VK_LEFT) || keys.get(KeyEvent.VK_A)) {
            if(Utils.toPixel(body.getPosition().x - width/2) > 0)
                body.setTransform(body.getPosition().addLocal(Utils.toWorld(-speed), 0), 0f);
        }
        if(keys.get(KeyEvent.VK_RIGHT) || keys.get(KeyEvent.VK_D)) {
            if(Utils.toPixel(body.getPosition().x + width/2) < Game.WIDTH - 1)
                body.setTransform(body.getPosition().addLocal(Utils.toWorld(speed), 0), 0f);
        }
        if(keys.get(KeyEvent.VK_SPACE)) {
            if(Utils.toPixel(body.getPosition().y) > Game.HEIGHT - 100)
                body.setTransform(body.getPosition().addLocal(0, Utils.toWorld(-speed)), 0f);
        } else if(Utils.toPixel(body.getPosition().y) < Game.HEIGHT - 50)
            body.setTransform(body.getPosition().addLocal(0, Utils.toWorld(speed)), 0f);

        if(keys.get(KeyEvent.VK_COMMA) || keys.get(KeyEvent.VK_Q)) {
            body.setTransform(body.getPosition(), (float) Math.toRadians(-5));
        } else if(keys.get(KeyEvent.VK_PERIOD) || keys.get(KeyEvent.VK_E)) {
            body.setTransform(body.getPosition(), (float) Math.toRadians(5));
        } else
            body.setTransform(body.getPosition(), 0f);
    }
    
}
