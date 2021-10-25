package window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import game.Game;

public final class MainMenu extends JFrame implements ActionListener{
    
    public static Game currentGame;

    public static final int WIDTH = 500;
    public static final int HEIGHT = 700;
    public static final Color BACKGROUND_COLOR = new Color(50, 111, 168);

    public static float fpsTarget;

    private String frameTitle = "2DTennis V2 Main Menu";
    private GridBagConstraints gbc;
    private Font titleFont = new Font("SansSerif", Font.BOLD, 60);
    private Font font = new Font("SansSerif", Font.PLAIN, 50);
    private JPanel buttonsPanel, titlePanel, creditPanel, settingsPanel, tutorialPanel;
    private JButton start, settings, tutorial, exit, back;
    private JLabel title, author;
    private JTextArea tutorialText;
    private Dimension buttonDimensions = new Dimension(300, 70);
    private KeyManager keyManager;

    public MainMenu() {
        this.keyManager = new KeyManager(this);
        initFrame();
        createMainMenu();
    }

    private void initFrame() {
        this.setSize(WIDTH, HEIGHT);
        this.setResizable(false);
        this.setTitle(frameTitle);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(BACKGROUND_COLOR);
        this.addKeyListener(keyManager);
        this.setVisible(true);
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
        setButtonSettings(start, "start");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 0);
        buttonsPanel.add(start, gbc);

        settings = new JButton("Settings");
        setButtonSettings(settings, "settings");
        gbc.gridx = 0;
        gbc.gridy = 2;
        buttonsPanel.add(settings, gbc);

        tutorial = new JButton("Tutorial");
        setButtonSettings(tutorial, "tutorial");
        gbc.gridx = 0;
        gbc.gridy = 3;
        buttonsPanel.add(tutorial, gbc);

        exit = new JButton("Exit");
        setButtonSettings(exit, "exit");
        gbc.gridx = 0;
        gbc.gridy = 4;
        buttonsPanel.add(exit, gbc);
        gbc.insets = new Insets(0, 0, 0, 0);

        author = new JLabel("Oskari Ojamaa 2021");
        author.setFont(new Font("SansSerif", Font.PLAIN, 10));
        creditPanel.add(author, BorderLayout.CENTER);

        this.add(titlePanel, BorderLayout.PAGE_START);
        this.add(buttonsPanel, BorderLayout.CENTER);
        this.add(creditPanel, BorderLayout.PAGE_END);
        this.requestFocusInWindow();
    }

    private void startGame() {
        MainMenu.currentGame = new Game();
        MainMenu.currentGame.start();
    }

    private void setButtonSettings(JButton button, String actionCommand) {
        button.setBackground(Color.BLACK);
        button.setFocusPainted(false);
        button.setFont(font);
        button.setPreferredSize(buttonDimensions);
        button.setActionCommand(actionCommand);
        button.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()) {
            case "start" :
                fpsTarget = fpsTarget <= 0f ? 60f : fpsTarget;
                this.setVisible(false);
                this.dispose();
                startGame();
                break;
            case "settings" :
                titlePanel.setVisible(false);
                buttonsPanel.setVisible(false);
                showSettings();
                break;
            case "back" :
                settingsPanel.setVisible(false);
                titlePanel.setVisible(true);
                buttonsPanel.setVisible(true);
                break;
            case "exit" :
                System.exit(0);
        }
    }

    private void showSettings() {
        settingsPanel = new JPanel();
        settingsPanel.setBackground(BACKGROUND_COLOR);
        Font font = new Font("SansSerif", Font.PLAIN, 30);
        JLabel fps = new JLabel("FPS");
        fps.setFont(font);
        JSlider fpsSlider = new JSlider(Game.FPS_MIN, Game.FPS_MAX, 60);
        fpsSlider.setMajorTickSpacing(10);
        fpsSlider.setMinorTickSpacing(5);
        fpsSlider.setPaintTicks(true);
        fpsSlider.setFocusable(false);
        fpsSlider.setPreferredSize(new Dimension((int) buttonDimensions.getWidth(), 50));
        fpsSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                var source = (JSlider) e.getSource();
                fpsTarget = source.getValue();
                System.out.println(fpsTarget);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        settingsPanel.add(fps, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        settingsPanel.add(fpsSlider, gbc);

        back = new JButton("Back");
        setButtonSettings(back, "back");
        gbc.gridx = 0;
        gbc.gridy = 3;
        settingsPanel.add(back, gbc);
        this.add(settingsPanel, BorderLayout.CENTER);
    }

}
