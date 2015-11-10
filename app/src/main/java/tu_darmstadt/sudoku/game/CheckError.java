package tu_darmstadt.sudoku.game;

import java.util.LinkedList;

/**
 * Created by Chris on 08.11.2015.
 */
public class CheckError {

    /*
     * An Error is created for every cell.
     */

    private int value = 0;
    private LinkedList<GameCell> list = new LinkedList<GameCell>();

    public CheckError(GameCell first, GameCell second) {
        add(first);
        add(second);
    }

    public void add(GameCell cell) {
        if(value == 0 && cell.getValue() != 0) {
            value = cell.getValue();
        }

        if(!list.contains(cell)) {
            list.add(cell);
        }
    }

    public boolean contains(GameCell c) {
        return list.contains(c);
    }

    public void merge(CheckError other) {
        // merge the same object? ... why would you do that. This would simply clear the CheckError.
        if(equals(other)) return;

        for(GameCell c : other.list) {
            if(!contains(c)) {
                add(c);
            }
        }
        // Empty the other list.... because they are merged now.
        other.list = new LinkedList<GameCell>();
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof CheckError)) {
            return false;
        }
        if(list.size() != ((CheckError) other).list.size()) return false;

        for(int i = 0; i < list.size(); i++) {
            if(!list.get(i).equals(((CheckError) other).list.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[CheckError: "); sb.append(value);
        for(GameCell c : list) {
            sb.append(" (");
            sb.append(c.getRow());
            sb.append("|");
            sb.append(c.getCol());
            sb.append(")");
        }
        sb.append("]");
        return sb.toString();
    }
}
