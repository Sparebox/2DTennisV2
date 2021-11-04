package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import game.Game;
import game.GameMode;
import utils.Utils;

public final class Ball extends Entity {

    public static final float BOOST_F = 1.5f;
    public static final float VEL_DEFAULT = 5f;

    public static float vel = VEL_DEFAULT;

    private CircleShape cs;
    private float radius;
    private boolean inBubble;
    private Image bubbleSprite;

    public Ball(int x, int y, int radius) {
        this.bubbleSprite = new ImageIcon(getClass().getResource("/bubble.png")).getImage();
        this.radius = Utils.toWorld(radius);
        this.bd = new BodyDef();
        this.bd.type = BodyType.DYNAMIC;
        this.bd.position.set(Utils.toWorld(x), Utils.toWorld(y));
        this.bd.allowSleep = false;
        this.bd.gravityScale = 0f;
        this.bd.linearVelocity = new Vec2(VEL_DEFAULT, VEL_DEFAULT);

        this.cs = new CircleShape();
        this.cs.setRadius(this.radius/2);

        this.fd = new FixtureDef();
        this.fd.filter.categoryBits = CollisionCategory.BALL.BIT;
        this.fd.shape = cs;
        this.fd.density = 0.1f;
        this.fd.friction = 0f;
        this.fd.restitution = 1f;
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillOval(Utils.toPixel(body.getPosition().x - radius/2), Utils.toPixel(body.getPosition().y - radius/2), Utils.toPixel(radius), Utils.toPixel(radius));
        if(inBubble)
            g.drawImage(bubbleSprite, Utils.toPixel(body.getPosition().x - radius*2), Utils.toPixel(body.getPosition().y - radius*2), Game.BALL_RADIUS*4, Game.BALL_RADIUS*4, null);
    }

    @Override
    public void update() {
        Vec2 velocity = body.getLinearVelocity();
        if(velocity.length() != vel) {
            velocity.normalize();
            velocity.mulLocal(vel);
        }
        if(velocity.abs().y < 0.1f) {
            if(velocity.y > 0)
                velocity.y += 0.5f;
            else 
                velocity.y -= 0.5f;
        }
        if(Utils.toPixel(body.getPosition().y + radius) > Game.HEIGHT &&
        !inBubble &&
        (Game.currentGameMode == GameMode.SINGLE ||
        Game.currentGameMode == GameMode.CPU ||
        Game.currentGameMode == GameMode.VERSUS)) {
            if(currentGame != null) {
                currentGame.endGame();
            }
        }
        if(Utils.toPixel(body.getPosition().y - radius) < 0f && 
        Game.currentGameMode == game.GameMode.VERSUS && !inBubble) {
            if(currentGame != null) {
                currentGame.endGame();
            }
        }
        
            
    }

    public void setInBubble(boolean inBubble) {
        this.inBubble = inBubble;
    }

    public boolean isInBubble() {
        return inBubble;
    }
    
}
