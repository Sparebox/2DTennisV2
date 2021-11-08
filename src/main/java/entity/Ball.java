package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

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
import utils.Timer;
import utils.Utils;

public final class Ball extends Entity {

    public static final float BOOST_F = 1.5f;
    public static final float VEL_DEFAULT = 5f;
    public static final float VEL_VERSUS = 8f;
    public static final int VORTEX_AREA = 50; // In pixels
    public static final float VORTEX_POWER = 0.01f;

    public static float vel = VEL_DEFAULT;

    private CircleShape cs;
    private float radius;
    private boolean inBubble;
    private boolean inVortex;
    private BufferedImage bubbleSprite;
    private BufferedImage vortexSprites;
    private Animation vortexAnimation;
    private Timer vortexTimer;

    public Ball(int x, int y, int radius) {
        this.vortexTimer = new Timer(100);
        try {
            this.bubbleSprite = ImageIO.read(getClass().getResource("/bubble.png"));
            this.vortexSprites = ImageIO.read(getClass().getResource("/vortex_sprites.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not load sprite images");
            System.exit(1);
        }
        this.vortexAnimation = new Animation(50, Animation.cropSprites(vortexSprites, 12));
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
        if(inVortex)
            vortexAnimation.drawAnimation(g, 
            Utils.toPixel(body.getPosition().x - radius*3), 
            Utils.toPixel(body.getPosition().y - radius*3), 
            Game.BALL_RADIUS*6, Game.BALL_RADIUS*6);
    }

    @Override
    public void update() {
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
        if(velocity.abs().y < 0.1f) {
            if(velocity.y > 0)
                velocity.y += 0.5f;
            else 
                velocity.y -= 0.5f;
        }
        if(inVortex && vortexTimer.tick())
            simulateVortex();

        // Game over conditions //
        if(Utils.toPixel(body.getPosition().y + radius) > Game.height &&
        !inBubble &&
        (Game.currentGameMode == GameMode.SINGLE ||
        Game.currentGameMode == GameMode.CPU ||
        Game.currentGameMode == GameMode.VERSUS)) {
            if(currentGame != null) {
                currentGame.endGame(false);
            }
        }
        if(Utils.toPixel(body.getPosition().y - radius) < 0f && 
        Game.currentGameMode == game.GameMode.VERSUS && !inBubble) {
            if(currentGame != null) {
                currentGame.endGame(true);
            }
        }
    }

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
            applyVortex(b, body.getPosition(), bCOM, VORTEX_POWER);
            System.out.println("applied vortex");
        }
    }

    private void applyVortex(Body b, Vec2 vortexCenter, Vec2 applyPoint, float vortexPower) {
        Vec2 vortexDir = vortexCenter.sub(applyPoint);
        float distance = vortexDir.normalize();
        if(distance == 0)
            return;
        float invDistance = 1 / distance;
        float impulseMag = vortexPower * invDistance * invDistance;
        b.applyLinearImpulse(vortexDir.mul(impulseMag), applyPoint);
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
    
}
