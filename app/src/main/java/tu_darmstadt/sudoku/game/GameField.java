package tu_darmstadt.sudoku.game;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by Christopher Beckmann on 06.11.2015.
 */
public class GameField implements Cloneable {

    //private int id;
    private int sectionHeight;
    private int sectionWidth;
    //private List additionalSections
    private int size;
    private GameCell[][] field;

    public GameField() {
        this(GameType.Default_9x9);
    }

    public GameField(GameType type) {
        setGameType(type);

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

    private void setGameType(GameType type) {
        switch(type) {
            case Default_9x9:
                this.size = 9;
                this.sectionHeight = 3;
                this.sectionWidth = 3;
                break;
            case Unspecified:
            default:
                this.size = 1;
                this.sectionHeight = 1;
                this.sectionWidth = 1;
                throw new IllegalArgumentException("GameType can not be unspecified.");
        }
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

    public LinkedList<GameCell> getRow(int row) {
        LinkedList<GameCell> result = new LinkedList<GameCell>();
        for(GameCell c : field[row]) {
            result.add(c);
        }
        return result;
    }

    public LinkedList<GameCell> getColumn(int col) {
        LinkedList<GameCell> result = new LinkedList<GameCell>();
        for(int i = 0; i < size ; i++) {    // row
            for(int j = 0 ; j < size ; j++) {   // col
                if(j == col) {
                    result.add(field[i][j]);
                }
            }
        }
        return result;
    }

    public LinkedList<GameCell> getSection(final int sec) {
        return actionOnCells(new ICellAction<LinkedList<GameCell>>() {
            @Override
            public LinkedList<GameCell> action(GameCell gc, LinkedList<GameCell> existing) {
                if((int)(Math.floor(gc.getRow()/sectionHeight)*sectionHeight + Math.floor(gc.getCol()/sectionWidth)) == sec) {
                    existing.add(gc);
                }
                return existing;
            }}, new LinkedList<GameCell>());
    }

    public LinkedList<GameCell> getSection(int row, int col) {
        int sec = (int) (Math.floor(row / sectionHeight) * sectionHeight + Math.floor(col / sectionWidth));
        return getSection(sec);
    }

    public int getSize() {
        return size;
    }

    public <T> T actionOnCells(ICellAction<T> ca, T existing) {
        for(int i = 0; i < field.length; i++) {
            for(int j = 0; j < field[i].length; j++) {
                existing = ca.action(field[i][j], existing);
            }
        }
        return existing;
    }

    @Override
    public GameField clone() throws CloneNotSupportedException {
        GameField clone = (GameField) super.clone();

        GameCell[][] cloneField = new GameCell[size][size];
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                cloneField[i][j] = field[i][j].clone();
            }
        }
        clone.field = cloneField;

        return clone;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[GameField: \n");

        for (int i = 0; i < size; i++) {

            for (int j = 0; j < size; j++) {
                if (j % sectionWidth == 0) {
                    sb.append("\t");
                }

                sb.append(getField()[i][j]);
                sb.append(" ");
            }

            sb.append("]");
        }
        return sb.toString();
    }
}
