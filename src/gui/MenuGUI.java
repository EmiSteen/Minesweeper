package gui;

import javax.swing.*;
import java.awt.*;

public class MenuGUI {

    public MenuGUI() {
        JFrame frame = new JFrame("Menu");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(250, 400);
        frame.setResizable(false);
        frame.add(selectionPanel(frame), BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    private JPanel selectionPanel(JFrame frame) {
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new GridLayout(5, 1));
        createMenuButton(frame, selectionPanel, "Easy", 8, 8, 10);
        createMenuButton(frame, selectionPanel, "Medium", 16, 16, 40);
        createMenuButton(frame, selectionPanel, "Hard", 16, 30, 99);
        JButton customButton = new JButton();
        customButton.setText("Custom Board");
        customButton.addActionListener(actionEvent -> {
            frame.remove(selectionPanel);
            frame.add(customPanel(frame));
            frame.repaint();
            frame.setVisible(true);
        });
        customButton.setFocusable(false);
        customButton.setPreferredSize(new Dimension(250, 55));
        selectionPanel.add(customButton);
        JButton quitButton = new JButton();
        quitButton.setText("Quit");
        quitButton.addActionListener(actionEvent -> System.exit(0));
        quitButton.setFocusable(false);
        quitButton.setPreferredSize(new Dimension(250, 55));
        selectionPanel.add(quitButton);
        return selectionPanel;
    }

    private JPanel customPanel(JFrame frame) {
        JPanel customPanel = new JPanel();
        customPanel.setLayout(new GridLayout(5, 1));
        JSpinner rowSP = new JSpinner();
        JSpinner colSP = new JSpinner();
        JSpinner mineSP = new JSpinner();
        SpinnerModel rowSPModel = new SpinnerNumberModel(10, 4, 16, 1);
        SpinnerModel colSPModel = new SpinnerNumberModel(10, 8, 36, 1);
        SpinnerModel mineSPModel = new SpinnerNumberModel(15, 1, 70, 1);
        rowSP.setModel(rowSPModel);
        colSP.setModel(colSPModel);
        mineSP.setModel(mineSPModel);
        customPanel.add(rowSP);
        customPanel.add(colSP);
        customPanel.add(mineSP);
        JButton startButton = new JButton();
        startButton.setText("Start");
        startButton.addActionListener(actionEvent -> {
            frame.dispose();
            int rows = (int) rowSP.getValue();
            int cols = (int) colSP.getValue();
            int mines = (int) (((int) mineSP.getValue()) / 100.0 * (rows * cols));
            new BoardGUI(rows, cols, mines);
        });
        startButton.setFocusable(false);
        customPanel.add(startButton);
        JButton backButton = new JButton();
        backButton.setText("Back");
        backButton.addActionListener(actionEvent -> {
            frame.remove(customPanel);
            frame.add(selectionPanel(frame));
            frame.setVisible(true);
        });
        backButton.setFocusable(false);
        customPanel.add(backButton);
        return customPanel;
    }

    private void createMenuButton(JFrame frame, JPanel selectionPanel, String difficulty, int rows, int cols, int mines) {
        JButton menuButton = new JButton();
        menuButton.setText(difficulty + " - " + cols + " x " + rows + " - " + mines + " mines");
        menuButton.addActionListener(actionEvent -> {
            frame.dispose();
            new BoardGUI(rows, cols, mines);
        });
        menuButton.setFocusable(false);
        menuButton.setPreferredSize(new Dimension(250, 55));
        selectionPanel.add(menuButton);
    }

}
