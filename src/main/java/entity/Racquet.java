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

public class Racquet extends Entity implements KinematicEntity{

    private PolygonShape ps;

    public Racquet(int x, int width, int height) {
        this.width = Utils.toWorld(width);
        this.height = Utils.toWorld(height);
        this.bd = new BodyDef();
        this.bd.type = BodyType.STATIC;
        this.bd.position.set(Utils.toWorld(x), Utils.toWorld(Game.HEIGHT - 50));

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
        if(MainMenu.currentGame.getKeyManager().getKeys().get(KeyEvent.VK_LEFT)) {
            if(Utils.toPixel(body.getPosition().x - width/2) > 0)
                body.getPosition().addLocal(Utils.toWorld(-5), 0);
        }
        if(MainMenu.currentGame.getKeyManager().getKeys().get(KeyEvent.VK_RIGHT)) {
            if(Utils.toPixel(body.getPosition().x + width/2) < Game.WIDTH - 1)
                body.getPosition().addLocal(Utils.toWorld(5), 0);
        }
        // if(MainMenu.currentGame.getKeyManager().getKeys().get(KeyEvent.VK_LEFT)) {
        //     body.setLinearVelocity(new Vec2(-5, 0));
        // }
        
    }
    
}
