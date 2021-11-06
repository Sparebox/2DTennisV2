package game;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import entity.Ball;
import entity.Entity;
import entity.LineBoundary;
import entity.Pickup;
import entity.Racquet;
import entity.Tile;
import utils.Timer;
import window.GameSummary;
import window.KeyManager;
import window.MainMenu;

public final class Game implements Runnable {

    public static int width = 800;
    public static int height = 1000;
    public static final Color BACKGROUND_COLOR = Color.BLACK;
    public static final int FPS_MAX = 300;
    public static final int FPS_MIN = 1;
    public static final int FPS_DEFAULT = 60;
    public static final int VEL_ITERATIONS = 6;
    public static final int POS_ITERATIONS = 3;
    public static final Vec2 GRAVITY = new Vec2(0, 9.81f);
    public static final int DEFAULT_TILES = 50;
    public static final int TILE_WIDTH = 40;
    public static final int TILE_HEIGTH = 20;
    public static final int BALL_RADIUS = 20;

    public static GameMode currentGameMode;
    public static double nsPerUpdate;

    private Pickup currentPickup;
    private World physWorld;
    private Thread gameThread;
    private JFrame frame;
    private Canvas canvas;
    private KeyManager keyManager;
    private CustomContactListener customContactListener;
    private PickupGen pickUpGen;
    private boolean running;
    private BufferStrategy bs;
    private String fpsString = "FPS: ";
    private Set<Entity> entities;
    private Set<Entity> entitiesToDelete;
    private Set<Entity> entitiesToAdd;
    private Racquet playerRacquet;
    private Racquet cpuRacquet;
    private Ball ball;
    private Timer secTimer;
    private int ticks;
    private int score;
    private int secondsSinceStart;
    private int pickupsPickedup;
    private Bot bot;
    private Image arrowLeft;
    private Image arrowRight;
    
    public Game() {
        init();
    }

    private void init() {
        nsPerUpdate = 1e9 / MainMenu.fpsTarget;
        this.keyManager = new KeyManager(this);
        this.secTimer = new Timer((int)1e3);
        this.entities = new HashSet<>();
        this.entitiesToDelete = new HashSet<>();
        this.entitiesToAdd = new HashSet<>();
        this.customContactListener = new CustomContactListener(this);
        physWorld = new World(GRAVITY);
        physWorld.setContactListener(this.customContactListener);
        Entity.setCurrentGame(this);
        initFrame();
        initCanvas();
        createBoundaries(false);
        int lastTileY;
        switch(currentGameMode) {
            case CPU :
                this.pickUpGen = new PickupGen(this);
                this.cpuRacquet = new Racquet(width/2, 200, 20);
                this.cpuRacquet.setCpuOwned(true);
                lastTileY = createTiles();
                this.ball = new Ball(width/2, lastTileY + 2 * BALL_RADIUS, BALL_RADIUS);
                entitiesToAdd.add(cpuRacquet);
                entitiesToAdd.add(ball);
                this.bot = new Bot(this);
                this.arrowLeft = new ImageIcon(getClass().getResource("/leftkey.png")).getImage();
                this.arrowRight = new ImageIcon(getClass().getResource("/rightkey.png")).getImage();
                break;
            case SINGLE :
                this.pickUpGen = new PickupGen(this);
                this.playerRacquet = new Racquet(width/2, 200, 20);
                lastTileY = createTiles();
                this.ball = new Ball(width/2, lastTileY + 2 * BALL_RADIUS, BALL_RADIUS);
                entitiesToAdd.add(playerRacquet);
                entitiesToAdd.add(ball);
                break;
            case VERSUS :
                this.playerRacquet = new Racquet(width/2, 200, 20);
                this.cpuRacquet = new Racquet(width/2, 50, 200, 20);
                this.cpuRacquet.setCpuOwned(true);
                this.ball = new Ball(width/2, 100, BALL_RADIUS);
                entitiesToAdd.add(playerRacquet);
                entitiesToAdd.add(cpuRacquet);
                entitiesToAdd.add(ball);
                this.bot = new Bot(this);
                break;
            // case TUTORIAL :
            //     this.pickUpGen = new PickupGen(this);
            //     this.arrowLeft = new ImageIcon(getClass().getResource("/leftkey.png")).getImage();
            //     this.arrowRight = new ImageIcon(getClass().getResource("/rightkey.png")).getImage();
            //     break;
        }
    }

