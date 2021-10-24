package game;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.border.EtchedBorder;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import entity.Ball;
import entity.Entity;
import entity.LineBoundary;
import entity.Racquet;
import entity.Tile;
import timer.Timer;
import window.KeyManager;
import window.MainMenu;

public final class Game implements Runnable {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 1000;
    public static final Color BACKGROUND_COLOR = Color.BLACK;
    public static final int FPS = 165;
    public static final double NS_PER_TICK = 1e9 / FPS;
    public static final int VEL_ITERATIONS = 6;
    public static final int POS_ITERATIONS = 3;
    public static final Vec2 GRAVITY = new Vec2(0, 0);
    
    public static World physWorld;
    
    private Thread gameThread;
    private JFrame frame;
    private Canvas canvas;
    private MainMenu window;
    private KeyManager keyManager;
    private boolean running;
    private BufferStrategy bs;
    private String fps = "FPS: ";
    private Set<Entity> entities;
    private Set<Entity> entitiesToDelete;
    private Timer secTimer;
    private int ticks;
    private int tileAmount = 50;
    private int tileWidth = 40;
    private int tileHeight = 20;
    private int ballRadius = 20;
    
    public Game() {
        init();
    }


    private void init() {
        this.keyManager = new KeyManager(this);
        initFrame();
        initCanvas();
        this.secTimer = new Timer((int)1e3);
        physWorld = new World(GRAVITY);
        this.entities = new HashSet<>();
        this.entitiesToDelete = new HashSet<>();
        createBoundaries(false);
        this.entities.add(new Racquet(WIDTH/2, 200, 20));
        int lastTileY = createTiles();
        Entity ball = new Ball(WIDTH/2, lastTileY + ballRadius/2, ballRadius);
        ball.getBody().setLinearVelocity(new Vec2(5, 5));
        this.entities.add(ball);
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
        System.out.println("Started game");
    }

    public synchronized void stop() {
        if(!running)
            return;
        running = false;
        this.frame.dispose();
        System.out.println("Stopped game"); 
    }

    @Override
    public void run() {
        double delta = 0;
        long now;
        long lastTime = System.nanoTime();
        long accumulator = 0;
        while(running) {
            now = System.nanoTime();
            delta = now - lastTime;
            accumulator += delta;
            lastTime = now;
            
            while(accumulator >= NS_PER_TICK) {
                physWorld.step(1f/165f, VEL_ITERATIONS, POS_ITERATIONS);
                update();
                render();
                accumulator -= NS_PER_TICK;
                ticks++;
            }
            if(secTimer.tick()) {
                fps = "FPS: "+ticks;
                ticks = 0;
            }
        }
    }

    private void update() {
        if(!entitiesToDelete.isEmpty()) {
            entities.removeAll(entitiesToDelete);
            entitiesToDelete.clear();
        }
        for(Entity e : entities) {
            e.update();
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
        g.drawString(fps, 20, 20);

        bs.show();
        g.dispose();
    }

    private void renderEntities(Graphics2D g) {
        for(Entity e : entities) {
            e.render(g);
        }
    }

    private void createBoundaries(boolean visible) {
        Entity leftWall = new LineBoundary(0, 0, 0, HEIGHT, visible);
        Entity rightWall = new LineBoundary(WIDTH, 0, WIDTH, HEIGHT, visible);
        Entity ceil = new LineBoundary(0, 0, WIDTH, 0, visible);
        this.entities.add(leftWall);
        this.entities.add(rightWall);
        this.entities.add(ceil);
    }

    private int createTiles() {
        int tileGap = 10;
        int lastX = 0;
        int lastY = tileHeight/2;
        for(int i = 0; i < tileAmount; i++) {
            if(lastX >= WIDTH) {
                lastY += tileGap + tileHeight;
                lastX = 0; 
            }
            this.entities.add(new Tile(lastX, lastY, tileWidth, tileHeight));
            lastX += tileGap + tileWidth;
        }
        return lastY;
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

    public MainMenu getWindow() {
        return window;
    }

    public void setWindow(MainMenu window) {
        this.window = window;
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

    public Set<Entity> getEntitiesToDelete() {
        return entitiesToDelete;
    }
    
}

