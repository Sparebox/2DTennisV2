package physics.jbox2d;

import javax.swing.SwingUtilities;

import window.MainMenu;

public class Launcher {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainMenu();
            }
        });
        
    }
}
