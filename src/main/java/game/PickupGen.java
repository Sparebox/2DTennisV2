package game;

import java.util.Random;

import org.jbox2d.collision.shapes.CircleShape;
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

    public static int maxInterval = 5;  // 25
    public static int minInterval = 1;  // 15

    private static Game currentGame;
    
    private Timer randomTimer;
    private Timer resetTimer;
    private Random random;
    private Pickup pickupToBeApplied;
    
    public PickupGen(Game currentGame) {
        PickupGen.currentGame = currentGame;
        this.random = new Random();
        int randomTime = Math.min(maxInterval, Math.max(random.nextInt(maxInterval+1), minInterval));
        this.randomTimer = new Timer((int) (randomTime*1e3));
        this.resetTimer = new Timer(RESET_TIME);
        resetEffects();
    }

    public void update() {
        if(randomTimer.tick() && currentGame.getCurrentPickup() == null) {
            int x = random.nextInt(Game.WIDTH);
            if(x < PICKUP_WIDTH/2)
                x = PICKUP_WIDTH/2;
            else if(x + PICKUP_WIDTH/2 > Game.WIDTH)
                x = Game.WIDTH - PICKUP_WIDTH/2;
            int ordinal = random.nextInt(PickUpType.values().length);
            PickUpType type = PickUpType.values()[ordinal];
            type = PickUpType.BUBBLE;
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
        int randomTime = Math.min(maxInterval, Math.max(random.nextInt(maxInterval+1), minInterval));
        randomTimer.setIntervalMs((int) (randomTime*1e3));
    }

    private void applyPickup(Pickup pickup) {
        switch(pickup.getPickUpType()) {
            case ROCKET :
                Entity rocket = new Rocket(Utils.toPixel(pickup.getBody().getPosition().x), Utils.toPixel(pickup.getBody().getPosition().y),
                Rocket.WIDTH, Rocket.HEIGHT);
                currentGame.getEntitiesToAdd().add(rocket);
                break;
            case SPEED_UP :
                Ball.vel = Ball.VEL_DEFAULT * Ball.BOOST_F;
                break;
            case BUBBLE :
                createBubble();
                break;
        }
    }

    private void resetEffects() {
        Ball.vel = Ball.VEL_DEFAULT;
        if(currentGame.getBall() != null) {
            destroyBubble();
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

    private void destroyBubble() {
        Ball ball = currentGame.getBall();
        Vec2 destroyedPos = ball.getBody().getPosition().clone();
        Vec2 destroyedVel = ball.getBody().getLinearVelocity().clone();
        currentGame.getPhysWorld().destroyBody(ball.getBody());
        ball.getBd().position = destroyedPos;
        ball.getFd().shape.setRadius(Utils.toWorld(Game.BALL_RADIUS/2));
        ball.setBody(currentGame.getPhysWorld().createBody(ball.getBd()));
        ball.getBody().createFixture(ball.getFd());
        ball.getBody().setLinearVelocity(destroyedVel);
        currentGame.getBall().setInBubble(false);
    }

    public void setPickupToBeApplied(Pickup pickupToBeApplied) {
        this.pickupToBeApplied = pickupToBeApplied;
    }

    public Pickup getPickupToBeApplied() {
        return pickupToBeApplied;
    }

}
