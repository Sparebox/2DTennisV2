package window;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JTextArea;

import game.Game;
import game.GameMode;
/**
 * This class is responsible for the summary at the end of each game
 */
public class GameSummary extends JFrame implements ActionListener {
    
    private static final String FRAME_TITLE = "Game Summary";

    private Game currentGame;
    private JPanel summaryPanel;
    private GridBagConstraints gbc;
    private Font font;
    private KeyManager keyManager;
    private boolean won;

    /**
     * Creates a new game summary window with the summary
     * @param currentGame the instance of the Game class
     * @param won true if the game was won and false if it was lost
     */
    public GameSummary(Game currentGame, boolean won) {
        this.currentGame = currentGame;
        this.won = won;
        this.keyManager = new KeyManager(this);
        initFrame();
        createSummary();
    }

    private void initFrame() {
        this.setSize(MainMenu.WIDTH, MainMenu.HEIGHT);
        this.setResizable(false);
        this.setTitle(FRAME_TITLE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(MainMenu.BACKGROUND_COLOR);
        this.addKeyListener(keyManager);
        this.gbc = new GridBagConstraints();
        this.font = new Font("SansSerif", Font.PLAIN, 20);
    }
    /**
     * Creates the needed Swing GUI elements
     */
    private void createSummary() {
        summaryPanel = new JPanel(new GridBagLayout());
        summaryPanel.setBackground(MainMenu.BACKGROUND_COLOR);
        gbc.insets = new Insets(0, 0, 50, 0);
        var summaryText = new JLabel("Game Summary");
        summaryText.setFont(MainMenu.TITLE_FONT);
        summaryText.setForeground(Color.ORANGE);

        gbc.gridx = 0;
        gbc.gridy = 0;
        summaryPanel.add(summaryText, gbc);
        
        String timeStr;
        if(currentGame.getSecondsSinceStart() < 60) 
            timeStr = Integer.toString(currentGame.getSecondsSinceStart())+" seconds";
        else
            timeStr = Integer.toString(currentGame.getSecondsSinceStart() / 60)+" minutes and " +
            Integer.toString(currentGame.getSecondsSinceStart() % 60)+" seconds";
        
        String scoreStr;
        if(Game.currentGameMode == GameMode.VERSUS)
            if(won)
                scoreStr = "You Won!";
            else
                scoreStr = "You Lost!";
        else if(won)
            scoreStr = "You Won! Score: "+Integer.toString(currentGame.getScore())+"/"+Game.tileAmount;
        else 
            scoreStr = "You Lost! Score: "+Integer.toString(currentGame.getScore())+"/"+Game.tileAmount;
        
        var textArea = new JTextArea("Time: " + timeStr + "\n" + scoreStr +
        "\nPickups picked up: "+Integer.toString(currentGame.getPickupsPickedup()) +
        "\nLevel: "+Game.currentLevel);
        textArea.setColumns(15);
        textArea.setFont(font);
        textArea.setEditable(false);

        gbc.insets = new Insets(20, 0, 0, 0);
        gbc.gridx = 0;
        gbc.gridy = 1;
        summaryPanel.add(textArea, gbc);

        var back = new JButton("Back");
        back.setActionCommand("back");
        back.addActionListener(this);
        back.setFont(font);
        back.setBackground(MainMenu.BACKGROUND_COLOR);
        back.setFocusPainted(false);

        gbc.gridx = 0;
        gbc.gridy = 2;
        summaryPanel.add(back, gbc);

        this.add(summaryPanel);
        this.setVisible(true);
        this.requestFocusInWindow();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()) {
            case "back" :
                this.setVisible(false);
                this.dispose();
                new MainMenu();
                break;
        }
        
    }

}
