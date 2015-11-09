package tu_darmstadt.sudoku.game;

/**
 * Created by Chris on 06.11.2015.
 */
public class GameCell {

    private int row = -1;
    private int col = -1;
    private int value = 0;
    private boolean fixed = false;
    private boolean notes[];
    private int size = 0;

    public GameCell(int row, int col, int size) {
        this(row,col,size,0);
    }

    /**
     * Constructor with init parameter. The cell will have that value and be uneditable.
     * @param val the start value to be saved in the cell
     */
    public GameCell(int row, int col, int size, int val) {
        this.row = row;
        this.col = col;
        this.size = size;
        clearNotes();
        if(0 < val && val <= size) {
            setValue(val);
            setFixed(true);
        } else {
            setValue(0);
            setFixed(false);

        }
    }

    /**
     * Set the value of the cell, only if cell isn't fixed.
     * @param val the value to be assigned to the cell.
     */
    public void setValue(int val) {
        clearNotes();
        value = val;
    }

    public int getValue() {
        return value;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    /**
     * Toggle notes in this cell, if cell isn't fixed.
     * @param val the value to be toggled.
     */
    public void setNotes(int val) {
        // only possible to set notes if cell isn't fixed.
        // TODO .. put logic here?
        notes[val - 1] = !notes[val - 1];
    }

    public boolean[] getNotes() {
        return notes;
    }

    /**
     * Clear the notes array (set everything to false).
     */
    public void clearNotes() {
        notes = new boolean[size];
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean b) {
        fixed = b;
    }
}
