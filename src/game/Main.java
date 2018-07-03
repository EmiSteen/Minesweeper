package game;

import javax.swing.*;
import gui.MenuGUI;

public class Main {

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.exit(0);
        }
        new MenuGUI();
    }

}
