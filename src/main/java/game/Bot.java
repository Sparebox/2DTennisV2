package game;

import org.jbox2d.common.Vec2;

import entity.Ball;
import entity.Entity;
import entity.Racquet;
import entity.Pickup;
import entity.Tile;

import utils.Utils;
import window.MainMenu;

public final class Bot {
    
    public static final int SPEED = Racquet.MOVE_SPEED * Racquet.BOOST;

    private Game currentGame;
    private Racquet racquet;
    private Ball ball;
    private Vec2 ballPos;
    private Vec2 racquetPos;
    private Vec2 predictedBallPos;
    private Vec2 ballDir;
    private boolean initialUpdate = true;
    private int lastTileCount;
    private float averageTileX;

    public Bot(Game currentGame) {
        this.currentGame = currentGame;
    }

    public void update() {
        if(initialUpdate) {
            for(Entity e : this.currentGame.getEntities()) {
                if(e instanceof Racquet r) {
                    this.racquet = r;
                }
                if(e instanceof Ball b) {
                    this.ball = b;
                }
                if(this.racquet != null && this.ball != null)
                    break;
            }
            this.ballPos = this.ball.getBody().getPosition();
            this.racquetPos = this.racquet.getBody().getPosition();
            initialUpdate = false;
        }
        ballDir = new Vec2(ball.getBody().getLinearVelocity());
        ballDir.normalize();
        predictedBallPos = ballPos.add(ballDir);
        while(predictedBallPos.y <= racquetPos.y && ballDir.y > 0f) {
            predictedBallPos.addLocal(ballDir);
        }
        
        if(racquetPos.x + racquet.getWidth() / 4 < predictedBallPos.x) {
            if(ball.getBody().getLinearVelocity().x > 0f) {
                racquet.right(SPEED);
            }
        } else if(racquetPos.x - racquet.getWidth() / 4 > predictedBallPos.x) {
            if(ball.getBody().getLinearVelocity().x < 0f)
                racquet.left(SPEED);
        }
        
        if(ball.getBody().getLinearVelocity().abs().x < 0.01f) {
            if(racquetPos.x - racquet.getWidth() / 4 > predictedBallPos.x)
                racquet.left(SPEED);
            else if(racquetPos.x + racquet.getWidth() / 4 < predictedBallPos.x)
                racquet.right(SPEED);
            if(racquetPos.x < predictedBallPos.x)
                racquet.rotateL();
            else 
                racquet.rotateR();
        } else if(racquetPos.y - ballPos.y < racquet.getHeight()) {
            if(predictedBallPos.x > racquetPos.x)
                racquet.rotateR();
            else
                racquet.rotateL();
        }

        considerPickup();
        considerAiming();
    }

    private void considerPickup() {
        Pickup pickup = null;
        if(ballPos.y < Utils.toWorld(Game.HEIGHT / 2) && ballDir.y < 0f) {
            for(Entity e : currentGame.getEntities()) {
                if(e instanceof Pickup p) {
                    pickup = p;
                    break;
                }
            }
        }
        if(pickup == null || 
        racquetPos.sub(pickup.getBody().getPosition()).lengthSquared() >
        racquetPos.sub(ballPos).lengthSquared())
            return;
        if(racquetPos.x + racquet.getWidth() / 2 < pickup.getBody().getPosition().x)
            racquet.right(SPEED);
        else if(racquetPos.x - racquet.getWidth() / 2 > pickup.getBody().getPosition().x)
            racquet.left(SPEED);
    }

    private void considerAiming() {
        int tileCount = MainMenu.tileAmount - currentGame.getScore();
        if(tileCount > 2 && tileCount < 21) {
            if(lastTileCount - tileCount != 0) {
                float xSum = 0f;
                for(Entity e : currentGame.getEntities()) {
                    if(e instanceof Tile t) {
                        xSum += t.getBody().getPosition().x;
                    } else 
                        continue;
                }
                averageTileX = xSum / tileCount;
                float minDist = Float.POSITIVE_INFINITY;
                for(Entity e : currentGame.getEntities()) {
                    if(e instanceof Tile t) {
                        float dist = Math.abs(t.getBody().getPosition().x - averageTileX);
                        if(dist < minDist)
                            minDist = dist;
                    }
                }
                if(minDist > Utils.toWorld(Game.TILE_WIDTH * 2)) {
                    return;
                }
            }
            if(averageTileX > racquetPos.x && ballDir.y > 0f && ballDir.x < 0f) 
                racquet.rotateR();
            else if(averageTileX < racquetPos.x && ballDir.y > 0f && ballDir.x > 0f)
                racquet.rotateL();
            lastTileCount = tileCount;
        }
    }

    public Vec2 getPredictedBallPos() {
        return predictedBallPos;
    }

    public float getAverageTileX() {
        return averageTileX;
    }

    
}
