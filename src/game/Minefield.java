package game;

import java.util.ArrayList;
import java.util.List;

public class Minefield {
    private int minefield[][];
    private int state[][];
    private int correctFlagCounter = 0;
    private int flagCounter = 0;
    private int numUncovered = 0;
    private int rows;
    private int cols;
    private int numMines;

    public Minefield(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.numMines = mines;
        minefield = new int[rows][cols];
        state = new int[rows][cols];
    }

    public void generateMinefield(int row, int col) {
        minefield = new int[this.rows][this.cols];
        int availableMineBlockCount = getAvailableMineBlockCount(row, col);
        List<Integer> availableMineBlockPosition = new ArrayList<>(availableMineBlockCount);
        createAvailablePositionArrayList(row, col, availableMineBlockPosition);
        spawnMines(availableMineBlockCount, availableMineBlockPosition);
    }

    private int getAvailableMineBlockCount(int row, int col) {
        int availableMineBlockCount = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (isOutsidePerimeter(row, col, i, j)) {
                    availableMineBlockCount++;
                }
            }
        }
        return availableMineBlockCount;
    }

    private void createAvailablePositionArrayList(int row, int col, List<Integer> availableMineBlockPosition) {
        int count = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (isOutsidePerimeter(row, col, i, j)) {
                    availableMineBlockPosition.add(count);
                }
                count++;
            }
        }
    }

    private void spawnMines(int availableMineBlockCount, List<Integer> availableMineBlockPosition) {
        for (int i = 0; i < numMines; i++) {
            int random = (int) (Math.random() * availableMineBlockCount);
            int position = availableMineBlockPosition.remove(random);
            int rRow = position / cols;
            int rCol = position % cols;
            minefield[rRow][rCol] = -1;
            availableMineBlockCount--;
        }
    }

    private boolean isOutsidePerimeter(int row, int col, int i, int j) {
        return (i > row + 1 || i < row - 1) || (j > col + 1 || j < col - 1);
    }

    public int getAdjacent(int row, int col) {
        return minefield[row][col];
    }

    private void calculateAdjacent(int row, int col) {
        int adjecent = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (checkForMine(row, col, i, j)) {
                    adjecent++;
                }
            }
        }
        minefield[row][col] = adjecent;
    }

    private boolean checkForMine(int row, int col, int i, int j) {
        return isInsideBounds(row, col, i, j) && isMine(row+i, col+j);
    }

    public void flag(int row, int col) {
        state[row][col] = 2;
        if (minefield[row][col] == -1) {
            correctFlagCounter++;
        }
        flagCounter++;
    }

    public boolean isFlagged(int row, int col) {
        return state[row][col] == 2;
    }

    public void unflag(int row, int col) {
        state[row][col] = 0;
        if (minefield[row][col] == -1) {
            correctFlagCounter--;
        }
        flagCounter--;
    }

    public int countAdjacentFlags(int row, int col) {
        int adjecentFlags = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (isInsideBounds(row, col, i, j) && isFlagged(row+i, col+j)) {
                    adjecentFlags++;
                }
            }
        }
        return adjecentFlags;
    }

    public int getCorrectFlagCounter() {
        return this.correctFlagCounter;
    }

    public int getFlagCounter() {
        return this.flagCounter;
    }

    private void incrementUncovered() {
        numUncovered++;
    }

    public int getNumUncovered() {
        return this.numUncovered;
    }

    public int digMine(int row, int col) {
        if (!isUncovered(row,col) && isMine(row, col) && !isFlagged(row, col)) {
            return -1;
        } else if (!isUncovered(row, col) && !isFlagged(row, col)) {
            calculateAdjacent(row, col);
            uncover(row, col);
            if (minefield[row][col] == 0) {
                eliminate(row, col);
            } else {
                return minefield[row][col];
            }
        } else {
            return -2;
        }
        return -3;
    }

    private void eliminate(int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (isInsideBounds(row, col, i, j) && !isUncovered(row + i, col + j)) {
                    digMine(row + i, col + j);
                }
            }
        }
    }

    private void uncover(int row, int col) {
        state[row][col] = 1;
        incrementUncovered();
    }

    public boolean isUncovered(int row, int col) {
        return state[row][col] == 1;
    }

    public boolean isInsideBounds(int row, int col, int i, int j) {
        return row + i >= 0 && row + i < this.rows && col + j >= 0 && col + j < this.cols;
    }

    private boolean isMine(int row, int col) {
        return minefield[row][col] == -1;
    }

//    Unused method to print a grid
//
//    private void printGrid(int grid[][]) {
//        System.out.print("+");
//        for (int i = 0; i < rows; i++) {
//            System.out.print("---+");
//        }
//        System.out.println();
//        for (int i = 0; i < rows; i++) {
//            System.out.print("| ");
//            for (int j = 0; j < rows; j++) {
//                if (grid[i][j] != -1) {
//                    System.out.print(grid[i][j] + " | ");
//                } else {
//                    System.out.print("*" + " | ");
//                }
//            }
//            System.out.println();
//            System.out.print("+");
//            for (int j = 0; j < rows; j++) {
//                System.out.print("---+");
//            }
//            System.out.println();
//        }
//    }

//    unused method to print the state of fields of the board
//
//    private void printState() {
//        printGrid(state);
//    }

//    unused method to print the minefield board
//    private void printMinefield() {
//        printGrid(minefield);
//    }

}