    private void initFrame() {
        this.frame = new JFrame();
        this.frame.setSize(width, height);
        this.frame.setResizable(false);
        this.frame.setTitle("2DTennis V2");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setLocationRelativeTo(null);
        this.frame.setLayout(new BorderLayout());
        this.frame.getContentPane().setBackground(BACKGROUND_COLOR);
        this.frame.setVisible(true);
    }

    private void initCanvas() {
        this.canvas = new Canvas();
        this.canvas.setPreferredSize(new Dimension(width, height));
        this.canvas.setMaximumSize(new Dimension(width, height));
        this.canvas.setMinimumSize(new Dimension(width, height));
        this.canvas.setBackground(BACKGROUND_COLOR);
        this.canvas.addKeyListener(keyManager);
        this.frame.add(canvas);
        this.frame.pack();
        this.canvas.requestFocusInWindow();
    }

    public synchronized void start() {
        if(running)
            return;
        running = true;
        gameThread = new Thread(this, "GameThread");
        gameThread.start();
    }

    public synchronized void stop() {
        if(!running)
            return;
        running = false;
        this.frame.setVisible(false);
        this.frame.dispose();
        MainMenu.currentGame = null;
        Entity.setCurrentGame(null);
    }

    public synchronized void endGame(boolean won) {
        stop();
        new GameSummary(this, won);
    }

    public int createTiles() {
        int tileGap = 10;
        int lastX = TILE_WIDTH/2;
        int lastY = TILE_HEIGTH*4;
        int row = 1;
        for(int i = 0; i < MainMenu.tileAmount; i++) {
            if(lastX + TILE_WIDTH/2 >= width) {
                row++;
                lastY += tileGap + TILE_HEIGTH;
                lastX = row % 2 == 0 ? TILE_WIDTH/2 + tileGap*2 : TILE_WIDTH/2;
            }
            entitiesToAdd.add(new Tile(lastX, lastY, TILE_WIDTH, TILE_HEIGTH));
            lastX += tileGap + TILE_WIDTH;
        }
        return lastY;
    }

    public void destroyTiles() {
        for(Entity e : entities) {
            if(e instanceof Tile) {
                entitiesToDelete.add(e);
            }
        }
    }

    @Override
    public void run() {
        double delta = 0;
        long now;
        long lastTime = System.nanoTime();
        long accumulator = 0;
        long updateAccumulator = 0;
        while(running) {
            now = System.nanoTime();
            delta = now - lastTime;
            accumulator += delta;
            updateAccumulator += delta;
            lastTime = now;
            while(accumulator >= nsPerUpdate) {
                physWorld.step(1f/MainMenu.fpsTarget, VEL_ITERATIONS, POS_ITERATIONS);
                while(updateAccumulator >= 1e9 / 100f) {
                    update();
                    updateAccumulator -= 1e9 / 100f;
                }
                render();
                accumulator -= nsPerUpdate;
                ticks++;
            }
            if(secTimer.tick()) {
                fpsString = "FPS: "+ticks;
                ticks = 0;
                secondsSinceStart++;
            }
        }
    }

    private void update() {
        if(score == MainMenu.tileAmount) {
            endGame(true);
            return;
        }
        if(!entitiesToDelete.isEmpty()) {
            for(Entity e : entitiesToDelete) {
                physWorld.destroyBody(e.getBody());
                entities.remove(e);
            }
            entitiesToDelete.clear();
        }
        if(!entitiesToAdd.isEmpty()) {
            for(Entity e : entitiesToAdd) {
                e.setBody(physWorld.createBody(e.getBd()));
                e.getBody().createFixture(e.getFd());
                entities.add(e);
            }
            entitiesToAdd.clear();
        }
        for(Entity e : entities) {
            e.update();
        }
        if(pickUpGen != null) 
            pickUpGen.update();
        if(bot != null)
            bot.update();
    }       

