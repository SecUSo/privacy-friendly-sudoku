package tu_darmstadt.sudoku.game;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Chris on 09.11.2015.
 */
public enum GameType {
    Unspecified,
    Default_9x9,
    Default_12x12,
    Default_6x6;

    public static List<GameType> getValidGameTypes() {
        LinkedList<GameType> result = new LinkedList<>();
        result.add(Default_6x6);
        result.add(Default_9x9);
        result.add(Default_12x12);
        return result;
    }


}
