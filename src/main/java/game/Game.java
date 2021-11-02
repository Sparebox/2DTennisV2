package game;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
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
import utils.Utils;
import window.GameSummary;
import window.KeyManager;
import window.MainMenu;

public final class Game implements Runnable {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 1000;
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

    public static GameMode currentGameMode = GameMode.VERSUS;
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
        this.pickUpGen = new PickupGen(this);
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
                lastTileY = createTiles();
                this.cpuRacquet = new Racquet(WIDTH/2, 200, 20);
                this.cpuRacquet.setCpuOwned(true);
                entitiesToAdd.add(cpuRacquet);
                entitiesToAdd.add(new Ball(WIDTH/2, lastTileY + 2 * BALL_RADIUS, BALL_RADIUS));
                this.bot = new Bot(this);
                this.arrowLeft = new ImageIcon(getClass().getResource("/leftkey.png")).getImage();
                this.arrowRight = new ImageIcon(getClass().getResource("/rightkey.png")).getImage();
                break;
            case SINGLE :
                lastTileY = createTiles();
                this.playerRacquet = new Racquet(WIDTH/2, 200, 20);
                entitiesToAdd.add(playerRacquet);
                entitiesToAdd.add(new Ball(WIDTH/2, lastTileY + 2 * BALL_RADIUS, BALL_RADIUS));
                break;
            case VERSUS :
                this.playerRacquet = new Racquet(WIDTH/2, 200, 20);
                this.cpuRacquet = new Racquet(WIDTH/2, 50, 200, 20);
                cpuRacquet.setCpuOwned(true);
                entitiesToAdd.add(playerRacquet);
                entitiesToAdd.add(cpuRacquet);
                entitiesToAdd.add(new Ball(WIDTH/2, HEIGHT/2, BALL_RADIUS));
                this.bot = new Bot(this);
                break;
            case TUTORIAL :
                this.arrowLeft = new ImageIcon(getClass().getResource("/leftkey.png")).getImage();
                this.arrowRight = new ImageIcon(getClass().getResource("/rightkey.png")).getImage();
                break;
        }
        
        
    }

    private void initFrame() {
        this.frame = new JFrame();
        this.frame.setSize(WIDTH, HEIGHT);
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
        this.canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.canvas.setMaximumSize(new Dimension(WIDTH, HEIGHT));
        this.canvas.setMinimumSize(new Dimension(WIDTH, HEIGHT));
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
        gameThread = new Thread(this, "SimThread");
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
        if(score == MainMenu.tileAmount &&
        (currentGameMode == GameMode.CPU ||
        currentGameMode == GameMode.SINGLE)) {
            endGame();
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

        if(pickUpGen != null && bot != null) {
            pickUpGen.update();
            bot.update();
        }
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
        g.drawString(fpsString, 20, 20);
        g.drawString("Score: "+score, 20, 40);
        //g.drawOval(Utils.toPixel(bot.getPredictedBallPos().x), Utils.toPixel(bot.getPredictedBallPos().y), 5, 5);
        if(currentGameMode == GameMode.TUTORIAL || currentGameMode == GameMode.CPU) {
            if(cpuRacquet.isLeftPressed())
                g.drawImage(arrowLeft, 
                Game.WIDTH - arrowLeft.getWidth(null) - 50, 
                Game.HEIGHT - arrowLeft.getHeight(null) - 50,
                50, 50, null);
            if(cpuRacquet.isRightPressed())
                g.drawImage(arrowRight, 
                Game.WIDTH - arrowLeft.getWidth(null)/2 - 50, 
                Game.HEIGHT - arrowLeft.getHeight(null) - 50,
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
                leftWall = new LineBoundary(0, 0, 0, HEIGHT, visible);
                rightWall = new LineBoundary(WIDTH, 0, WIDTH, HEIGHT, visible);
                ceil = new LineBoundary(0, 0, WIDTH, 0, visible);
                break;
            case CPU :
                leftWall = new LineBoundary(0, 0, 0, HEIGHT, visible);
                rightWall = new LineBoundary(WIDTH, 0, WIDTH, HEIGHT, visible);
                ceil = new LineBoundary(0, 0, WIDTH, 0, visible);
                break;
            case VERSUS:
                leftWall = new LineBoundary(0, 0, 0, HEIGHT, visible);
                rightWall = new LineBoundary(WIDTH, 0, WIDTH, HEIGHT, visible);
                break;
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

    private int createTiles() {
        int tileGap = 10;
        int lastX = TILE_WIDTH/2;
        int lastY = TILE_HEIGTH*4;
        int row = 1;
        for(int i = 0; i < MainMenu.tileAmount; i++) {
            if(lastX + TILE_WIDTH/2 >= WIDTH) {
                row++;
                lastY += tileGap + TILE_HEIGTH;
                lastX = row % 2 == 0 ? TILE_WIDTH/2 + tileGap*2 : TILE_WIDTH/2;
            }
            entitiesToAdd.add(new Tile(lastX, lastY, TILE_WIDTH, TILE_HEIGTH));
            lastX += tileGap + TILE_WIDTH;
        }
        return lastY;
    }

    public void endGame() {
        stop();
        new GameSummary(this);
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

