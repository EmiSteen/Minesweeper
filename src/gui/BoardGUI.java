package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

import game.Minefield;

public class BoardGUI {

    private boolean gameActive = false;
    private boolean gameStarted = false;
    private final boolean devMode;
    private Minefield mf;
    private final int rows;
    private final int cols;
    private final int mines;
    private JButton minefieldButtons[][];
    private Timer timer;
    private JLabel flagLabel;
    private JLabel timeLabel;
    private final Image flagImage;
    private final ImageIcon flagImageIcon;
    private final ImageIcon mineImageIcon;
    private JButton pauseButton;
    private JButton restartButton;
    private boolean speedFlagMode = false;
    private final Color digitColors[] = {Color.BLACK, Color.BLUE, Color.GREEN, Color.RED, Color.CYAN, Color.ORANGE, Color.PINK, Color.MAGENTA, Color.BLACK};
    private final Color uncoveredTileColor = new Color(0, 190, 255);
    // unused tile colors
    // private final Color alternateTileColors[] = {new Color(180,180,180), new Color(255, 255, 255)};
    private final Color alternateTileColors[] = {new Color(255, 255, 255), new Color(255, 255, 255)};
    private final Color pausedTileColor = new Color(113, 113, 114);
    private final Color flaggedTileColor = Color.yellow;
    private final Color defaultButtonColor = new Color(125, 125, 125);
    private final Color selectedButtonColor = Color.yellow;

