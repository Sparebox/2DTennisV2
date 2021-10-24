package window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import game.Game;

public final class MainMenu extends JFrame{
    
    public static Game currentGame;

    public static final int WIDTH = 500;
    public static final int HEIGHT = 700;
    public static final Color BACKGROUND_COLOR = new Color(50, 111, 168);

    private String frameTitle = "2DTennisV2 Main Menu";
    private MouseManager mouseManager;
    private GridBagConstraints gbc;
    private Font titleFont = new Font("SansSerif", Font.BOLD, 60);
    private Font font = new Font("SansSerif", Font.PLAIN, 50);
    private JPanel buttonsPanel, titlePanel, creditPanel, settingsPanel, tutorialPanel;
    private JButton start, settings, tutorial, back, exit;
    private JLabel title;
    private JLabel author;
    private JTextArea tutorialText;
    private Dimension buttonDimensions = new Dimension(300, 70);

    public MainMenu() {
        initFrame();
        createMainMenu();
    }

    private void initFrame() {
        this.setSize(WIDTH, HEIGHT);
        this.setResizable(false);
        this.setTitle(frameTitle);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new GridBagLayout());
        this.getContentPane().setBackground(BACKGROUND_COLOR);
        this.setVisible(true);
        this.mouseManager = new MouseManager();
        this.gbc = new GridBagConstraints();
    }

    private void createMainMenu() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setBackground(BACKGROUND_COLOR);
        buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        creditPanel = new JPanel(new BorderLayout());
        creditPanel.setBackground(BACKGROUND_COLOR);

        title = new JLabel("2DTENNIS");
        title.setFont(titleFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        titlePanel.add(title, gbc);

        var welcome = new JLabel("Welcome To");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 30));
        gbc.gridx = 0;
        gbc.gridy = 0;
        titlePanel.add(welcome, gbc);

        var v2 = new JLabel("V2");
        v2.setFont(new Font("SansSerif", Font.BOLD, 30));
        v2.setForeground(Color.ORANGE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        titlePanel.add(v2, gbc);

        start = new JButton("Start");
        setButtonSettings(start);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 0);
        buttonsPanel.add(start, gbc);

        settings = new JButton("Settings");
        setButtonSettings(settings);
        gbc.gridx = 0;
        gbc.gridy = 2;
        buttonsPanel.add(settings, gbc);

        tutorial = new JButton("Tutorial");
        setButtonSettings(tutorial);
        gbc.gridx = 0;
        gbc.gridy = 3;
        buttonsPanel.add(tutorial, gbc);

        exit = new JButton("Exit");
        setButtonSettings(exit);
        gbc.gridx = 0;
        gbc.gridy = 4;
        buttonsPanel.add(exit, gbc);

        author = new JLabel("Oskari Ojamaa 2021");
        author.setFont(new Font("SansSerif", Font.PLAIN, 10));
        creditPanel.add(author, BorderLayout.LINE_START);

        gbc.insets = new Insets(0, 0, 50, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.add(titlePanel, gbc);
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.add(buttonsPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        this.add(creditPanel, gbc);

    }

    private void startGame() {
        if(currentGame != null)
            return;
        currentGame = new Game(this);
        currentGame.start();
    }

    private void setButtonSettings(JButton button) {
        button.setBackground(Color.BLACK);
        button.setFocusPainted(false);
        button.setFont(font);
        button.setPreferredSize(buttonDimensions);
    }

}
