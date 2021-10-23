package window;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public final class KeyManager extends KeyAdapter {

    HashMap<Integer, Boolean> keys = new HashMap<>();

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_W :
                MouseManager.spawnType = SpawnType.WALL;
                break;
            case KeyEvent.VK_M :
                MouseManager.spawnType = SpawnType.MOVER;
                break;
        }
        keys.put(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys.put(e.getKeyCode(), false);
    }
    
}
