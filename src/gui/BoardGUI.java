package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import game.Minefield;

// todo: replace flags and mines with images
// todo: Start a new game on same window when game is over

class BoardGUI {

    private boolean gameActive = false;
    private boolean gameStarted = false;
    private Minefield mf;
    private int rows;
    private int cols;
    private int mines;
    private JButton minefieldButtons[][];
    private Timer timer;
    private JLabel flagLabel;
    private boolean devMode = false;
    private Color digitColors[] = {Color.BLACK, Color.BLUE, Color.GREEN, Color.RED, Color.CYAN, Color.ORANGE, Color.PINK, Color.MAGENTA, Color.BLACK};
    private Color uncoveredTileColor = new Color(0, 190, 255);
    //unused tile colors
    //private Color alternateTileColors[] = {new Color(180,180,180), new Color(255, 255, 255)};
    private Color alternateTileColors[] = {new Color(255, 255, 255), new Color(255, 255, 255)};
    private Color pausedTileColor = new Color(113,113,114);
    private Color flaggedTileColor = Color.yellow;

    BoardGUI(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
        mf = new Minefield(rows, cols, mines);
        JFrame frame = new JFrame(cols + "x" + rows + " - " + mines + "*");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(cols * 47, rows * 47 + 100);
        frame.setResizable(false);
        frame.add(minefieldPanel(frame), BorderLayout.CENTER);
        frame.add(systemPanel(frame), BorderLayout.SOUTH);
        frame.add(timePanel(), BorderLayout.NORTH);
        frame.setVisible(true);
    }

    private JPanel minefieldPanel(JFrame frame) {
        JPanel minefieldPanel = new JPanel();
        minefieldPanel.setLayout(new GridLayout(rows, cols));
        minefieldButtons = new JButton[rows][cols];
        createMinefieldButtons(minefieldPanel, frame);
        return minefieldPanel;
    }

    private void createMinefieldButtons(JPanel minefieldPanel, JFrame frame) {
        int colorCount;
        for (int i = 0; i < rows; i++) {
            colorCount = i % 2;
            for (int j = 0; j < cols; j++) {
                minefieldButtons[i][j] = new JButton();
                minefieldButtons[i][j].setFocusable(false);
                minefieldPanel.add(minefieldButtons[i][j]);
                minefieldButtons[i][j].setBackground(alternateTileColors[colorCount%2]);
                addMinefieldButttonActionListeners(i, j, minefieldPanel, frame);
                colorCount++;
            }
        }
    }

