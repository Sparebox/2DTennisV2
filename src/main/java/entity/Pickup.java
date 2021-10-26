package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;

import javax.swing.ImageIcon;

import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import game.Game;
import utils.Utils;
import window.MainMenu;

public class Pickup extends Entity {

    private PickUpType pickUpType;
    private PolygonShape ps;
    private Image icon;

    public Pickup(int x, int y, int width, int height, PickUpType pickUpType) {
        this.pickUpType = pickUpType;
        this.icon = new ImageIcon("src/main/resources/pickup.png").getImage();
        this.width = Utils.toWorld(width);
        this.height = Utils.toWorld(height);
        this.bd = new BodyDef();
        this.bd.type = BodyType.DYNAMIC;
        this.bd.position.set(Utils.toWorld(x), Utils.toWorld(y));
        this.bd.allowSleep = false;

        this.ps = new PolygonShape();
        this.ps.setAsBox(this.width / 2, this.height / 2);

        this.fd = new FixtureDef();
        this.fd.filter.categoryBits = CollisionCategory.PICK_UP.BIT;
        this.fd.filter.maskBits = CollisionCategory.RACQUET.BIT;
        this.fd.isSensor = true;
        this.fd.shape = ps;

        this.body = Game.physWorld.createBody(this.bd);
        this.body.createFixture(this.fd);
        this.body.setGravityScale(0f);
        this.body.setLinearVelocity(new Vec2(0, 2f));
        this.body.setUserData(this);
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.drawRect(Utils.toPixel(body.getPosition().x - width/2), Utils.toPixel(body.getPosition().y - height/2), Utils.toPixel(width), Utils.toPixel(height));
        g.drawImage(icon, 
        Utils.toPixel(body.getPosition().x - width/2), 
        Utils.toPixel(body.getPosition().y - height/2), 
        Utils.toPixel(body.getPosition().x + width/2),
        Utils.toPixel(body.getPosition().y + height/2), 0, 0, icon.getWidth(null), icon.getHeight(null), null);
    }

    @Override
    public void update() {
        if(Utils.toPixel(body.getPosition().y - height/2) > Game.HEIGHT)
            destroy();
    }

    public void destroy() {
        Game.physWorld.destroyBody(body);
        MainMenu.currentGame.getEntitiesToDelete().add(this);
    }

    public PickUpType getPickUpType() {
        return pickUpType;
    }
    
}
