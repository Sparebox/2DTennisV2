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
        int randomTime = Math.min(maxInterval, Math.max(random.nextInt(maxInterval+1), minInterval));
        this.randomTimer = new Timer((int) (randomTime*1e3));
    }

    public void update() {
        if(randomTimer.tick()) {
            int x = random.nextInt(Game.WIDTH);
            if(x < PICKUP_WIDTH/2)
                x = PICKUP_WIDTH/2;
            else if(x + PICKUP_WIDTH/2 > Game.WIDTH)
                x = Game.WIDTH - PICKUP_WIDTH/2;
            currentPickup = new Pickup(x, PICKUP_HEIGTH/2, PICKUP_WIDTH, PICKUP_HEIGTH, PickUpType.ROCKET);
            currentGame.getEntitiesToAdd().add(currentPickup);
            setNewRandomInterval();
        }
    }

    public static void pickedUp(Pickup p) {
        applyPickup(p);
        currentGame.getEntitiesToDelete().add(p);
    }

    private void setNewRandomInterval() {
        int randomTime = Math.min(maxInterval, Math.max(random.nextInt(maxInterval+1), minInterval));
        randomTimer.setIntervalMs((int) (randomTime*1e3));
    }

    private static void applyPickup(Pickup pickup) {
        switch(pickup.getPickUpType()) {
            case ROCKET :
                Entity rocket = new Rocket(Utils.toPixel(pickup.getBody().getPosition().x), Utils.toPixel(pickup.getBody().getPosition().y),
                Rocket.WIDTH, Rocket.HEIGHT);
                currentGame.getEntitiesToAdd().add(rocket);
                break;
        }
    }

}
