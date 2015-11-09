package tu_darmstadt.sudoku.game;

/**
 * Created by Chris on 06.11.2015.
 */
public class GameField {

    private int size;
    private GameCell[][] field;

    public GameField() {
        this(9);
    }

    public GameField(int size) {
        this.size = size;
        field = new GameCell[size][size];



        // TODO: this is a placeholder, because we don't have real levels yet.
        int[][] level = {{ 5, 0, 1,  9, 0, 0,  0, 0, 0 },
                         { 2, 0, 0,  0, 0, 4,  9, 5, 0 },
                         { 3, 9, 0,  7, 0, 0,  0, 2, 6 },

                         { 0, 3, 0,  0, 0, 1,  0, 7, 2 },
                         { 0, 0, 6,  0, 5, 7,  0, 0, 0 },
                         { 0, 7, 2,  0, 0, 9,  0, 4, 1 },

                         { 0, 0, 0,  0, 7, 0,  4, 0, 9 },
                         { 6, 4, 0,  0, 0, 0,  0, 0, 0 },
                         { 7, 0, 0,  0, 1, 0,  3, 0, 5 }};

        initCells(level);
    }

    public void initCells(int[][] level) {
        // Initit the game field
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                field[i][j] = new GameCell(i,j,size,level[i][j]);
            }
        }
    }

    public GameCell getCell(int row, int col) {
        return field[row][col];
    }

    public GameCell[][] getField() {
        return field;
    }

    public GameCell[] getRow(int row) {
        return field[row];
    }

    public GameCell[] getColumn(int col) {
        GameCell[] result = new GameCell[size];
        for(int i = 0; i < size ; i++) {    // row
            for(int j = 0 ; j < size ; j++) {   // col
                if(j == col) {
                    result[i] = field[i][j];
                }
            }
        }
        return result;
    }

    public GameCell[] getSection(int sec) {
        GameCell[] result = new GameCell[size];
        int c = 0;
        for(int i = 0; i < size ; i++) {    // row
            for(int j = 0 ; j < size ; j++) {   // col
                if(Math.floor(i/3)*3 + Math.floor(j/3) == sec) {
                    result[c++] = field[i][j];
                }
                if(c > size) {
                    break;
                }
            }
        }
        return result;
    }







}
