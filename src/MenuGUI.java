import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuGUI {

    public MenuGUI() {
        JFrame frame = new JFrame("Minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(250, 400);
        frame.setResizable(false);
        frame.add(selectionPanel(frame), BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JPanel selectionPanel(JFrame frame) {
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout( new GridLayout(5,1));
        JButton easyButton = new JButton();
        easyButton.setText("Easy - 8 x 8 - 10 mines");
        easyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.dispose();
                new BoardGUI(8,8,10);
            }
        });
        easyButton.setFocusable(false);
        selectionPanel.add(easyButton);
        JButton mediumButton = new JButton();
        mediumButton.setText("Medium - 16 x 14 - 35 mines");
        mediumButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.dispose();
                new BoardGUI(14,16,35);
            }
        });
        mediumButton.setFocusable(false);
        selectionPanel.add(mediumButton);
        JButton hardButton = new JButton();
        hardButton.setText("Hard - 26 x 14 - 93 mines");
        hardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.dispose();
                new BoardGUI(14,26,93);
            }
        });
        hardButton.setFocusable(false);
        selectionPanel.add(hardButton);
        JButton customButton = new JButton();
        customButton.setText("Custom Board");
        customButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.remove(selectionPanel);
                frame.add(customPanel(frame));
                frame.repaint();
                frame.setVisible(true);
            }
        });
        customButton.setFocusable(false);
        selectionPanel.add(customButton);
        JButton quitButton = new JButton();
        quitButton.setText("Quit");
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }
        });
        quitButton.setFocusable(false);
        selectionPanel.add(quitButton);
        return selectionPanel;
    }

    private JPanel customPanel(JFrame frame) {
        JPanel customPanel = new JPanel();
        customPanel.setLayout(new GridLayout(5,1));
        JSpinner rowSP = new JSpinner();
        JSpinner colSP = new JSpinner();
        JSpinner mineSP = new JSpinner();
        SpinnerModel rowSPModel = new SpinnerNumberModel(10,4,14,1);
        SpinnerModel colSPModel = new SpinnerNumberModel(10,8,26,1);
        SpinnerModel mineSPModel = new SpinnerNumberModel(15,1,70,1);
        rowSP.setModel(rowSPModel);
        colSP.setModel(colSPModel);
        mineSP.setModel(mineSPModel);
        customPanel.add(rowSP);
        customPanel.add(colSP);
        customPanel.add(mineSP);
        JButton startButton = new JButton();
        startButton.setText("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.dispose();
                int rows = (int)rowSP.getValue();
                int cols = (int)colSP.getValue();
                int mines = (int)( ((int)mineSP.getValue())/100.0 * (rows * cols));
                System.out.println(mines);
                new BoardGUI(rows,cols,mines);
            }
        });
        startButton.setFocusable(false);
        customPanel.add(startButton);
        JButton backButton = new JButton();
        backButton.setText("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.remove(customPanel);
                frame.add(selectionPanel(frame));
                frame.setVisible(true);
            }
        });
        backButton.setFocusable(false);
        customPanel.add(backButton);
        return customPanel;
    }

}
