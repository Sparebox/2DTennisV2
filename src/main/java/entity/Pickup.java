package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import game.Game;
import game.PickupGen;
import utils.Utils;

public class Pickup extends Entity {

    private PickUpType pickUpType;
    private PolygonShape ps;
    private Image icon;

    public Pickup(int x, int y, int width, int height, PickUpType pickUpType) {
        this.pickUpType = pickUpType;
        this.icon = new ImageIcon(getClass().getResource("/pickup.png")).getImage();
        this.width = Utils.toWorld(width);
        this.height = Utils.toWorld(height);
        this.bd = new BodyDef();
        this.bd.type = BodyType.DYNAMIC;
        this.bd.position.set(Utils.toWorld(x), Utils.toWorld(y));
        this.bd.allowSleep = false;
        this.bd.gravityScale = 0f;
        this.bd.linearVelocity = new Vec2(0, 2f);
        this.bd.userData = this;

        this.ps = new PolygonShape();
        this.ps.setAsBox(this.width / 2, this.height / 2);

        this.fd = new FixtureDef();
        this.fd.filter.categoryBits = CollisionCategory.PICK_UP.BIT;
        this.fd.filter.maskBits = CollisionCategory.RACQUET.BIT;
        this.fd.isSensor = true;
        this.fd.shape = ps;
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
        if(currentGame == null)
            return;
        if(Utils.toPixel(body.getPosition().y - height/2) > Game.HEIGHT)
            currentGame.getEntitiesToDelete().add(this);
    }

    public PickUpType getPickUpType() {
        return pickUpType;
    }
    
}