    BoardGUI(int rows, int cols, int mines) {
        readConfigFile();
        devMode = false;
        
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
        mf = new Minefield(this, rows, cols, mines);
        
        flagImage = Toolkit.getDefaultToolkit().createImage(game.Minesweeper.class.getResource("resources/images/flag.png"));
        flagImageIcon = new ImageIcon(flagImage.getScaledInstance(22,22, Image.SCALE_SMOOTH));
        Image mineImage = Toolkit.getDefaultToolkit().createImage(game.Minesweeper.class.getResource("resources/images/mine.png"));
        mineImageIcon = new ImageIcon(mineImage.getScaledInstance(22,22, Image.SCALE_SMOOTH));
        
        final JFrame frame = new JFrame("Minesweeper - " + cols + "x" + rows + " : " + mines);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(cols * 47, rows * 47 + 100);
        frame.setResizable(false);
        frame.add(minefieldPanel(), BorderLayout.CENTER);
        frame.add(systemPanel(frame), BorderLayout.SOUTH);
        frame.add(timePanel(), BorderLayout.NORTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void readConfigFile() {
        File configFile = new File("config/config_file.txt");
        if (configFile.exists()) {
            try {
                Scanner scanner = new Scanner(configFile);
                String modeConfig = scanner.next();
                switch (modeConfig) {
                    case "Normal":
                        speedFlagMode = false;
                        break;
                    case "SpeedFlag":
                        speedFlagMode = true;
                        break;
                    default:
                        writeFile("Normal");
                        break;
                }
                scanner.close();
            } catch (Exception ignored) {
                System.exit(1);
            }
        } else {
            writeFile("Normal");
        }
    }

    private void writeFile(String mode) {
        try {
            PrintWriter writer = new PrintWriter("config/config_file.txt");
            writer.println(mode);
            writer.close();
        } catch (Exception ignored) {
            System.exit(1);
        }
    }

    private JPanel minefieldPanel() {
        JPanel minefieldPanel = new JPanel();
        minefieldPanel.setLayout(new GridLayout(rows, cols));
        minefieldButtons = new JButton[rows][cols];
        createMinefieldButtons(minefieldPanel);
        return minefieldPanel;
    }

    private void createMinefieldButtons(JPanel minefieldPanel) {
        int colorCount;
        for (int i = 0; i < rows; i++) {
            colorCount = i % 2;
            for (int j = 0; j < cols; j++) {
                minefieldButtons[i][j] = new JButton();
                minefieldButtons[i][j].setFocusable(false);
                minefieldButtons[i][j].setPreferredSize(new Dimension(35,35));
                minefieldButtons[i][j].setMargin(new Insets(0,0,0,0));
                minefieldButtons[i][j].setFont(new Font("Ariel", Font.BOLD, 10));
                minefieldPanel.add(minefieldButtons[i][j]);
                minefieldButtons[i][j].setBackground(alternateTileColors[colorCount % 2]);
                addMinefieldButtonActionListeners(i, j);
                colorCount++;
            }
        }
    }

    private void addMinefieldButtonActionListeners(int row, int col) {
        minefieldButtons[row][col].addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (gameActive) {
                    if (speedFlagMode) {
                        speedFlagButtonAction(mouseEvent);
                    } else {
                        normalButtonAction(mouseEvent);
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

            private void normalButtonAction(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == 1) {
                    normalMouseButton1Action();
                } else if (mouseEvent.getButton() == 3) {
                    mouseButton3Action();
                }
                checkForWinningMove();
            }

            private void speedFlagButtonAction(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == 1) {
                    speedMouseButton1Action();
                } else if (mouseEvent.getButton() == 3) {
                    altMouseButton1Action();
                }
                checkForWinningMove();
            }

            private void normalMouseButton1Action() {
                altMouseButton1Action();
                constantMouseButton1Action();
            }

            private void speedMouseButton1Action() {
                mouseButton3Action();
                constantMouseButton1Action();
            }

            private void altMouseButton1Action() {
                if (!mf.isUncovered(row, col) && !mf.isFlagged(row, col)) {
                    int adjacent = mf.digMine(row, col);
                    if (adjacent == -1) {

                        gameOver();
                    } else {
                         uncoverMinefieldButtons(adjacent, row, col);
                    }
                }
            }

            private void constantMouseButton1Action() {
                if (mf.isUncovered(row, col) && mf.getAdjacent(row, col) != 0 && mf.countAdjacentFlags(row, col) == mf.getAdjacent(row, col)) {
                    boolean foundMine = digAdjacent();
                    if (foundMine) {
                        gameOver();
                    }
                }
            }

            private boolean digAdjacent() {
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
                if (!mf.isUncovered(row, col)) {
                    if (mf.isFlagged(row, col)) {
                        mf.unflag(row, col);
                        minefieldButtons[row][col].setBackground(alternateTileColors[(row%2+col)%2]);
                        minefieldButtons[row][col].setText("");
                        minefieldButtons[row][col].setIcon(null);
                    } else {
                        mf.flag(row, col);
                        minefieldButtons[row][col].setBackground(flaggedTileColor);
                        minefieldButtons[row][col].setIcon(flagImageIcon);
                    }
                    flagLabel.setText(mf.getFlagCounter() + "/" + mines);
                }
            }

            private void startGame() {
                initTimer();
                timer.start();
                gameStarted = true;
                gameActive = true;
                pauseButton.setEnabled(true);
                restartButton.setEnabled(true);
                mf.generateMinefield(row, col);
                altMouseButton1Action();
                constantMouseButton1Action();
            }

            private void initTimer() {
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
            }

            private void updateTimeLabel(JLabel timeLabel, int count) {
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

            private void checkForWinningMove() {
                if (isWinningMove()) {
                    gameOver();
                }
            }

            private boolean isWinningMove() {
                return mf.getCorrectFlagCounter() == mines && mf.getFlagCounter() == mines && mf.getNumUncovered() == rows * cols - mines;
            }

            private void gameOver() {
                repaintBoard();
                timer.stop();
                gameActive = false;
                pauseButton.setEnabled(false);
                showBombs();
            }
        });
    }

    private JPanel systemPanel(JFrame frame) {
        JPanel systemPanel = new JPanel();

        flagLabel = new JLabel();
        flagLabel.setText(mf.getFlagCounter() + "/" + mines);
        flagLabel.setPreferredSize(new Dimension(40,24));
        systemPanel.add(flagLabel);

        JButton changeBoardButton = new JButton();
        changeBoardButton.setText("Change Board");
        changeBoardButton.addActionListener(actionEvent -> {
            frame.dispose();
            new MenuGUI();
        });
        changeBoardButton.setFocusable(false);
        systemPanel.add(changeBoardButton);

        pauseButton = new JButton();
        pauseButton.setText("Pause");
        pauseButton.setEnabled(false);
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
        });
        pauseButton.setFocusable(false);
        systemPanel.add(pauseButton);

