package window;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import game.Game;

public final class KeyManager extends KeyAdapter {

    private HashMap<Integer, Boolean> keys = new HashMap<>();
    private Game currentGame;
    private MainMenu menu;

    public KeyManager(Game currentGame) {
        this.currentGame = currentGame;
        this.keys.put(KeyEvent.VK_LEFT, false);
        this.keys.put(KeyEvent.VK_RIGHT, false);
        this.keys.put(KeyEvent.VK_UP, false);
        this.keys.put(KeyEvent.VK_SHIFT, false);
        this.keys.put(KeyEvent.VK_COMMA, false);
        this.keys.put(KeyEvent.VK_PERIOD, false);
    }

    public KeyManager(MainMenu menu) {
        this.menu = menu;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys.put(e.getKeyCode(), true);
        switch(e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE :
                if(currentGame != null) {
                    currentGame.stop();
                    MainMenu.currentGame = null;
                    new MainMenu();
                } else if(menu != null) {
                    System.exit(0);
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys.put(e.getKeyCode(), false);
    }

    public HashMap<Integer, Boolean> getKeys() {
        return keys;
    }
    
}
