import java.util.ArrayList;
import java.util.List;

class Minefield {
    private int minefield[][];
    private int state[][];
    private int correctFlagCounter = 0;
    private int flagCounter = 0;
    private int numUncovered = 0;
    private int rows;
    private int cols;
    private int numMines;

    Minefield(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.numMines = mines;
        minefield = new int[rows][cols];
        state = new int[rows][cols];
    }

    void generateMinefield(int row, int col) {
        minefield = new int[this.rows][this.cols];
        int elements = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if ((i > row + 1 || i < row - 1) || (j > col + 1 || j < col - 1)) {
                    elements++;
                }
            }
        }
        List<Integer> availablePosition = new ArrayList<>(elements);
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
            int rRow = position / cols;
            int rCol = position % cols;
            minefield[rRow][rCol] = -1;
            elements--;
        }
    }

    int getValue(int row, int col) {
        return minefield[row][col];
    }

    private void checkAdjecency(int row, int col) {
        int adjecent = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (row + i >= 0 && row + i < this.rows && col + j >= 0 && col + j < this.cols && minefield[row + i][col + j] == -1) {
                    adjecent++;
                }
            }
        }
        minefield[row][col] = adjecent;
    }

    void flag(int row, int col) {
        state[row][col] = 2;
        if (minefield[row][col] == -1) {
            correctFlagCounter++;
        }
        flagCounter++;
    }

    boolean isFlagged(int row, int col) {
        return state[row][col] == 2;
    }

    void unflag(int row, int col) {
        state[row][col] = 0;
        if (minefield[row][col] == -1) {
            correctFlagCounter--;
        }
        flagCounter--;
    }

    private void uncover(int row, int col) {
        state[row][col] = 1;
        incrementUncovered();
    }

    boolean isUncovered(int row, int col) {
        return state[row][col] == 1;
    }

    int checkAdjecentFlags(int row, int col) {
        int adjecentFlags = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (row + i >= 0 && row + i < this.rows && col + j >= 0 && col + j < this.cols && state[row + i][col + j] == 2) {
                    adjecentFlags++;
                }
            }
        }
        return adjecentFlags;
    }

//    Unused method to print a grid
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
//    private void printState() {
//        printGrid(state);
//    }

//    unused method to print the minefield board
//    private void printMinefield() {
//        printGrid(minefield);
//    }

    int getCorrectFlagCounter() {
        return this.correctFlagCounter;
    }

    int getFlagCounter() {
        return this.flagCounter;
    }

    private void incrementUncovered() {
        numUncovered++;
    }

    int getNumUncovered() {
        return this.numUncovered;
    }

    int digMine(int row, int col) {
        if (!isUncovered(row,col) && getValue(row,col) == -1 && !isFlagged(row, col)) {
            return -1;
        } else if (!isUncovered(row, col) && !isFlagged(row, col)) {
            checkAdjecency(row, col);
            uncover(row, col);
            if (getValue(row, col) == 0) {
                eliminate(row, col);
            } else {
                return getValue(row, col);
            }
        } else {
            return -2;
        }
        return 0;
    }

    private void eliminate(int row, int col) {
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (row + i >= 0 && row + i < rows && col + j >= 0 && col + j < cols && !isUncovered(row + i, col + j)) {
                    digMine(row + i, col + j);
                }
            }
        }
    }

}
