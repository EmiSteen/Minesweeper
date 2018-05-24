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

    public int[][] generateMinefield(int row, int col) {
        minefield = new int[this.rows][this.cols];
        int elements = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if ((i > row + 1 || i < row - 1) || (j > col + 1 || j < col - 1)) {
                    elements++;
                }
            }
        }
        List<Integer> availablePosition = new ArrayList<Integer>(elements);
        int count = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if ((i > row + 1 || i < row - 1) || (j > col + 1 || j < col - 1)) {
                    availablePosition.add(count);
                }
                count++;
            }
        }
        for (int i = 0; i < numMines; i++) {
            int random = (int) (Math.random() * elements);
            int position = availablePosition.remove(random);
            int rRow = position/cols;
            int rCol = position%cols;
            minefield[rRow][rCol] = -1;
            elements--;
        }
        return minefield;
    }

    public int getValue (int row, int col) {
        return minefield[row][col];
    }

    public void checkAdjecency(int row, int col) {
        int adjecent = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (row + i >= 0 && row + i < this.rows && col + j >= 0 && col + j < this.cols && minefield[row+i][col+j] == -1) {
                    adjecent++;
                }
            }
        }
        minefield[row][col] = adjecent;
    }

    public void flag(int row, int col) {
        state[row][col] = 2;
        if (minefield[row][col] == -1) {
            correctFlagCounter++;
        }
        flagCounter++;
    }

    public boolean isFlagged(int row, int col) {
        if (state[row][col] == 2) {
            return true;
        } else {
            return false;
        }
    }

    public void unflag(int row, int col) {
        state[row][col] = 0;
        if (minefield[row][col] == -1) {
            correctFlagCounter--;
        }
        flagCounter--;
    }

    public void uncover(int row, int col) {
        state[row][col] = 1;
        incrementUncovered();
    }

    public boolean isUncovered(int row, int col) {
        if(state[row][col] == 1) {
            return true;
        } else {
            return false;
        }
    }

    public int checkAdjecentFlags(int row, int col) {
        int adjecentFlags = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (row + i >= 0 && row + i < this.rows && col + j >= 0 && col + j < this.cols && state[row+i][col+j] == 2) {
                    adjecentFlags++;
                }
            }
        }
        return adjecentFlags;
    }

    private void printGrid(int grid[][]) {
        System.out.print("+");
        for (int i = 0; i < rows; i++) {
            System.out.print("---+");
        }
        System.out.println();
        for (int i = 0; i < rows; i++) {
            System.out.print("| ");
            for (int j = 0; j < rows; j++) {
                if (grid[i][j] != -1) {
                    System.out.print(grid[i][j] + " | ");
                } else {
                    System.out.print("*" + " | ");
                }
            }
            System.out.println();
            System.out.print("+");
            for (int j = 0; j < rows; j++) {
                System.out.print("---+");
            }
            System.out.println();
        }
    }

    public void printState() {
        printGrid(state);
    }

    public void printMinefield() {
        printGrid(minefield);
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

}
