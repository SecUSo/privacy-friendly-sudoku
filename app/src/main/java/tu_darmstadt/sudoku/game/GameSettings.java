package tu_darmstadt.sudoku.game;

/**
 * Created by Chris on 09.11.2015.
 */
public class GameSettings {
    private static boolean enableAutomaticNoteDeletion = true;
    private static boolean highlightConnectedRow = true;
    private static boolean highlightConnectedColumn = true;
    private static boolean highlightConnectedSection = true;


    public static boolean getEnableAutomaticNoteDeletion() {
        return enableAutomaticNoteDeletion;
    }
    public static boolean getHighlightConnectedRow() {
        return highlightConnectedRow;
    }
    public static boolean getHighlightConnectedColumn() {
        return highlightConnectedColumn;
    }
    public static boolean getHighlightConnectedSection() {
        return highlightConnectedSection;
    }
}
