package game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import entity.Entity;
import entity.LineBoundary;
import timer.Timer;
import window.KeyManager;
import window.MainMenu;

public final class Game implements Runnable {

    public static final int WIDTH = 1600;
    public static final int HEIGHT = 800;
    public static final int FPS = 165;
    public static final double NS_PER_TICK = 1e9 / FPS;
    public static final int VEL_ITERATIONS = 6;
    public static final int POS_ITERATIONS = 3;
    public static final Vec2 GRAVITY = new Vec2(0f, 9.81f);
    
    public static World physWorld;
    
    private Thread simThread;
    private JFrame frame;
    private Canvas canvas;
    private Color canvasColor;
    private MainMenu window;
    private KeyManager keyManager;
    private boolean running;
    private BufferStrategy bs;
    private String fps = "FPS: ";
    private Set<Entity> entities;
    private Timer secTimer;
    private int ticks;
    
    public Game(MainMenu window) {
        this.window = window;
        init();
    }

    private void init() {
        this.secTimer = new Timer((int)1e3);
        physWorld = new World(GRAVITY);
        this.entities = new HashSet<>();
        createBoundaries();
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
        for(Entity e : entities) {
            e.update();
        }
    }

    private void render() {
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

    public synchronized void start() {
        if(running)
            return;
        running = true;
        simThread = new Thread(this, "SimThread");
        simThread.start();
        System.out.println("Started simulation");
    }

    public synchronized void stop() {
        if(!running)
            return;
        running = false;
        System.out.println("Stopped simulation"); 
    }

    private void createBoundaries() {
        Entity ground = new LineBoundary(0, WIDTH, WIDTH, HEIGHT);
        Entity leftWall = new LineBoundary(0, 0, 0, HEIGHT);
        Entity rightWall = new LineBoundary(WIDTH, 0, WIDTH, HEIGHT);
        Entity ceil = new LineBoundary(0, 0, WIDTH, 0);
        this.entities.add(ground);
        this.entities.add(leftWall);
        this.entities.add(rightWall);
        this.entities.add(ceil);
    }

    private void createCanvas() {
        this.canvasColor = new Color(0, 0, 0);
        this.canvas = new Canvas();
        this.canvas.setMinimumSize(new Dimension(WIDTH, HEIGHT));
        this.canvas.setMaximumSize(new Dimension(WIDTH, HEIGHT));
        this.canvas.setSize((new Dimension(WIDTH, HEIGHT)));
        this.canvas.setBackground(canvasColor);
        this.canvas.addKeyListener(keyManager);
        
    }

    public Thread getSimThread() {
        return simThread;
    }

    public void setSimThread(Thread simThread) {
        this.simThread = simThread;
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

    
}

