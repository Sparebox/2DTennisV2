package game;

import java.util.Random;

import org.jbox2d.common.Vec2;

import entity.Entity;
import entity.PickUpType;
import entity.Pickup;
import entity.Rocket;
import utils.Timer;
import utils.Utils;


public final class PickupGen {
    
    public static final int PICKUP_WIDTH = 30;
    public static final int PICKUP_HEIGTH = 30;

    public static int maxInterval = 25;
    public static int minInterval = 15;

    private static Game currentGame;

    private Timer randomTimer;
    private Random random;
    private Pickup currentPickup;

    public PickupGen(Game currentGame) {
        PickupGen.currentGame = currentGame;
        this.random = new Random();
        this.randomTimer = new Timer((int)1e3);
    }

    public void update() {
        if(randomTimer.tick()) {
            int x = random.nextInt(Game.WIDTH);
            if(x < PICKUP_WIDTH/2)
                x = PICKUP_WIDTH/2;
            else if(x + PICKUP_WIDTH/2 > Game.WIDTH)
                x = Game.WIDTH - PICKUP_WIDTH/2;
            currentPickup = new Pickup(x, PICKUP_HEIGTH/2, PICKUP_WIDTH, PICKUP_HEIGTH, PickUpType.ROCKET);
            currentGame.getEntities().add(currentPickup);
            setNewRandomInterval();
        }
    }

    public static void pickedUp(Pickup p) {
        //applyPickup(p);
        p.destroy();
    }

    private void setNewRandomInterval() {
        int randomTime = random.nextInt(maxInterval+1);
        if(randomTime < minInterval)
            randomTime = minInterval;
        randomTimer.setIntervalMs((int) (randomTime*1e3));
    }

    private static void applyPickup(Pickup pickup) {
        switch(pickup.getPickUpType()) {
            case ROCKET :
                Entity rocket = new Rocket(Utils.toPixel(pickup.getBody().getPosition().x), Utils.toPixel(pickup.getBody().getPosition().y),
                Rocket.WIDTH, Rocket.HEIGHT);
                rocket.setBody(currentGame.getPhysWorld().createBody(rocket.getBd()));
                rocket.getBody().createFixture(rocket.getFd());
                rocket.getBody().setGravityScale(0f);
                rocket.getBody().setUserData(rocket);
                rocket.getBody().setLinearVelocity(new Vec2(0, -5f));
                currentGame.addEntity(rocket); 
                break;
        }
    }

}