        restartButton = new JButton();
        restartButton.setText("Restart");
        restartButton.setEnabled(false);
        restartButton.addActionListener(actionEvent -> {
            timer.stop();
            restartGame();
        });
        restartButton.setFocusable(false);
        systemPanel.add(restartButton);

        JButton speedFlagButton = new JButton();
        if (speedFlagMode) {
            speedFlagButton.setBackground(selectedButtonColor);
        } else {
            speedFlagButton.setBackground(defaultButtonColor);
        }
        speedFlagButton.setIcon(new ImageIcon(flagImage.getScaledInstance(15,15, Image.SCALE_SMOOTH)));
        speedFlagButton.addActionListener(actionEvent -> {
            if (!speedFlagMode) {
                speedFlagButton.setBackground(selectedButtonColor);
                writeFile("SpeedFlag");
            } else {
                speedFlagButton.setBackground(defaultButtonColor);
                writeFile("Normal");
            }
            speedFlagMode = !speedFlagMode;
        });
        speedFlagButton.setFocusable(false);
        systemPanel.add(speedFlagButton);

        return systemPanel;
    }

    private JPanel timePanel() {
        JPanel timePanel = new JPanel();
        timeLabel = new JLabel();
        timeLabel.setText("00:00");
        timePanel.add(timeLabel);
        return timePanel;
    }

    private void repaintBoard() {
        int colorCount;
        for (int i = 0; i < rows; i++) {
            colorCount = i % 2;
            for (int j = 0; j < cols; j++) {
                if (mf.isUncovered(i, j)) {
                    minefieldButtons[i][j].setBackground(uncoveredTileColor);
                    if (mf.getAdjacent(i, j) > 0) {
                        minefieldButtons[i][j].setForeground(digitColors[mf.getAdjacent(i, j)]);
                        minefieldButtons[i][j].setText(mf.getAdjacent(i, j) + "");
                    }
                } else {
                    flagLabel.setText(mf.getFlagCounter() + "/" + mines);
                    if (mf.isFlagged(i, j)) {
                        minefieldButtons[i][j].setBackground(flaggedTileColor);
                        minefieldButtons[i][j].setIcon(flagImageIcon);
                    } else {
                        minefieldButtons[i][j].setBackground(alternateTileColors[colorCount % 2]);
                        minefieldButtons[i][j].setText("");
                        minefieldButtons[i][j].setIcon(null);
                    }
                }
                colorCount++;
            }
        }
        if (devMode) {
            showBombs();
        }
    }

    private void showBombs() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (mf.getAdjacent(i, j) == -1) {
                    if (!mf.isFlagged(i, j)) {
                        minefieldButtons[i][j].setIcon(mineImageIcon);
                    } else {
                        minefieldButtons[i][j].setBackground(Color.GREEN);
                    }
                } else if (mf.isFlagged(i, j)) {
                    minefieldButtons[i][j].setIcon(null);
                    minefieldButtons[i][j].setForeground(digitColors[0]);
                    minefieldButtons[i][j].setBackground(Color.RED);
                    minefieldButtons[i][j].setText("x");
                }
            }
        }
    }

    private void setBlankBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                minefieldButtons[i][j].setIcon(null);
                minefieldButtons[i][j].setBackground(pausedTileColor);
                minefieldButtons[i][j].setText("");
            }
        }
    }

    private void restartGame() {
        mf = new Minefield(this, rows, cols, mines);
        pauseButton.setEnabled(false);
        timeLabel.setText("00:00");
        pauseButton.setText("Pause");
        gameStarted = false;
        gameActive = false;
        restartButton.setEnabled(false);
        repaintBoard();
    }

    public void uncoverMinefieldButtons(int adjacent, int row, int col) {
        minefieldButtons[row][col].setBackground(uncoveredTileColor);
        if (mf.getAdjacent(row, col) > 0) {
            minefieldButtons[row][col].setForeground(digitColors[adjacent]);
            minefieldButtons[row][col].setText(adjacent + "");
        }
    }

}
