package game;

import java.util.Random;

import entity.Pickup;
import entity.PickUpType;
import timer.Timer;

public final class PickupGen {
    
    public static final int PICKUP_WIDTH = 30;
    public static final int PICKUP_HEIGTH = 30;

    public static int maxInterval = 20;
    public static int minInterval = 10;

    private Game currentGame;
    private Timer randomTimer;
    private Random random;
    private Pickup currentPickup;

    public PickupGen(Game currentGame) {
        this.currentGame = currentGame;
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
        applyPickup(p.getPickUpType());
        p.destroy();
    }

    private void setNewRandomInterval() {
        int randomTime = random.nextInt(maxInterval+1);
        if(randomTime < minInterval)
            randomTime = minInterval;
        randomTimer.setIntervalMs((int) (randomTime*1e3));
    }

    private static void applyPickup(PickUpType type) {
        switch(type) {
            case ROCKET :
                break;
        }
    }

}
