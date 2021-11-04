package game;

import org.jbox2d.common.Vec2;

import entity.Ball;
import entity.Entity;
import entity.Pickup;
import entity.Racquet;
import entity.Tile;
import utils.Timer;
import utils.Utils;
import window.MainMenu;

public final class Bot {
    
    public static final int SPEED = Racquet.MOVE_SPEED * Racquet.BOOST;
    
    private Game currentGame;
    private Racquet cpuRacquet;
    private Ball ball;
    private Vec2 ballPos;
    private Vec2 cpuRacquetPos;
    private Vec2 predictedBallPos;
    private Vec2 ballDir;
    private Vec2 lastBallDir;
    private boolean initialUpdate = true;
    private boolean allowAiming = true;
    private float averageTileX;
    private Timer aimTimeout;
    private int timeout = (int) 10e3;
    private int lastTileCount;

    public Bot(Game currentGame) {
        this.currentGame = currentGame;
        this.lastBallDir = new Vec2();
        this.aimTimeout = new Timer(timeout);
    }

    public void update() {
        if(initialUpdate) {
            this.ball = currentGame.getBall();
            this.cpuRacquet = currentGame.getCpuRacquet();
            this.ballPos = this.ball.getBody().getPosition();
            this.cpuRacquetPos = this.cpuRacquet.getBody().getPosition();
            initialUpdate = false;
        }
        ballDir = ball.getBody().getLinearVelocity().clone();
        ballDir.normalize();
        predictedBallPos = ballPos.add(ballDir);
        if(!lastBallDir.equals(ballDir)) {
            if(Game.currentGameMode == GameMode.CPU) {
                while(predictedBallPos.y <= cpuRacquetPos.y && ballDir.y > 0f) {
                    predictedBallPos.addLocal(ballDir);
                }
            } else if(Game.currentGameMode == GameMode.VERSUS) {
                while(predictedBallPos.y >= cpuRacquetPos.y && ballDir.y < 0f) {
                    predictedBallPos.addLocal(ballDir);
                }
            }
        }
        lastBallDir = ballDir.clone();
        if(Game.currentGameMode == GameMode.CPU) {
            if(cpuRacquetPos.x + cpuRacquet.getWidth()/3 < predictedBallPos.x && ballPos.y > Utils.toWorld(Game.HEIGHT/3)) {
                if(ball.getBody().getLinearVelocity().x > 0f) {
                    cpuRacquet.right(SPEED);
                }
            } else if(cpuRacquetPos.x - cpuRacquet.getWidth()/3 > predictedBallPos.x && ballPos.y > Utils.toWorld(Game.HEIGHT/3)) {
                if(ball.getBody().getLinearVelocity().x < 0f) {
                    cpuRacquet.left(SPEED);
                }
            }
        } else if(Game.currentGameMode == GameMode.VERSUS) {
            if(cpuRacquetPos.x + cpuRacquet.getWidth()/3 < predictedBallPos.x && ballPos.y < Utils.toWorld(Game.HEIGHT - Game.HEIGHT/3)) {
                if(ball.getBody().getLinearVelocity().x > 0f) {
                    cpuRacquet.right(SPEED);
                }
            } else if(cpuRacquetPos.x - cpuRacquet.getWidth()/3 > predictedBallPos.x && ballPos.y < Utils.toWorld(Game.HEIGHT - Game.HEIGHT/3)) {
                if(ball.getBody().getLinearVelocity().x < 0f) {
                    cpuRacquet.left(SPEED);
                }
            }
        }
        
        
        if(ball.getBody().getLinearVelocity().abs().x < 0.01f) { // Avoid x velocity stall
            if(cpuRacquetPos.x - cpuRacquet.getWidth()/3 > predictedBallPos.x) {
                cpuRacquet.left(SPEED);
            }
            else if(cpuRacquetPos.x + cpuRacquet.getWidth()/3 < predictedBallPos.x) {
                cpuRacquet.right(SPEED);
            }
            if(cpuRacquetPos.x < predictedBallPos.x)
                cpuRacquet.rotateL();
            else 
                cpuRacquet.rotateR();
        } else if(cpuRacquetPos.y - ballPos.y < cpuRacquet.getHeight() * 2 && Game.currentGameMode == GameMode.CPU) {
            if(cpuRacquetPos.x + cpuRacquet.getWidth() < predictedBallPos.x) {
                cpuRacquet.rotateR();
            }
                
            else if(cpuRacquetPos.x - cpuRacquet.getWidth() > predictedBallPos.x) {
                cpuRacquet.rotateL();
            }
                
        }

        if(Game.currentGameMode == GameMode.CPU) {
            considerPickup();
            considerAiming();
        }
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
        cpuRacquetPos.sub(pickup.getBody().getPosition()).lengthSquared() >
        cpuRacquetPos.sub(ballPos).lengthSquared())
            return;
        if(cpuRacquetPos.x + cpuRacquet.getWidth() / 2 < pickup.getBody().getPosition().x)
            cpuRacquet.right(SPEED);
        else if(cpuRacquetPos.x - cpuRacquet.getWidth() / 2 > pickup.getBody().getPosition().x)
            cpuRacquet.left(SPEED);
    }

    private void considerAiming() {
        int tileCount = MainMenu.tileAmount - currentGame.getScore();
        if(tileCount < 21) {
            if(lastTileCount != tileCount) {
                aimTimeout.reset();
                allowAiming = true;
                float xSum = 0f;
                for(Entity e : currentGame.getEntities()) {
                    if(e instanceof Tile t) 
                        xSum += t.getBody().getPosition().x;
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
                if(minDist > Utils.toWorld(Game.TILE_WIDTH * 2))
                    allowAiming = false;
            }
            lastTileCount = tileCount;
            if(aimTimeout.tick())
                allowAiming = false;

            if(!allowAiming)
                return;
            if(averageTileX > cpuRacquetPos.x && ballDir.y > 0f) {
                cpuRacquet.rotateR();
            }
            else if(averageTileX < cpuRacquetPos.x && ballDir.y > 0f) {
                cpuRacquet.rotateL();
            }
        }
    }

    public Vec2 getPredictedBallPos() {
        return predictedBallPos;
    }

    public float getAverageTileX() {
        return averageTileX;
    }

}
