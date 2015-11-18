package tu_darmstadt.sudoku.controller;

/**
 * Created by Chris on 17.11.2015.
 */
public enum Symbol {

    SaveFormat(new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "O", "P"}),
    Default(new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N"}),
    Roman(new String[] {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX", "XXI", "XXII"}),
    Fancy(new String[] {"♪", "♫", "☼", "♥", "♦", "♣", "♠", "•", "○", "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N" });

    private String[] map;

    Symbol(String[] map) {
        this.map = map;
    }

    public static String getSymbol(Symbol type, int value) {
        return (type.map[value]);
    }

    public static int getValue(Symbol type, String c) {
        for(int i = 0; i < type.map.length; i++) {
            if(type.map[i].equals(c)) return i;
        }
        return -1;
    }

}
