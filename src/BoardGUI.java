import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class BoardGUI {

    private boolean gameActive = false;
    private int rows;
    private int cols;
    private int mines;
    private Timer timer;
    private JLabel flagLabel;
    private boolean devMode = false;
    private Color colors[] = {Color.black, Color.blue, Color.green, Color.red, Color.cyan, Color.orange, Color.pink, Color.MAGENTA, Color.BLACK};

    BoardGUI(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
        Minefield mf = new Minefield(rows, cols, mines);
        JFrame frame = new JFrame("Minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(cols * 47, rows * 47 + 100);
        frame.setResizable(false);
        frame.add(minefieldPanel(mf, frame), BorderLayout.CENTER);
        frame.add(systemPanel(mf, frame), BorderLayout.SOUTH);
        frame.add(timePanel(), BorderLayout.NORTH);
        frame.setVisible(true);
    }

    private JPanel minefieldPanel(Minefield mf, JFrame frame) {
        JPanel minefieldPanel = new JPanel();
        minefieldPanel.setLayout(new GridLayout(rows, cols));
        JButton minefieldButtons[][] = new JButton[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                minefieldButtons[i][j] = new JButton();
                minefieldButtons[i][j].setBackground(new Color(255, 255, 255));
                minefieldButtons[i][j].setFocusable(false);
                minefieldPanel.add(minefieldButtons[i][j]);
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int row = i;
                int col = j;
                minefieldButtons[i][j].addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent mouseEvent) {
                        if (mouseEvent.getButton() == 1) {
                            if (!gameActive) {
                                timer.start();
                                gameActive = true;
                                mf.generateMinefield(row, col);
                                if (devMode) {
                                    for (int k = 0; k < rows; k++) {
                                        for (int l = 0; l < cols; l++) {
                                            if (mf.getValue(k, l) == -1) {
                                                minefieldButtons[k][l].setText("*");
                                            }
                                        }
                                    }
                                }
                            }
                            if (!mf.isUncovered(row, col) && !mf.isFlagged(row, col)) {
                                minefieldButtons[row][col].setBackground(new Color(0, 190, 255));
                                int value = mf.digMine(row, col);
                                System.out.println(value);
                                if (value == -1) {
                                    showBombs(mf);
                                    timer.stop();
                                    JOptionPane.showMessageDialog(minefieldPanel, "Game Over!");
                                    frame.dispose();
                                    new MenuGUI();
                                } else if (value > 0) {
                                    minefieldButtons[row][col].setForeground(colors[mf.getValue(row, col)]);
                                    minefieldButtons[row][col].setText(value + "");
                                }
                                mf.digMine(row, col);
                            } else if (mf.isUncovered(row, col) && mf.getValue(row, col) != 0 && mf.checkAdjecentFlags(row, col) == mf.getValue(row, col)) {
                                for (int k = -1; k < 2; k++) {
                                    for (int l = -1; l < 2; l++) {
                                        if (row + k >= 0 && row + k < rows && col + l >= 0 && col + l < cols) {
                                            mf.digMine(row + k, col + l);
                                        }
                                    }
                                }
                            }
                            if (mf.getCorrectFlagCounter() == mines && mf.getFlagCounter() == mines && mf.getNumUncovered() == rows * cols - mines) {
                                timer.stop();
                                JOptionPane.showMessageDialog(minefieldPanel, "You win!");
                                frame.dispose();
                                new MenuGUI();
                            }
                        } else if (gameActive && mouseEvent.getButton() == 3) {
                            if (!mf.isUncovered(row, col)) {
                                if (mf.isFlagged(row, col)) {
                                    mf.unflag(row, col);
                                    minefieldButtons[row][col].setText("");
                                    flagLabel.setText(mf.getFlagCounter() + "/" + mines);
                                } else {
                                    mf.flag(row, col);
                                    minefieldButtons[row][col].setText("^");
                                    flagLabel.setText(mf.getFlagCounter() + "/" + mines);
                                }
                                if (mf.getCorrectFlagCounter() == mines && mf.getFlagCounter() == mines && mf.getNumUncovered() == rows * cols - mines) {
                                    timer.stop();
                                    JOptionPane.showMessageDialog(minefieldPanel, "You win!");
                                    frame.dispose();
                                    new MenuGUI();
                                } else if (mf.getCorrectFlagCounter() == mines && mf.getFlagCounter() == mines) {
                                    System.out.println(mf.getNumUncovered() + " : " + ((rows * cols) - mines));
                                }
                            }
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent mouseEvent) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent mouseEvent) {
                    }

                    @Override
                    public void mouseEntered(MouseEvent mouseEvent) {

                    }

                    @Override
                    public void mouseExited(MouseEvent mouseEvent) {

                    }

//                    private void digMine(int row, int col) {
//                        if (!mf.isFlagged(row, col)) {
//                            minefieldButtons[row][col].setBackground(new Color(0, 190, 255));
//                        }
//                        if (mf.getValue(row, col) == -1 && !mf.isFlagged(row, col)) {
//                            showBombs(mf);
//                            timer.stop();
//                            JOptionPane.showMessageDialog(minefieldPanel, "Game Over!");
//                            frame.dispose();
//                            new MenuGUI();
//                        } else if (!mf.isUncovered(row, col) && !mf.isFlagged(row, col)) {
//                            mf.checkAdjecency(row, col);
//                            mf.uncover(row, col);
//                            if (mf.getValue(row, col) == 0) {
//                                eliminate(row, col, mf);
//                            } else {
//                                minefieldButtons[row][col].setForeground(colors[mf.getValue(row, col)]);
//                                minefieldButtons[row][col].setText(mf.getValue(row, col) + "");
//                            }
//                        }
//                    }

//                    private void eliminate(int row, int col, Minefield mf) {
//                        for (int i = -1; i < 2; i++) {
//                            for (int j = -1; j < 2; j++) {
//                                if (row + i >= 0 && row + i < rows && col + j >= 0 && col + j < cols && !mf.isUncovered(row + i, col + j)) {
//                                    digMine(row + i, col + j);
//                                }
//                            }
//                        }
//                    }

                    private void showBombs(Minefield mf) {
                        for (int i = 0; i < rows; i++) {
                            for (int j = 0; j < cols; j++) {
                                if (mf.getValue(i, j) == -1) {
                                    minefieldButtons[row][col].setForeground(Color.black);
                                    minefieldButtons[i][j].setText("*");
                                }
                            }
                        }
                    }
                });
            }
        }
        return minefieldPanel;
    }

    private JPanel systemPanel(Minefield mf, JFrame frame) {
        JPanel systemPanel = new JPanel();
        flagLabel = new JLabel();
        flagLabel.setText(mf.getFlagCounter() + "/" + mines);
        systemPanel.add(flagLabel);
        JButton changeBoardButton = new JButton();
        changeBoardButton.setText("Change Board");
        changeBoardButton.addActionListener(actionEvent -> {
            frame.dispose();
            new MenuGUI();
        });
        changeBoardButton.setFocusable(false);
        systemPanel.add(changeBoardButton);
        JButton restartButton = new JButton();
        restartButton.setText("Restart");
        restartButton.addActionListener(actionEvent -> {
            frame.dispose();
            new BoardGUI(rows, cols, mines);
        });
        restartButton.setFocusable(false);
        systemPanel.add(restartButton);
        JButton quitButton = new JButton();
        quitButton.setText("Quit");
        quitButton.addActionListener(actionEvent ->
                System.exit(0));
        quitButton.setFocusable(false);
        systemPanel.add(quitButton);
        return systemPanel;
    }

    private JPanel timePanel() {
        JPanel timePanel = new JPanel();
        JLabel timeLabel = new JLabel();
        timeLabel.setText("00:00");
        timePanel.add(timeLabel);
        timer = new Timer(1000, new ActionListener() {
            int count = 1;

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (count % 60 < 10 && count / 60 < 10) {
                    timeLabel.setText("0" + count / 60 + ":0" + count % 60);
                } else if (count % 60 >= 10 && count / 60 < 10) {
                    timeLabel.setText("0" + count / 60 + ":" + count % 60);
                } else if (count % 60 < 10 && count / 60 >= 10) {
                    timeLabel.setText(count / 60 + ":0" + count % 60);
                } else if (count % 60 >= 10 && count / 60 >= 10) {
                    timeLabel.setText(count / 60 + ":" + count % 60);
                }
                count++;
            }
        });
        return timePanel;
    }

    private void repaintBoard(JButton[][] minefieldButtons, Minefield mf) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (mf.isUncovered(i,j)) {
                    minefieldButtons[i][j].setBackground(new Color(0, 190, 255));
                    if (mf.getValue(i,j)>0) {
                        minefieldButtons[i][j].setText(mf.getValue(i,j)+ "");
                        minefieldButtons[i][j].setForeground(colors[mf.getValue(i,j)]);
                    }
                }
            }
        }
    }

}