    private void render() {
        if(!running)
            return;
        bs = canvas.getBufferStrategy();
        if(bs == null) {
            canvas.createBufferStrategy(3);
            return;
        }
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // Clear the screen
        
        renderEntities(g);

        g.setColor(Color.WHITE);
        g.drawString(fpsString, 10, 20);
        g.drawString("Score: "+score, 10, 40);
        //g.drawOval(Utils.toPixel(bot.getPredictedBallPos().x), Utils.toPixel(bot.getPredictedBallPos().y), 5, 5); // Display ball prediction
        if(currentGameMode == GameMode.CPU) {
            if(cpuRacquet.isLeftPressed())
                g.drawImage(arrowLeft, 
                Game.width - arrowLeft.getWidth(null) - 50, 
                Game.height - arrowLeft.getHeight(null) - 50,
                50, 50, null);
            if(cpuRacquet.isRightPressed())
                g.drawImage(arrowRight, 
                Game.width - arrowLeft.getWidth(null)/2 - 50, 
                Game.height - arrowLeft.getHeight(null) - 50,
                50, 50, null);
        }
        
        bs.show();
        g.dispose();
    }

    private void renderEntities(Graphics2D g) {
        for(Entity e : entities) {
            e.render(g);
        }
    }

    private void createBoundaries(boolean visible) {
        Entity leftWall = null;
        Entity rightWall = null;
        Entity ceil = null;
        Entity ground = null;
        switch(currentGameMode) {
            case SINGLE :
                leftWall = new LineBoundary(0, 0, 0, height, visible);
                rightWall = new LineBoundary(width, 0, width, height, visible);
                ceil = new LineBoundary(0, 0, width, 0, visible);
                ground = new LineBoundary(0, height, width, height, visible);
                break;
            case CPU :
                leftWall = new LineBoundary(0, 0, 0, height, visible);
                rightWall = new LineBoundary(width, 0, width, height, visible);
                ceil = new LineBoundary(0, 0, width, 0, visible);
                ground = new LineBoundary(0, height, width, height, visible);
                break;
            case VERSUS :
                leftWall = new LineBoundary(0, 0, 0, height, visible);
                rightWall = new LineBoundary(width, 0, width, height, visible);
                ground = new LineBoundary(0, height, width, height, visible);
                break;
            // case TUTORIAL :
            //     leftWall = new LineBoundary(0, 0, 0, HEIGHT, visible);
            //     rightWall = new LineBoundary(WIDTH, 0, WIDTH, HEIGHT, visible);
            //     ceil = new LineBoundary(0, 0, WIDTH, 0, visible);
            //     ground = new LineBoundary(0, HEIGHT, WIDTH, HEIGHT, visible);
            //     break;
        }
        if(leftWall != null)
            entitiesToAdd.add(leftWall);
        if(rightWall != null)
            entitiesToAdd.add(rightWall);
        if(ceil != null)
            entitiesToAdd.add(ceil);
        if(ground != null)
            entitiesToAdd.add(ground);
    }

    public Bot getBot() {
        return bot;
    }

    public Ball getBall() {
        return ball;
    }

    public Racquet getCpuRacquet() {
        return cpuRacquet;
    }

    public int getPickupsPickedup() {
        return pickupsPickedup;
    }

    public void setPickupsPickedup(int pickupsPickedup) {
        this.pickupsPickedup = pickupsPickedup;
    }

    public int getSecondsSinceStart() {
        return secondsSinceStart;
    }

    public PickupGen getPickUpGen() {
        return pickUpGen;
    }

    public Pickup getCurrentPickup() {
        return currentPickup;
    }

    public void setCurrentPickup(Pickup currentPickup) {
        this.currentPickup = currentPickup;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public World getPhysWorld() {
        return physWorld;
    }

    public Thread getSimThread() {
        return gameThread;
    }

    public void setSimThread(Thread simThread) {
        this.gameThread = simThread;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public BufferStrategy getBs() {
        return bs;
    }

    public void setBs(BufferStrategy bs) {
        this.bs = bs;
    }

    public Timer getSecTimer() {
        return secTimer;
    }

    public int getTicks() {
        return ticks;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    public KeyManager getKeyManager() {
        return keyManager;
    }

    public Set<Entity> getEntities() {
        return entities;
    }

    public Set<Entity> getEntitiesToAdd() {
        return entitiesToAdd;
    }

    public Set<Entity> getEntitiesToDelete() {
        return entitiesToDelete;
    }
    
}

