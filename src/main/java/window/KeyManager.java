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

    public KeyManager(MainMenu menu) {
        this.menu = menu;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys.put(e.getKeyCode(), true);
        switch(e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE :
                if(this.currentGame != null) {
                    this.currentGame.stop();
                    new MainMenu();
                } else if(menu != null) {
                    System.exit(0);
                }
                break;
            case KeyEvent.VK_ENTER :
                if(menu != null) {
                    MainMenu.fpsTarget = MainMenu.fpsTarget <= 0f ? 60f : MainMenu.fpsTarget;
                    menu.setVisible(false);
                    menu.dispose();
                    menu.startGame();
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
