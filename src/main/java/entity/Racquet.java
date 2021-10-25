package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import game.Game;
import utils.Utils;
import window.MainMenu;

public class Racquet extends Entity implements KinematicEntity {

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

        this.body = Game.physWorld.createBody(this.bd);
        this.body.createFixture(this.fd);
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.drawRect(Utils.toPixel(body.getPosition().x - width/2), Utils.toPixel(body.getPosition().y - height/2), Utils.toPixel(width), Utils.toPixel(height));
    }

    @Override
    public void update() {
        if(MainMenu.currentGame == null)
            return;
        var keys = MainMenu.currentGame.getKeyManager().getKeys();
        int speed = keys.get(KeyEvent.VK_SHIFT) ? moveSpeed * 3 : moveSpeed;
        if(keys.get(KeyEvent.VK_LEFT)) {
            if(Utils.toPixel(body.getPosition().x - width/2) > 0)
                body.setTransform(body.getPosition().addLocal(Utils.toWorld(-speed), 0), 0f);
        }
        if(keys.get(KeyEvent.VK_RIGHT)) {
            if(Utils.toPixel(body.getPosition().x + width/2) < Game.WIDTH - 1)
                body.setTransform(body.getPosition().addLocal(Utils.toWorld(speed), 0), 0f);
        }
        if(keys.get(KeyEvent.VK_UP)) {
            if(Utils.toPixel(body.getPosition().y) > Game.HEIGHT - 100)
                body.setTransform(body.getPosition().addLocal(0, Utils.toWorld(-speed)), 0f);
        } else if(Utils.toPixel(body.getPosition().y) < Game.HEIGHT - 50)
            body.setTransform(body.getPosition().addLocal(0, Utils.toWorld(speed)), 0f);
    }
    
}