    private void addMinefieldButttonActionListeners(int row, int col, JPanel minefieldPanel, JFrame frame) {
        minefieldButtons[row][col].addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (gameActive) {
                    if (mouseEvent.getButton() == 1) {
                        mouseButton1Action();
                    } else if (mouseEvent.getButton() == 3) {
                        mouseButton3Action();
                    }
                } else if (!gameStarted && mouseEvent.getButton() == 1) {
                    startGame();
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

            private void mouseButton1Action() {
                if (!mf.isUncovered(row, col) && !mf.isFlagged(row, col)) {
                    int value = mf.digMine(row, col);
                    if (value == -1) {
                        gameOver(0);
                    }
                } else if (mf.isUncovered(row, col) && mf.getAdjacent(row, col) != 0 && mf.countAdjacentFlags(row, col) == mf.getAdjacent(row, col)) {
                    boolean foundMine = digAdjecent();
                    if (foundMine) {
                        repaintBoard();
                        gameOver(0);
                    }
                }
                repaintBoard();
                if (isWinningMove()) {
                    gameOver(1);
                }
            }

            private boolean digAdjecent() {
                boolean foundMine = false;
                for (int k = -1; k <= 1; k++) {
                    for (int l = -1; l <= 1; l++) {
                        if (mf.isInsideBounds(row, col, k, l)) {
                            if (mf.digMine(row + k, col + l) == -1) {
                                foundMine = true;
                            }
                            if (!mf.isFlagged(row + k, col + l)) {
                                minefieldButtons[row + k][col + l].setBackground(uncoveredTileColor);
                            }
                        }
                    }
                }
                return foundMine;
            }

            private void mouseButton3Action() {
                // todo: make second right click on a covered block a question flag
                if (!mf.isUncovered(row, col)) {
                    if (mf.isFlagged(row, col)) {
                        mf.unflag(row, col);
                        int tileColorNum = (row+col)%2;
                        minefieldButtons[row][col].setBackground(alternateTileColors[tileColorNum]);
                        minefieldButtons[row][col].setText("");
                        flagLabel.setText(mf.getFlagCounter() + "/" + mines);
                    } else {
                        mf.flag(row, col);
                        minefieldButtons[row][col].setBackground(flaggedTileColor);
                        minefieldButtons[row][col].setText("^");
                        flagLabel.setText(mf.getFlagCounter() + "/" + mines);
                    }
                    if (isWinningMove()) {
                        gameOver(1);
                    }
                }
            }

            private boolean isWinningMove() {
                return mf.getCorrectFlagCounter() == mines && mf.getFlagCounter() == mines && mf.getNumUncovered() == rows * cols - mines;
            }

            private void startGame() {
                timer.start();
                gameStarted = true;
                gameActive = true;
                mf.generateMinefield(row, col);
                if (devMode) {
                    showBombs();
                }
                mouseButton1Action();
            }

            private void gameOver(int status) {
                timer.stop();
                showBombs();
                if (status == 0) {
                    JOptionPane.showMessageDialog(minefieldPanel, "Game Over!");
                } else if (status == 1) {
                    JOptionPane.showMessageDialog(minefieldPanel, "You win!");
                }
                frame.dispose();
                new MenuGUI();
            }

            private void showBombs() {
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        if (mf.getAdjacent(i, j) == -1) {
                            if (!mf.isFlagged(i, j)) {
                                minefieldButtons[i][j].setText("*");
                            }
                        } else if (mf.isFlagged(i, j)) {
                            minefieldButtons[i][j].setBackground(Color.RED);
                            minefieldButtons[i][j].setText("x");
                        }
                    }
                }
            }

        });
    }

    private JPanel systemPanel(JFrame frame) {
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
                    if (gameStarted) {
                        if (gameActive) {
                            pauseButton.setText("Play");
                            gameActive = false;
                            setBlankBoard();
                        } else {
                            pauseButton.setText("Pause");
                            repaintBoard();
                            gameActive = true;
                        }
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
                    updateTimeLabel(timeLabel, count);
                    count++;
                }
            }
        });
        return timePanel;
    }

    private void updateTimeLabel(JLabel timeLabel ,int count) {
        if (count % 60 < 10 && count / 60 < 10) {
            timeLabel.setText("0" + count / 60 + ":0" + count % 60);
        } else if (count % 60 >= 10 && count / 60 < 10) {
            timeLabel.setText("0" + count / 60 + ":" + count % 60);
        } else if (count % 60 < 10 && count / 60 >= 10) {
            timeLabel.setText(count / 60 + ":0" + count % 60);
        } else if (count % 60 >= 10 && count / 60 >= 10) {
            timeLabel.setText(count / 60 + ":" + count % 60);
        }
    }

    private void repaintBoard() {
        int colorCount;
        for (int i = 0; i < rows; i++) {
            colorCount = i % 2;
            for (int j = 0; j < cols; j++) {
                if (mf.isUncovered(i, j)) {
                    minefieldButtons[i][j].setBackground(uncoveredTileColor);
                    if (mf.getAdjacent(i, j) > 0) {
                        minefieldButtons[i][j].setText(mf.getAdjacent(i, j) + "");
                        minefieldButtons[i][j].setForeground(digitColors[mf.getAdjacent(i, j)]);
                    }
                } else {
                    minefieldButtons[i][j].setBackground(alternateTileColors[colorCount % 2]);
                    if (mf.isFlagged(i,j)) {
                        minefieldButtons[i][j].setBackground(flaggedTileColor);
                        minefieldButtons[i][j].setText("^");
                    }
                }
                colorCount++;
            }
        }
    }

    private void setBlankBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                minefieldButtons[i][j].setBackground(pausedTileColor);
                minefieldButtons[i][j].setText("");
            }
        }
    }

}
