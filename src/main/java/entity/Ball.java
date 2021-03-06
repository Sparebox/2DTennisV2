package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import game.AreaQueryCallback;
import game.Game;
import game.GameMode;
import game.PickupGen;
import utils.Timer;
import utils.Utils;

/**
 * Physics ball
 */
public final class Ball extends Entity {

    public static final float BOOST_F = 1.5f;
    public static final float VEL_DEFAULT = 5f;
    public static final float VEL_VERSUS = 8f;
    public static final int VORTEX_AREA = 50; // In pixels
    public static final float VORTEX_POWER = 0.01f;
    public static final int BUBBLE_FLASH_INTERVAL = 50; // Milliseconds

    public static float vel = VEL_DEFAULT;

    private CircleShape cs;
    private float radius;
    private boolean inBubble;
    private boolean inVortex;
    private boolean isSquare;
    private boolean frozen;
    private BufferedImage bubbleSprite;
    private BufferedImage vortexSprites;
    private Animation vortexAnimation;
    private Timer vortexTimer;
    private Timer secTimer;
    private Timer bubbleFlashTimer;
    private int bubbleSeconds;

    /**
     * Creates and initializes JBox2D physics for a new ball
     * @param x the initial x-coordinate
     * @param y the initial y-coordinate
     * @param radius the radius of the ball in pixels
     */
    public Ball(int x, int y, int radius) {
        this.vortexTimer = new Timer(100);
        this.secTimer = new Timer((int) 1e3);
        this.bubbleFlashTimer = new Timer(BUBBLE_FLASH_INTERVAL);
        try {
            this.bubbleSprite = ImageIO.read(getClass().getResource("/bubble.png"));
            this.vortexSprites = ImageIO.read(getClass().getResource("/vortex_sprites.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not load sprite images");
            System.exit(1);
        }
        this.vortexAnimation = new Animation(25, Animation.cropSprites(vortexSprites, 12));
        this.radius = Utils.toWorld(radius);
        this.bd = new BodyDef();
        this.bd.type = BodyType.DYNAMIC;
        this.bd.position.set(Utils.toWorld(x), Utils.toWorld(y));
        this.bd.allowSleep = false;
        this.bd.gravityScale = 0f;
        this.bd.linearVelocity = new Vec2(VEL_DEFAULT, VEL_DEFAULT);
        this.bd.userData = this;

        this.cs = new CircleShape();
        this.cs.setRadius(this.radius/2);

        this.fd = new FixtureDef();
        this.fd.filter.categoryBits = CollisionCategory.BALL.BIT;
        this.fd.shape = cs;
        this.fd.density = 1f;
        this.fd.friction = 0f;
        this.fd.restitution = 1f;
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.WHITE);
        if(isSquare) {
            AffineTransform old = g.getTransform();
            g.rotate(body.getAngle(), Utils.toPixel(body.getPosition().x), Utils.toPixel(body.getPosition().y));
            g.fillRect(Utils.toPixel(body.getPosition().x - radius), Utils.toPixel(body.getPosition().y - radius), Utils.toPixel(radius*2), Utils.toPixel(radius*2));
            g.setTransform(old);
        }
        else
            g.fillOval(Utils.toPixel(body.getPosition().x - radius/2), Utils.toPixel(body.getPosition().y - radius/2), Utils.toPixel(radius), Utils.toPixel(radius));
        if(inBubble) {
            if(secTimer.tick())
                bubbleSeconds++;
            if(bubbleSeconds < (PickupGen.RESET_TIME - 1e3)/1e3)
                g.drawImage(bubbleSprite, Utils.toPixel(body.getPosition().x - radius*2),
                Utils.toPixel(body.getPosition().y - radius*2), Game.BALL_RADIUS*4,
                Game.BALL_RADIUS*4, null);
            else if(bubbleFlashTimer.tick())
                g.drawImage(bubbleSprite, Utils.toPixel(body.getPosition().x - radius*2),
                Utils.toPixel(body.getPosition().y - radius*2), Game.BALL_RADIUS*4,
                Game.BALL_RADIUS*4, null);
        }
        if(inVortex)
            vortexAnimation.draw(g, Utils.toPixel(body.getPosition().x - radius*3),
            Utils.toPixel(body.getPosition().y - radius*3), 
            Game.BALL_RADIUS*6, Game.BALL_RADIUS*6);
    }

