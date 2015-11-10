package tu_darmstadt.sudoku.game;

/**
 * Created by Chris on 06.11.2015.
 */
public class GameCell {

    private int row = 0;
    private int col = 0;
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
        deleteNotes();
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
    public void toggleNote(int val) {
        if(!isFixed())
            notes[val - 1] = !notes[val - 1];
    }

    public void setNote(int val) {
        if(!isFixed())
            notes[val - 1] = true;
    }

    public void deleteNote(int val) {
        if(!isFixed())
            notes[val - 1] = false;
    }

    public boolean[] getNotes() {
        return notes;
    }

    /**
     * Clear the notes array (set everything to false).
     */
    public void deleteNotes() {
        notes = new boolean[size];
    }

    public boolean isFixed() {
        return fixed;
    }

    private void setFixed(boolean b) {
        fixed = b;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof GameCell)) return false;
        if(((GameCell) other).getCol() != this.getCol()) return false;
        if(((GameCell) other).getRow() != this.getRow()) return false;
        if(((GameCell) other).isFixed() != this.isFixed()) return false;
        if(((GameCell) other).getValue() != this.getValue()) return false;
        if(((GameCell) other).getNotes().length != this.getNotes().length) return false;
        for(int i = 0; i < notes.length; i++) {
            if(((GameCell) other).getNotes()[i] != this.getNotes()[i]) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{("); sb.append(row); sb.append("|"); sb.append(col); sb.append(")");
        sb.append(" ");
        if(value == 0) {
            sb.append("[");
            boolean addedNotes = false;
            for(int i = 0; i < size; i++) {
                if(notes[i]) {
                    if(addedNotes) {
                        sb.append(" ,");
                    }
                    sb.append(i+1);
                    addedNotes = true;
                }
            }
            sb.append("]");
        } else {
            sb.append(value);
        }
        sb.append("}");

        return sb.toString();
    }
}
