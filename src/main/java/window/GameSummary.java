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

public class GameSummary extends JFrame implements ActionListener {
    
    private static final String FRAME_TITLE = "Game Summary";

    private Game currentGame;
    private JPanel summaryPanel;
    private GridBagConstraints gbc;
    private Font font;
    private KeyManager keyManager;

    public GameSummary(Game currentGame) {
        this.currentGame = currentGame;
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
        gbc.insets = new Insets(20, 0, 0, 0);

        String timeStr;
        if(currentGame.getSecondsSinceStart() < 60) {
            timeStr = Integer.toString(currentGame.getSecondsSinceStart())+" seconds";
        }
        else {
            timeStr = Integer.toString(currentGame.getSecondsSinceStart() / 60)+" minutes and " +
            Integer.toString(currentGame.getSecondsSinceStart() % 60)+" seconds";
        }
        var textArea = new JTextArea("Time: " + timeStr +
        "\nScore: "+Integer.toString(currentGame.getScore())+"/"+MainMenu.tileAmount + 
        "\nPickups picked up: "+Integer.toString(currentGame.getPickupsPickedup()));
        textArea.setColumns(15);
        textArea.setFont(font);
        textArea.setEditable(false);
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
