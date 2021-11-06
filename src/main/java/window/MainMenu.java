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
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import game.Game;
import game.GameMode;

public final class MainMenu extends JFrame implements ActionListener{
    
    public static Game currentGame;

    public static final int WIDTH = 500;
    public static final int HEIGHT = 700;
    public static final Color BACKGROUND_COLOR = new Color(50, 111, 168);
    public static final Font FONT = new Font("SansSerif", Font.PLAIN, 50);
    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 60);

    public static int fpsTarget = Game.FPS_DEFAULT;
    public static int tileAmount = Game.DEFAULT_TILES;

    private String frameTitle = "2DTennis V2 Main Menu";
    private GridBagConstraints gbc;
    private JPanel buttonsPanel, titlePanel, creditPanel, settingsPanel, tutorialPanel;
    private JButton start, settings, tutorial, exit;
    private JLabel title, author;
    private JTextArea tutorialText;
    private Dimension buttonDimensions = new Dimension(300, 70);
    private KeyManager keyManager;
    private boolean settingsVisible;

    public MainMenu() {
        this.keyManager = new KeyManager(this);
        initFrame();
        createMainMenu();
    }

    public void startGame() {
        if(Game.currentGameMode == null)
            Game.currentGameMode = game.GameMode.SINGLE;
        MainMenu.currentGame = new Game();
        MainMenu.currentGame.start();
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

        titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setBackground(BACKGROUND_COLOR);
        buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        creditPanel = new JPanel(new BorderLayout());
        creditPanel.setBackground(BACKGROUND_COLOR);

        title = new JLabel("2DTENNIS");
        title.setFont(TITLE_FONT);
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

    private void setButtonSettings(JButton button, String actionCommand) {
        button.setBackground(Color.BLACK);
        button.setFocusPainted(false);
        button.setFont(FONT);
        button.setPreferredSize(buttonDimensions);
        button.setActionCommand(actionCommand);
        button.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()) {
            case "start" :
                this.setVisible(false);
                this.dispose();
                startGame();
                break;
            case "settings" :
                titlePanel.setVisible(false);
                buttonsPanel.setVisible(false);
                showSettings();
                break;
            case "exit" :
                System.exit(0);
        }
    }

    private void showSettings() {
        settingsVisible = true;
        int maxWidth = (int) buttonDimensions.getWidth();
        settingsPanel = new JPanel();
        settingsPanel.setBackground(BACKGROUND_COLOR);
        settingsPanel.setLayout(new GridBagLayout());
        JPanel firstPanel = new JPanel();
        firstPanel.setLayout(new GridBagLayout());
        firstPanel.setBackground(BACKGROUND_COLOR);
        firstPanel.setVisible(true);
        JPanel nextPanel = new JPanel();
        nextPanel.setLayout(new GridBagLayout());
        nextPanel.setBackground(BACKGROUND_COLOR);
        nextPanel.setVisible(false);
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new GridBagLayout());
        btnPanel.setBackground(BACKGROUND_COLOR);
        settingsPanel.add(firstPanel);
        settingsPanel.add(nextPanel);
        gbc.gridx = 0;
        gbc.gridy = 1;
        settingsPanel.add(btnPanel, gbc);
        Font font = new Font("SansSerif", Font.PLAIN, 30);
        JLabel fps = new JLabel("Target FPS");
        fps.setForeground(Color.ORANGE);
        fps.setFont(font);
        JLabel tilesLabel = new JLabel("Tiles amount");
        tilesLabel.setForeground(Color.ORANGE);
        tilesLabel.setFont(font);
        JTextField selectedFps = new JTextField(Integer.toString(fpsTarget), 3);
        selectedFps.setHorizontalAlignment(SwingConstants.CENTER);
        selectedFps.setFont(font);
        JTextField tileField = new JTextField(Integer.toString(tileAmount), 3);
        tileField.setHorizontalAlignment(SwingConstants.CENTER);
        tileField.setFont(font);
        JSlider fpsSlider = new JSlider(Game.FPS_MIN, Game.FPS_MAX, fpsTarget);
        fpsSlider.setMajorTickSpacing(10);
        fpsSlider.setMinorTickSpacing(5);
        fpsSlider.setPaintTicks(true);
        fpsSlider.setFocusable(false);
        fpsSlider.setPreferredSize(new Dimension(maxWidth, 50));
        fpsSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                var source = (JSlider) e.getSource();
                fpsTarget = source.getValue();
                selectedFps.setText(Integer.toString(source.getValue()));
            }
        });
        JSlider tileSlider = new JSlider(1, 450);
        tileSlider.setMajorTickSpacing(10);
        tileSlider.setMinorTickSpacing(5);
        tileSlider.setPaintTicks(true);
        tileSlider.setFocusable(false);
        tileSlider.setPreferredSize(new Dimension(maxWidth, 50));
        tileSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                var source = (JSlider) e.getSource();
                tileAmount = source.getValue();
                tileField.setText(Integer.toString(source.getValue()));
            }

        });
        JButton nextB = new JButton("Next");
        nextB.setBackground(Color.BLACK);
        nextB.setFocusPainted(false);
        nextB.setFont(FONT);
        nextB.setPreferredSize(buttonDimensions);
        nextB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                firstPanel.setVisible(false);
                nextPanel.setVisible(true);
                nextB.setVisible(false);
            }
        
        });
        JLabel modeLabel = new JLabel("Game Mode");
        modeLabel.setFont(font);
        modeLabel.setForeground(Color.ORANGE);
        JComboBox<GameMode> modeBox = new JComboBox<>(GameMode.values());
        if(Game.currentGameMode != null)
            modeBox.setSelectedItem(Game.currentGameMode);
        modeBox.setPreferredSize(new Dimension(maxWidth, 50));
        modeBox.setFocusable(false);
        modeBox.setFont(font);
        ((JLabel)modeBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        modeBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                var source = (JComboBox<GameMode>) e.getSource();
                Game.currentGameMode = (GameMode) source.getSelectedItem();
            }

        });

        gbc.insets = new Insets(0, 0, 10, 0);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        firstPanel.add(fps, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        firstPanel.add(selectedFps, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        firstPanel.add(fpsSlider, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        firstPanel.add(tilesLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        firstPanel.add(tileField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        firstPanel.add(tileSlider, gbc);

        gbc.insets = new Insets(0, 0, 20, 0);

        gbc.gridx = 0;
        gbc.gridy = 0;
        nextPanel.add(modeLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        nextPanel.add(modeBox, gbc);

        gbc.insets = new Insets(25, 0, 10, 0);

        JButton back = new JButton("Back");
        back.setBackground(Color.BLACK);
        back.setFocusPainted(false);
        back.setFont(FONT);
        back.setPreferredSize(buttonDimensions);
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int fps = Game.FPS_DEFAULT;
                try {
                    fps = Integer.parseInt(selectedFps.getText());
                } catch (NumberFormatException ex) {
                    
                }
                fpsTarget = Math.min(Game.FPS_MAX, Math.max(fps, Game.FPS_MIN));
                int tiles = Game.DEFAULT_TILES;
                try {
                    tiles = Integer.parseInt(tileField.getText());
                } catch (NumberFormatException ex) {
                   
                }
                tileAmount = tiles <= 0 ? Game.DEFAULT_TILES : tiles;
                if(!nextPanel.isVisible()) {
                    hideSettings();
                } else {
                    nextPanel.setVisible(false);
                    firstPanel.setVisible(true);
                    nextB.setVisible(true);
                }
                settingsVisible = false;
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        btnPanel.add(back, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        btnPanel.add(nextB, gbc);
        this.add(settingsPanel, BorderLayout.CENTER);
        this.requestFocusInWindow();
    }

    public void hideSettings() {
        settingsPanel.setVisible(false);
        titlePanel.setVisible(true);
        buttonsPanel.setVisible(true);
    }

    public boolean isSettingsVisible() {
        return settingsVisible;
    }

}
