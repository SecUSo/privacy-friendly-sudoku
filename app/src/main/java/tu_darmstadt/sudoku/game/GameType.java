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
    Default_6x6,
    X_9x9,
    Hyper_9x9;

    public static List<GameType> getValidGameTypes() {
        LinkedList<GameType> result = new LinkedList<>();
        result.add(Default_6x6);
        result.add(Default_9x9);
        result.add(Default_12x12);
        return result;
    }

    public static int getSize(GameType type) {
        switch(type) {
            case X_9x9:
            case Hyper_9x9:
            case Default_9x9:
                return 9;
            case Default_12x12:
                return 12;
            case Default_6x6:
                return 6;
            case Unspecified:
            default:
                return 1;
        }
    }

    public static int getSectionHeight(GameType type) {
        switch(type) {
            case X_9x9:
            case Hyper_9x9:
            case Default_9x9:
                return 3;
            case Default_12x12:
                return 3;
            case Default_6x6:
                return 2;
            case Unspecified:
            default:
                return 1;
        }
    }

    public static int getSectionWidth(GameType type) {
        switch(type) {
            case X_9x9:
            case Hyper_9x9:
            case Default_9x9:
                return 3;
            case Default_12x12:
                return 4;
            case Default_6x6:
                return 3;
            case Unspecified:
            default:
                return 1;
        }
    }
}
