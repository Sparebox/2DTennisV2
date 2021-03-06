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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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

import entity.Racquet;
import game.Game;
import game.GameMode;
import game.Level;
/**
 * The MainMenu class is the base class where games are started and where ended games lead back to
 */
public final class MainMenu extends JFrame implements ActionListener{
    
    public static Game currentGame;

    public static final int WIDTH = 500;
    public static final int HEIGHT = 700;
    public static final Color BACKGROUND_COLOR = new Color(50, 111, 168);
    public static final Font FONT = new Font("SansSerif", Font.PLAIN, 50);
    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 60);
    
    public static int fpsTarget = Game.FPS_DEFAULT;

    private String frameTitle = "2DTennis V2 Main Menu";
    private GridBagConstraints gbc;
    private JPanel buttonsPanel, titlePanel, creditPanel, settingsPanel, tutorialPanel;
    private JButton start, settings, tutorial, exit;
    private JLabel title, author;
    private JTextArea tutorialText;
    private Dimension buttonDimensions = new Dimension(300, 70);
    private KeyManager keyManager;
    private boolean settingsVisible;
    private Settings settingsData;

    /**
     * Creates the main menu window and displays the main menu
     */
    public MainMenu() {
        this.keyManager = new KeyManager(this);
        this.settingsData = new Settings();
        initFrame();
        createMainMenu();
        loadSettings();
    }

    /**
     * Starts a new game in the selected game mode
     * <p>
     * If no game mode is selected, the "single" game mode is the default
     */
    public void startGame() {
        if(Game.currentGameMode == null)
            Game.currentGameMode = game.GameMode.SINGLE;
        MainMenu.currentGame = new Game();
        MainMenu.currentGame.start();
    }

    /**
     * Calculates the highest tile row possible for current game window dimensions
     * @return calculated max row
     */
    public static int calculateMaxRow() {
        int tileGap = 10;
        int lastX = Game.TILE_WIDTH/2;
        int lastY = Game.TILE_HEIGTH*4;
        int maxRow = 0;
        do {
            lastX += tileGap + Game.TILE_WIDTH;
            if(lastX + Game.TILE_WIDTH/2 >= Game.WIDTH) {
                maxRow++;
                lastY += tileGap + Game.TILE_HEIGTH;
                lastX = maxRow % 2 != 0 ? Game.TILE_WIDTH/2 + tileGap*2 : Game.TILE_WIDTH/2;
            }
        }
        while(lastY + Game.TILE_HEIGTH * 3 < Racquet.Y_COORD);
        return maxRow;
    }

    public void hideSettings() {
        settingsPanel.setVisible(false);
        titlePanel.setVisible(true);
        buttonsPanel.setVisible(true);
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
    /**
     * Creates the Swing GUI components for the main menu and requests focus in window
     */
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

        gbc.insets = new Insets(50, 0, 0, 0);

        var welcome = new JLabel("Welcome To");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 30));
        gbc.gridx = 0;
        gbc.gridy = 0;
        titlePanel.add(welcome, gbc);

        gbc.insets = new Insets(25, 0, 0, 0);

        var v2 = new JLabel("V2");
        v2.setFont(new Font("SansSerif", Font.BOLD, 30));
        v2.setForeground(Color.ORANGE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        titlePanel.add(v2, gbc);

        gbc.insets = new Insets(0, 0, 0, 0);

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

        author = new JLabel("Copyright 2021 Oskari Ojamaa");
        author.setFont(new Font("SansSerif", Font.PLAIN, 10));
        creditPanel.add(author, BorderLayout.CENTER);

        this.add(titlePanel, BorderLayout.PAGE_START);
        this.add(buttonsPanel, BorderLayout.CENTER);
        this.add(creditPanel, BorderLayout.PAGE_END);
        this.requestFocusInWindow();
    }
    /**
     * Attempts to load saved settings from disk
     * <p>
     * If this is unsuccessful it is ignored 
     */
    private void loadSettings() {
        try {
            FileInputStream fileIn = new FileInputStream("./settings.set");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            this.settingsData = (Settings) in.readObject();
            in.close();
            fileIn.close();
            Game.rowAmount = Integer.parseInt(this.settingsData.get(Settings.ROW));
            String level = this.settingsData.get(Settings.LEVEL);
            for(Level l : Level.values()) {
                if(l.toString().equals(level))
                    Game.currentLevel = l;
            }
            fpsTarget = Integer.parseInt(this.settingsData.get(Settings.FPS));
            String mode = this.settingsData.get(Settings.MODE);
            for(GameMode m : GameMode.values()) {
                if(m.toString().equals(mode))
                    Game.currentGameMode = m;
            }
        } catch (IOException | ClassNotFoundException | NumberFormatException e) {}
    }
    /**
     * Saves current settings to disk in a "settings.set" file
     */
    private void saveSettings() {
        this.settingsData.put(Settings.ROW, Integer.toString(Game.rowAmount));
        this.settingsData.put(Settings.FPS, Integer.toString(fpsTarget));
        this.settingsData.put(Settings.MODE, Game.currentGameMode.toString());
        this.settingsData.put(Settings.LEVEL, Game.currentLevel.toString());
        try {
            FileOutputStream fileOut = new FileOutputStream("./settings.set");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(settingsData);
            out.close();
            fileOut.close();
         } catch (IOException e) {}
    }
    /**
     * Initialize the specified button with general settings
     * @param button the button to initialize
     * @param actionCommand the command for the button
     */
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
            case "tutorial" :
                titlePanel.setVisible(false);
                buttonsPanel.setVisible(false);
                showTutorial();
                break;
            case "exit" :
                System.exit(0);
        }
    }
    /**
     * Opens the settings view
     */
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
        JLabel tilesLabel = new JLabel("Tile rows");
        tilesLabel.setForeground(Color.ORANGE);
        tilesLabel.setFont(font);
        JTextField selectedFps = new JTextField(Integer.toString(fpsTarget), 3);
        selectedFps.setHorizontalAlignment(SwingConstants.CENTER);
        selectedFps.setFont(font);
        JTextField rowField = new JTextField(Integer.toString(Game.rowAmount), 3);
        rowField.setHorizontalAlignment(SwingConstants.CENTER);
        rowField.setFont(font);
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
        JSlider tileSlider = new JSlider(1, calculateMaxRow());
        if(this.settingsData.get("tile_rows") != null)
            tileSlider.setValue(Integer.parseInt(this.settingsData.get("tile_rows")));
        tileSlider.setMajorTickSpacing(5);
        tileSlider.setMinorTickSpacing(1);
        tileSlider.setPaintTicks(true);
        tileSlider.setFocusable(false);
        tileSlider.setPreferredSize(new Dimension(maxWidth, 50));
        tileSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                var source = (JSlider) e.getSource();
                Game.rowAmount = source.getValue();
                rowField.setText(Integer.toString(source.getValue()));
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
        firstPanel.add(rowField, gbc);

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
                int rows = Game.DEFAULT_ROWS;
                try {
                    rows = Integer.parseInt(rowField.getText());
                } catch (NumberFormatException ex) {
                   
                }
                Game.rowAmount = rows <= 0 ? Game.DEFAULT_ROWS : rows;
                saveSettings();
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
    /**
     * Opens the tutorial view
     */
    private void showTutorial() {
        tutorialPanel = new JPanel(new GridBagLayout());
        tutorialPanel.setBackground(BACKGROUND_COLOR);

        var header = new JLabel("Tutorial");
        header.setFont(FONT);
        header.setForeground(Color.ORANGE);

        gbc.gridx = 0;
        gbc.gridy = 0;
        tutorialPanel.add(header, gbc);

        String text = "Left: A / <-\nRight: D / ->\nJump: space\nBoost: shift\nRoll left: Q /,\nRoll right: E /.";
        tutorialText = new JTextArea(text, 10, 50);
        tutorialText.setEditable(false);
        gbc.gridx = 0;
        gbc.gridy = 1;
        tutorialPanel.add(tutorialText, gbc);
        
        var back = new JButton("Back");
        back.setBackground(Color.BLACK);
        back.setFocusPainted(false);
        back.setFont(FONT);
        back.setPreferredSize(buttonDimensions);
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tutorialPanel.setVisible(false);
                titlePanel.setVisible(true);
                buttonsPanel.setVisible(true);
            }
        });
        gbc.insets = new Insets(20, 0, 0, 0);
        gbc.gridx = 0;
        gbc.gridy = 2;
        tutorialPanel.add(back, gbc);

        this.add(tutorialPanel, BorderLayout.CENTER);
    }

    // Getters and setters

    public boolean isSettingsVisible() {
        return settingsVisible;
    }

}
