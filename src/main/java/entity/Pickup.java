package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import game.Game;
import utils.Utils;
/**
 * Physics pickup
 */
public class Pickup extends Entity {

    private PickUpType pickUpType;
    private PolygonShape ps;
    private BufferedImage icon;

    /**
     * Creates and initializes JBox2D physics for a new pickup
     * @param x the initial x-coordinate
     * @param y the initial y-coordinate
     * @param width the width of the pickup in pixels
     * @param height the height of the pickup in pixels
     * @param pickUpType the effect of this pickup
     */
    public Pickup(int x, int y, int width, int height, PickUpType pickUpType) {
        this.pickUpType = pickUpType;
        try {
            this.icon = ImageIO.read(getClass().getResource("/pickup.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not load sprite images");
            System.exit(1);
        }
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

    // Getters and setters

    public PickUpType getPickUpType() {
        return pickUpType;
    }
    
}
