package window;

import simulation.Simulation;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;

public final class Window extends JFrame{
    
    public static Simulation currentSim;

    public static final int W_WIDTH = 1600;
    public static final int W_HEIGHT = 800;

    private String title = "JBOX2D";
    public Canvas canvas;
    private Color canvasColor;
    private MouseManager mouseManager;
    private KeyManager keyManager;

    public Window() {
        initFrame();
        createCanvas();
        startSimulation();
    }

    private void initFrame() {
        this.setSize(W_WIDTH, W_HEIGHT);
        this.setResizable(false);
        this.setTitle(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.setVisible(true);
        this.mouseManager = new MouseManager();
        this.keyManager = new KeyManager();
    }

    private void createCanvas() {
        canvasColor = new Color(0, 0, 0);
        canvas = new Canvas();
        canvas.setMinimumSize(new Dimension(W_WIDTH, W_HEIGHT));
        canvas.setMaximumSize(new Dimension(W_WIDTH, W_HEIGHT));
        canvas.setSize((new Dimension(W_WIDTH, W_HEIGHT)));
        canvas.setBackground(canvasColor);
        canvas.addMouseListener(mouseManager);
        canvas.addMouseWheelListener(mouseManager);
        canvas.addMouseMotionListener(mouseManager);
        canvas.addKeyListener(keyManager);
        this.add(canvas, BorderLayout.CENTER);
        this.pack();
    }

    private void startSimulation() {
        if(currentSim != null)
            return;
        currentSim = new Simulation(this);
        currentSim.start();
        canvas.requestFocusInWindow();
    }

    public Canvas getCanvas() {
        return canvas;
    }

}