    @Override
    public void update() {
        if(currentGame == null)
            return;
        if(!frozen) {
            Vec2 velocity = body.getLinearVelocity();
            if(Game.currentGameMode == GameMode.VERSUS) {
                if(velocity.length() != VEL_VERSUS) {
                    velocity.normalize();
                    velocity.mulLocal(VEL_VERSUS);
                }
            }
            if(Game.currentGameMode == GameMode.CPU ||
            Game.currentGameMode == game.GameMode.SINGLE) {
                if(velocity.length() != vel) {
                    velocity.normalize();
                    velocity.mulLocal(vel);
                }
            }
            if(velocity.abs().y < 0.01f) {
                if(velocity.y > 0)
                    velocity.y += 0.5f;
                else 
                    velocity.y -= 0.5f;
            }
        }
        
        if(inVortex && vortexTimer.tick())
            simulateVortex();

        // Game over conditions //

        // Lose conditions //

        if(Utils.toPixel(body.getPosition().y + radius) >= Game.HEIGHT &&
        !inBubble && !isSquare &&
        (Game.currentGameMode == GameMode.SINGLE ||
        Game.currentGameMode == GameMode.CPU ||
        Game.currentGameMode == GameMode.VERSUS)) {
            currentGame.endGame(false);
        }
        else if(isSquare && Utils.toPixel(body.getPosition().y + radius * 2) >= Game.HEIGHT) {
            currentGame.endGame(false);
        }
        // Win conditions //
        else if(Utils.toPixel(body.getPosition().y - radius) <= 0f && 
        Game.currentGameMode == GameMode.VERSUS) {
            currentGame.endGame(true);
        }
        
    }

    /**
     * Applies physics effects to tiles in the vicinity of the ball
     */
    private void simulateVortex() {
        AreaQueryCallback callBack = new AreaQueryCallback();
        AABB area = new AABB(body.getPosition().sub(new Vec2(Utils.toWorld(VORTEX_AREA), Utils.toWorld(VORTEX_AREA))),
                             body.getPosition().add(new Vec2(Utils.toWorld(VORTEX_AREA), Utils.toWorld(VORTEX_AREA))));
        currentGame.getPhysWorld().queryAABB(callBack, area);
        for(Body b : callBack.getFoundBodies()) {
            if(b.getUserData() instanceof Ball)
                continue;
            Vec2 bCOM = b.getPosition().add(new Vec2(0.1f, 0));
            if(bCOM.sub(this.body.getPosition()).length() >= Utils.toWorld(VORTEX_AREA))
                continue;
            Utils.applyForce(b, body.getPosition(), bCOM, VORTEX_POWER);
        }
    }

    // Getters and setters

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
        if(frozen)
            this.body.getLinearVelocity().setZero();
        else
            this.body.setLinearVelocity(new Vec2(Ball.VEL_DEFAULT, Ball.VEL_DEFAULT));
    }

    public boolean isFrozen() {
        return frozen;
    }

    public Timer getBubbleTimer() {
        return secTimer;
    }

    public void setBubbleSeconds(int bubbleSeconds) {
        this.bubbleSeconds = bubbleSeconds;
    }

    public void setInBubble(boolean inBubble) {
        this.inBubble = inBubble;
    }

    public boolean isInBubble() {
        return inBubble;
    }

    public void setInVortex(boolean inVortex) {
        this.inVortex = inVortex;
    }

    public boolean isInVortex() {
        return inVortex;
    }

    public void setSquare(boolean isSquare) {
        this.isSquare = isSquare;
    }

    public boolean isSquare() {
        return isSquare;
    }
    
}
