package window;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.HashMap;


public final class MouseManager extends MouseAdapter {

    public static SpawnType spawnType = SpawnType.MOVER;

    private HashMap<Integer, Boolean> keys;

    public MouseManager() {
        keys = new HashMap<>();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        keys.put(e.getButton(), true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        keys.put(e.getButton(), false);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        
    }

}
