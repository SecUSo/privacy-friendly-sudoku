package org.secuso.privacyfriendlysudoku.game;

/**
 * Created by Chris on 08.11.2015.
 */
public class CellConflict {

    /*
     * A conflict is created for every cell.
     */
    private GameCell c1 = null;
    private GameCell c2 = null;


    public CellConflict(GameCell first, GameCell second) {
        c1 = first;
        c2 = second;
    }

    public boolean contains(GameCell c) {
        return c1.equals(c) || c2.equals(c);
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof CellConflict)) {
            return false;
        }
        if(!(c1.equals(((CellConflict) other).c1) && c2.equals(((CellConflict) other).c2)
                || c1.equals(((CellConflict) other).c2) && c2.equals(((CellConflict) other).c1))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Conflict ");
        sb.append(c1.toString());
        sb.append(" ");
        sb.append(c2.toString());
        sb.append("]");
        return sb.toString();
    }
}
