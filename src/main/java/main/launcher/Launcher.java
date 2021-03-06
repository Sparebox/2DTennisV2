package main.launcher;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import window.MainMenu;
/**
 * Entry point for the program
*/
public class Launcher {
    public static void main(String[] args) {
        //System.setProperty("sun.java2d.opengl", "True"); // Disabled for now because this causes problems on Windows 11 
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainMenu();
            }
        });
        
    }
}
