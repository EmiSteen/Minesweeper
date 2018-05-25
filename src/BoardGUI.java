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
    private Color uncoveredColor = new Color(0, 190, 255);
    private Color alternateTileColors[] = {new Color(255, 255, 255), new Color(255, 255, 255)};

    BoardGUI(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
        Minefield mf = new Minefield(rows, cols, mines);
        JFrame frame = new JFrame("Minesweeper");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
        int colorCount;
        for (int i = 0; i < rows; i++) {
            if (i % 2 == 0) {
                colorCount = 0;
            } else {
                colorCount = 1;
            }
            for (int j = 0; j < cols; j++) {
                minefieldButtons[i][j] = new JButton();

                minefieldButtons[i][j].setFocusable(false);
                minefieldPanel.add(minefieldButtons[i][j]);
                if (colorCount % 2 == 0) {
                    minefieldButtons[i][j].setBackground(alternateTileColors[0]);
                } else {
                    minefieldButtons[i][j].setBackground(alternateTileColors[1]);
                }
                colorCount++;
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
                                    showBombs();
                                }
                            }
                            if (!mf.isUncovered(row, col) && !mf.isFlagged(row, col)) {
                                int value = mf.digMine(row, col);
                                if (value == -1) {
                                    gameOver(0);
                                }
                            } else if (mf.isUncovered(row, col) && mf.getValue(row, col) != 0 && mf.checkAdjecentFlags(row, col) == mf.getValue(row, col)) {
                                if (digAdjecent()) {
                                    repaintBoard(minefieldButtons, mf);
                                    gameOver(0);
                                }
                            }
                            repaintBoard(minefieldButtons, mf);
                            if (mf.getCorrectFlagCounter() == mines && mf.getFlagCounter() == mines && mf.getNumUncovered() == rows * cols - mines) {
                                gameOver(1);
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
                                    gameOver(1);
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

                    private void showBombs() {
                        for (int i = 0; i < rows; i++) {
                            for (int j = 0; j < cols; j++) {
                                if (mf.getValue(i, j) == -1) {
                                    if (!mf.isFlagged(i, j)) {
                                        minefieldButtons[i][j].setText("*");
                                    }
                                } else if (mf.isFlagged(i, j)) {
                                    minefieldButtons[i][j].setText("x");
                                }
                            }
                        }
                    }

                    private boolean digAdjecent() {
                        boolean foundMine = false;
                        for (int k = -1; k < 2; k++) {
                            for (int l = -1; l < 2; l++) {
                                if (row + k >= 0 && row + k < rows && col + l >= 0 && col + l < cols) {
                                    if (mf.digMine(row + k, col + l) == -1) {
                                        foundMine = true;
                                    }
                                    if (!mf.isFlagged(row + k, col + l)) {
                                        minefieldButtons[row + k][col + l].setBackground(uncoveredColor);
                                    }
                                }
                            }
                        }
                        return foundMine;
                    }

                    private void gameOver(int status) {
                        showBombs();
                        timer.stop();
                        if (status == 0) {
                            JOptionPane.showMessageDialog(minefieldPanel, "Game Over!");
                        } else if (status == 1) {
                            JOptionPane.showMessageDialog(minefieldPanel, "You win!");
                        }
                        frame.dispose();
                        new MenuGUI();
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
        JButton pauseButton = new JButton();
        pauseButton.setText("Pause");
        pauseButton.addActionListener(actionEvent -> {
                    if (gameActive) {
                        pauseButton.setText("Play");
                        gameActive = false;
                    } else {
                        pauseButton.setText("Pause");
                        gameActive = true;
                    }
                }
        );
        pauseButton.setFocusable(false);
        systemPanel.add(pauseButton);
        JButton restartButton = new JButton();
        restartButton.setText("Restart");
        restartButton.addActionListener(actionEvent -> {
            frame.dispose();
            new BoardGUI(rows, cols, mines);
        });
        restartButton.setFocusable(false);
        systemPanel.add(restartButton);
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
                if (gameActive) {
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
            }
        });
        return timePanel;
    }

    private void repaintBoard(JButton[][] minefieldButtons, Minefield mf) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (mf.isUncovered(i, j)) {
                    minefieldButtons[i][j].setBackground(uncoveredColor);
                    if (mf.getValue(i, j) > 0) {
                        minefieldButtons[i][j].setText(mf.getValue(i, j) + "");
                        minefieldButtons[i][j].setForeground(colors[mf.getValue(i, j)]);
                    }
                }
            }
        }
    }
}
