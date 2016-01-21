package org.secuso.privacyfriendlysudoku.game;

import java.util.ArrayList;

/**
 * Created by Chris on 10.11.2015.
 */
public class CellConflictList extends ArrayList<CellConflict> {

    /**
     * Adds the CellConflict to the list.
     * We don't allow double entries.
     * @param object the object to be added
     * @return true if it could be added, false otherwise
     */
    @Override
    public boolean add(CellConflict object) {
        if(!contains(object)) {
            return super.add(object);
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[List ");
        for(int i = 0; i < size(); i++) {
            sb.append(get(i).toString());
            if(i+1 < size()) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }


}
