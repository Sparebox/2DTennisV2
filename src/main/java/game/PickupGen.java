package game;

import java.util.Random;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;

import entity.Ball;
import entity.Entity;
import entity.PickUpType;
import entity.Pickup;
import entity.Rocket;
import utils.Timer;
import utils.Utils;

public final class PickupGen {
    
    public static final int PICKUP_WIDTH = 30;
    public static final int PICKUP_HEIGTH = 30;
    public static final int RESET_TIME = (int) 5e3;

    public static final int MAX_INTERVAL = 3; // 25
    public static final int MIN_INTERVAL = 3; // 12 

    private static Game currentGame;
    
    private Timer randomTimer;
    private Timer resetTimer;
    private Random random;
    private Pickup pickupToBeApplied;
    
    public PickupGen(Game currentGame) {
        PickupGen.currentGame = currentGame;
        this.random = new Random();
        int randomTime = Math.min(MAX_INTERVAL, Math.max(random.nextInt(MAX_INTERVAL+1), MIN_INTERVAL));
        this.randomTimer = new Timer((int) (randomTime*1e3));
        this.resetTimer = new Timer(RESET_TIME);
        resetEffects();
    }

    public void update() {
        if(randomTimer.tick() && currentGame.getCurrentPickup() == null) {
            int x = random.nextInt(Game.width);
            if(x < PICKUP_WIDTH/2)
                x = PICKUP_WIDTH/2;
            else if(x + PICKUP_WIDTH/2 > Game.width)
                x = Game.width - PICKUP_WIDTH/2;
            int ordinal = random.nextInt(PickUpType.values().length);
            PickUpType type = PickUpType.values()[ordinal];
            type = PickUpType.SQUARE;
            var pickup = new Pickup(x, PICKUP_HEIGTH/2, PICKUP_WIDTH, PICKUP_HEIGTH, type);
            currentGame.getEntitiesToAdd().add(pickup);
            setNewRandomInterval();
        }
        if(resetTimer.tick() && currentGame.getCurrentPickup() != null) {
            resetEffects();
        }
        if(pickupToBeApplied != null) {
            applyPickup(pickupToBeApplied);
            currentGame.getEntitiesToDelete().add(pickupToBeApplied);
            currentGame.setCurrentPickup(pickupToBeApplied);
            currentGame.setPickupsPickedup(currentGame.getPickupsPickedup() + 1);
            resetTimer.reset();
            pickupToBeApplied = null;
        }
    }

    private void setNewRandomInterval() {
        int randomTime = Math.min(MAX_INTERVAL, Math.max(random.nextInt(MAX_INTERVAL+1), MIN_INTERVAL));
        randomTimer.setIntervalMs((int) (randomTime*1e3));
    }

    private void applyPickup(Pickup pickup) {
        switch(pickup.getPickUpType()) {
            case ROCKET :
                Entity rocket = new Rocket(Utils.toPixel(pickup.getBody().getPosition().x), Utils.toPixel(pickup.getBody().getPosition().y),
                Rocket.WIDTH, Rocket.HEIGHT);
                currentGame.getEntitiesToAdd().add(rocket);
                break;
            case BOOST :
                Ball.vel = Ball.VEL_DEFAULT * Ball.BOOST_F;
                break;
            case BUBBLE :
                createBubble();
                Ball.vel = Ball.VEL_DEFAULT * Ball.BOOST_F * 1.5f;
                break;
            case VORTEX :
                currentGame.getBall().setInVortex(true);
                break;
            case SQUARE :
                createSquare();
                break;
        }
    }

    private void resetEffects() {
        Ball.vel = Ball.VEL_DEFAULT;
        if(currentGame.getBall() != null) {
            restoreBall();
        }
        currentGame.setCurrentPickup(null);
    }

    private void createBubble() {
        Ball ball = currentGame.getBall();
        Vec2 destroyedPos = ball.getBody().getPosition().clone();
        Vec2 destroyedVel = ball.getBody().getLinearVelocity().clone();
        currentGame.getPhysWorld().destroyBody(ball.getBody());
        ball.getBd().position = destroyedPos;
        ball.getFd().shape.setRadius(Utils.toWorld(Game.BALL_RADIUS*2));
        ball.setBody(currentGame.getPhysWorld().createBody(ball.getBd()));
        ball.getBody().createFixture(ball.getFd());
        ball.getBody().setLinearVelocity(destroyedVel);
        currentGame.getBall().setInBubble(true);
    }

    private void restoreBall() {
        Ball ball = currentGame.getBall();
        Vec2 destroyedPos = ball.getBody().getPosition().clone();
        Vec2 destroyedVel = ball.getBody().getLinearVelocity().clone();
        currentGame.getPhysWorld().destroyBody(ball.getBody());
        ball.getBd().position = destroyedPos;
        CircleShape cs = new CircleShape();
        cs.setRadius(Utils.toWorld(Game.BALL_RADIUS/2));
        ball.getFd().shape = cs;
        ball.setBody(currentGame.getPhysWorld().createBody(ball.getBd()));
        ball.getBody().createFixture(ball.getFd());
        ball.getBody().setLinearVelocity(destroyedVel);
        ball.setInBubble(false);
        ball.setInVortex(false);
        ball.setSquare(false);
    }

    private void createSquare() {
        Ball ball = currentGame.getBall();
        Vec2 destroyedPos = ball.getBody().getPosition().clone();
        Vec2 destroyedVel = ball.getBody().getLinearVelocity().clone();
        currentGame.getPhysWorld().destroyBody(ball.getBody());
        ball.getBd().position = destroyedPos;
        PolygonShape ps = new PolygonShape();
        ps.setAsBox(Utils.toWorld(Game.BALL_RADIUS), Utils.toWorld(Game.BALL_RADIUS));
        ball.getFd().shape = ps;
        ball.setBody(currentGame.getPhysWorld().createBody(ball.getBd()));
        ball.getBody().createFixture(ball.getFd());
        ball.getBody().setLinearVelocity(destroyedVel);
        ball.getBody().setAngularVelocity(0.1f);
        ball.setSquare(true);
    }

    public void setPickupToBeApplied(Pickup pickupToBeApplied) {
        this.pickupToBeApplied = pickupToBeApplied;
    }

    public Pickup getPickupToBeApplied() {
        return pickupToBeApplied;
    }

}
