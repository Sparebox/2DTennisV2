package simulation;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.HashSet;
import java.util.Set;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import entity.Ball;
import entity.Box;
import entity.Entity;
import entity.LineBoundary;
import entity.BoxBoundary;
import timer.Timer;
import utils.Utils;
import window.Window;

public final class Simulation implements Runnable {

    public static final int FPS = 165;
    public static final double NS_PER_TICK = 1e9 / FPS;
    public static final int VEL_ITERATIONS = 6;
    public static final int POS_ITERATIONS = 3;
    public static final Vec2 GRAVITY = new Vec2(0f, 9.8f);
    
    public static World world;
    
    private Thread simThread;
    private Canvas canvas;
    private Window window;
    private boolean running;
    private BufferStrategy bs;
    private String fps = "FPS: ";
    private Set<Entity> entities;
    private Timer secTimer;
    private int ticks;
    
    public Simulation(Window window) {
        this.window = window;
        init();
    }

    private void init() {
        this.canvas = window.getCanvas();
        this.secTimer = new Timer((int)1e3);
        world = new World(GRAVITY);
        this.entities = new HashSet<>();
        createBoundaries();
        Entity box = new Box(200, 100, 100, 100);
        Entity ball = new Ball(100, 25, 50);
        ball.getBody().applyForceToCenter(new Vec2(60f,0));
        box.getBody().setAngularVelocity(-1);
        Entity platfrom = new BoxBoundary(100, 500, 100, 100);
        Entity platfrom2 = new BoxBoundary(400, 700, 100, 100);
        box.getBody().applyForceToCenter(new Vec2(1000f, 0));
        this.entities.add(box);
        this.entities.add(ball);
        this.entities.add(platfrom);
        this.entities.add(platfrom2);
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
                world.step(1f/165f, VEL_ITERATIONS, POS_ITERATIONS);
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
        Entity ground = new LineBoundary(0, Window.W_HEIGHT, Window.W_WIDTH, Window.W_HEIGHT);
        Entity leftWall = new LineBoundary(0, 0, 0, Window.W_HEIGHT);
        Entity rightWall = new LineBoundary(Window.W_WIDTH, 0, Window.W_WIDTH, Window.W_HEIGHT);
        Entity ceil = new LineBoundary(0, 0, Window.W_WIDTH, 0);
        this.entities.add(ground);
        this.entities.add(leftWall);
        this.entities.add(rightWall);
        this.entities.add(ceil);
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

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
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

