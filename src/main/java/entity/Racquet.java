package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import game.Game;
import game.GameMode;
import utils.Utils;
import window.MainMenu;

public class Racquet extends Entity {

    public static final int ROTATION = 5;
    public static final float MIN_WIDTH = 30; // In pixels
    public static final int BOOST = 3;
    public static final int MOVE_SPEED = 3;

    private PolygonShape ps;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean cpuOwned;
   
    public Racquet(int x, int width, int height) {
        this.width = Utils.toWorld(width);
        this.height = Utils.toWorld(height);
        this.bd = new BodyDef();
        this.bd.type = BodyType.KINEMATIC;
        this.bd.position.set(Utils.toWorld(x), Utils.toWorld(Game.HEIGHT - 50));
        this.bd.allowSleep = false;
        this.bd.userData = this;

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

    public Racquet(int x, int y, int width, int height) {
        this.width = Utils.toWorld(width);
        this.height = Utils.toWorld(height);
        this.bd = new BodyDef();
        this.bd.type = BodyType.KINEMATIC;
        this.bd.position.set(Utils.toWorld(x), Utils.toWorld(y));
        this.bd.allowSleep = false;
        this.bd.userData = this;

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
        if(currentGame.getCurrentPickup() != null) {
            var metrics = g.getFontMetrics();
            String str = currentGame.getCurrentPickup().getPickUpType().toString() + "!";
            g.drawString(str, Utils.toPixel(body.getPosition().x) - metrics.stringWidth(str)/2, Utils.toPixel(body.getPosition().y) + metrics.getAscent()/2);
        }
        g.setTransform(old);
    }
        

    @Override
    public void update() {
        leftPressed = false;
        rightPressed = false;
        if(MainMenu.currentGame == null || cpuOwned)
            return;
        var keys = MainMenu.currentGame.getKeyManager().getKeys();
        int speed = keys.get(KeyEvent.VK_SHIFT) ? MOVE_SPEED * BOOST : MOVE_SPEED;
        if(keys.get(KeyEvent.VK_LEFT) || keys.get(KeyEvent.VK_A)) {
            left(speed);
        }
        if(keys.get(KeyEvent.VK_RIGHT) || keys.get(KeyEvent.VK_D)) {
            right(speed);
        }
        if(keys.get(KeyEvent.VK_SPACE)) {
            if(Utils.toPixel(body.getPosition().y) > Game.HEIGHT - 100)
                body.setTransform(body.getPosition().addLocal(0, Utils.toWorld(-speed)), 0f);
        } else if(Utils.toPixel(body.getPosition().y) < Game.HEIGHT - 50)
            body.setTransform(body.getPosition().addLocal(0, Utils.toWorld(speed)), 0f);

        if(keys.get(KeyEvent.VK_COMMA) || keys.get(KeyEvent.VK_Q)) {
            rotateL();
        } else if(keys.get(KeyEvent.VK_PERIOD) || keys.get(KeyEvent.VK_E)) {
            rotateR();
        } else
            body.setTransform(body.getPosition(), 0f);
    }

    public void left(int speed) {
        leftPressed = true;
        if(Utils.toPixel(body.getPosition().x - width/2) > 0)
            body.setTransform(body.getPosition().addLocal(Utils.toWorld(-speed), 0), 0f);
        else
            body.setTransform(body.getPosition(), 0f);
    }

    public void right(int speed) {
        rightPressed = true;
        if(Utils.toPixel(body.getPosition().x + width/2) < Game.WIDTH - 1)
            body.setTransform(body.getPosition().addLocal(Utils.toWorld(speed), 0), 0f);
        else
            body.setTransform(body.getPosition(), 0f);
    }

    public void rotateL() {
        if(cpuOwned && Game.currentGameMode == GameMode.VERSUS)
            body.setTransform(body.getPosition(), (float) Math.toRadians(ROTATION));
        else
            body.setTransform(body.getPosition(), (float) Math.toRadians(-ROTATION));
    }

    public void rotateR() {
        if(cpuOwned && Game.currentGameMode == GameMode.VERSUS)
            body.setTransform(body.getPosition(), (float) Math.toRadians(-ROTATION));
        else
            body.setTransform(body.getPosition(), (float) Math.toRadians(ROTATION));
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public void setCpuOwned(boolean cpuOwned) {
        this.cpuOwned = cpuOwned;
    }

    public boolean isCpuOwned() {
        return cpuOwned;
    }
    
}
