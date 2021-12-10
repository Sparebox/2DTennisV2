package window;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import game.Game;
/**
 * Manages the key presses in-game and in the main menu
 */
public final class KeyManager extends KeyAdapter {

    private HashMap<Integer, Boolean> keys = new HashMap<>();
    private Game currentGame;
    private MainMenu menu;
    private GameSummary summary;

    public KeyManager(Game currentGame) {
        this.currentGame = currentGame;
        this.keys.put(KeyEvent.VK_ENTER, false);
        this.keys.put(KeyEvent.VK_SHIFT, false);
        this.keys.put(KeyEvent.VK_LEFT, false);
        this.keys.put(KeyEvent.VK_RIGHT, false);
        this.keys.put(KeyEvent.VK_SPACE, false);
        this.keys.put(KeyEvent.VK_COMMA, false);
        this.keys.put(KeyEvent.VK_PERIOD, false);
        this.keys.put(KeyEvent.VK_A, false);
        this.keys.put(KeyEvent.VK_D, false);
        this.keys.put(KeyEvent.VK_Q, false);
        this.keys.put(KeyEvent.VK_E, false);
    }
    /**
     * Constructor meant for use in the MainMenu class
     * @param menu the instance of the MainMenu class
     */
    public KeyManager(MainMenu menu) {
        this.menu = menu;
    }
    /**
     * Constructor meant for use in the GameSummary class
     * @param summary the instance of the GameSummary class
     */
    public KeyManager(GameSummary summary) {
        this.summary = summary;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys.put(e.getKeyCode(), true);
        switch(e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE :
                if(currentGame != null) {
                    currentGame.stop();
                    new MainMenu();
                }
                else if(menu != null) {
                    if(menu.isSettingsVisible())
                        menu.hideSettings();
                    else
                        System.exit(0);
                }
                else if(summary != null) {
                    System.exit(0);
                }
                break;
            case KeyEvent.VK_ENTER :
                if(menu != null) {
                    menu.setVisible(false);
                    menu.dispose();
                    menu.startGame();
                }
                if(summary != null) {
                    summary.dispose();
                    new MainMenu();
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys.put(e.getKeyCode(), false);
    }

    // Getters and setters

    public HashMap<Integer, Boolean> getKeys() {
        return keys;
    }
    
}
