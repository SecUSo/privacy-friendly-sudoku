package tu_darmstadt.sudoku.controller;

/**
 * Created by Chris on 17.11.2015.
 */
public enum Symbol {

    Default(new char[] {'1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N'}),
    Fancy(new char[] {'♪', '♫', '☼', '♥', '♦', '♣', '♠', '•', '○', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N' });

    private char[] map;

    Symbol(char[] map) {
        this.map = map;
    }

    public static String getSymbol(Symbol type, int value) {
        return String.valueOf(type.map[value]);
    }

    public static int getValue(Symbol type, char c) {
        for(int i = 0; i < type.map.length; i++) {
            if(type.map[i] == c) return i;
        }
        return -1;
    }

}
