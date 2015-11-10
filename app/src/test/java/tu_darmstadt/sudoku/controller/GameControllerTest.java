package tu_darmstadt.sudoku.controller;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import tu_darmstadt.sudoku.game.GameType;

import static org.junit.Assert.*;

/**
 * Created by Chris on 10.11.2015.
 */
public class GameControllerTest {

    GameController controller;

    @Before
    public void init() {
        controller = new GameController();
        int[][] level = {{ 5, 0, 1,  9, 0, 0,  0, 0, 0 },
                         { 2, 0, 0,  0, 0, 4,  9, 5, 0 },
                         { 3, 9, 0,  7, 0, 0,  0, 2, 6 },

                         { 0, 3, 0,  0, 0, 1,  0, 7, 2 },
                         { 0, 0, 6,  0, 5, 7,  0, 0, 0 },
                         { 0, 7, 2,  0, 0, 9,  0, 4, 1 },

                         { 0, 0, 0,  0, 7, 0,  4, 0, 9 },
                         { 6, 4, 0,  0, 0, 0,  0, 0, 0 },
                         { 7, 0, 0,  0, 1, 0,  3, 0, 5 }};


    }


    @Test
    public void setValueOfFixedCellTest() {
        assertEquals(5, controller.getValue(0, 0));
        controller.setValue(0, 0, 6);
        assertEquals(5, controller.getValue(0, 0));
    }

    @Test
    public void setValueOfFreeCellTest() {
        assertEquals(0, controller.getValue(0, 1));
        controller.setValue(0, 1, 6);
        assertEquals(6, controller.getValue(0, 1));
    }

    @Test
    public void solveTest1() {
        controller.setValue(0, 1, 8);        controller.setValue(0, 4, 2);
        controller.setValue(0, 5, 6);        controller.setValue(0, 6, 7);
        controller.setValue(0, 7, 3);        controller.setValue(0, 8, 4);
        controller.setValue(1, 1, 6);        controller.setValue(1, 2, 7);
        controller.setValue(1, 3, 1);        controller.setValue(1, 4, 3);
        controller.setValue(1, 8, 8);        controller.setValue(2, 2, 4);
        controller.setValue(2, 4, 8);        controller.setValue(2, 5, 5);
        controller.setValue(2, 6, 1);        controller.setValue(3, 0, 9);
        controller.setValue(3, 2, 5);        controller.setValue(3, 3, 8);
        controller.setValue(3, 4, 4);        controller.setValue(3, 6, 6);
        controller.setValue(4, 0, 4);        controller.setValue(4, 1, 1);
        controller.setValue(4, 3, 2);        controller.setValue(4, 6, 8);
        controller.setValue(4, 7, 9);        controller.setValue(4, 8, 3);
        controller.setValue(5, 0, 8);        controller.setValue(5, 3, 3);
        controller.setValue(5, 4, 6);        controller.setValue(5, 6, 5);
        controller.setValue(6, 0, 1);        controller.setValue(6, 1, 5);
        controller.setValue(6, 2, 3);        controller.setValue(6, 3, 6);
        controller.setValue(6, 5, 2);        controller.setValue(6, 7, 8);
        controller.setValue(7, 2, 8);        controller.setValue(7, 3, 5);
        controller.setValue(7, 4, 9);        controller.setValue(7, 5, 3);
        controller.setValue(7, 6, 2);        controller.setValue(7, 7, 1);
        controller.setValue(7, 8, 7);        controller.setValue(8, 1, 2);
        controller.setValue(8, 2, 9);        controller.setValue(8, 3, 4);
        controller.setValue(8, 5, 8);        controller.setValue(8, 7, 6);

        assertTrue(controller.isSolved());
        assertEquals(0, controller.getErrorList().size());
    }

    @Test
    public void solveTest2() {
        controller.setValue(0, 1, 8);        controller.setValue(0, 4, 2);
        controller.setValue(0, 5, 6);        controller.setValue(0, 6, 7);
        controller.setValue(0, 7, 3);        controller.setValue(0, 8, 4);
        controller.setValue(1, 1, 6);        controller.setValue(1, 2, 7);
        controller.setValue(1, 3, 1);        controller.setValue(1, 4, 3);
        controller.setValue(1, 8, 8);        controller.setValue(2, 2, 4);
        controller.setValue(2, 4, 8);        controller.setValue(2, 5, 5);
        controller.setValue(2, 6, 1);        controller.setValue(3, 0, 9);
        controller.setValue(3, 2, 5);        controller.setValue(3, 3, 8);
        controller.setValue(3, 4, 4);        controller.setValue(3, 6, 6);
        controller.setValue(4, 0, 4);        controller.setValue(4, 1, 1);
        controller.setValue(4, 3, 2);        controller.setValue(4, 6, 8);
        controller.setValue(4, 7, 9);        controller.setValue(4, 8, 3);
        controller.setValue(5, 0, 8);        controller.setValue(5, 3, 3);
        controller.setValue(5, 4, 6);        controller.setValue(5, 6, 5);
        controller.setValue(6, 0, 1);        controller.setValue(6, 1, 5);
        controller.setValue(6, 2, 3);        controller.setValue(6, 3, 1);
        controller.setValue(6, 5, 2);        controller.setValue(6, 7, 8);
        controller.setValue(7, 2, 8);        controller.setValue(7, 3, 5);
        controller.setValue(7, 4, 9);        controller.setValue(7, 5, 3);
        controller.setValue(7, 6, 2);        controller.setValue(7, 7, 1);
        controller.setValue(7, 8, 7);        controller.setValue(8, 1, 2);
        controller.setValue(8, 2, 9);        controller.setValue(8, 3, 4);
        controller.setValue(8, 5, 8);        controller.setValue(8, 7, 6);

        String result = "[List [Conflict [1 (1|3)] [1 (6|3)]], [Conflict [1 (6|0)] [1 (6|3)]], [Conflict [1 (6|3)] [1 (8|4)]]]";

        assertFalse(controller.isSolved());
        assertEquals(3, controller.getErrorList().size());
        assertEquals(result, controller.getErrorList().toString());
    }

    @Test
    public void solveTest3() {
        controller.setValue(0, 4, 2);        controller.setValue(0, 5, 6);
        controller.setValue(0, 6, 7);        controller.setValue(0, 7, 3);
        controller.setValue(0, 8, 4);        controller.setValue(1, 1, 6);
        controller.setValue(1, 2, 7);        controller.setValue(1, 3, 1);
        controller.setValue(1, 4, 3);        controller.setValue(1, 8, 8);
        controller.setValue(2, 2, 4);        controller.setValue(2, 4, 8);
        controller.setValue(2, 5, 5);        controller.setValue(2, 6, 1);
        controller.setValue(3, 0, 9);        controller.setValue(3, 2, 5);
        controller.setValue(3, 3, 8);        controller.setValue(3, 4, 4);
        controller.setValue(3, 6, 6);        controller.setValue(4, 0, 4);
        controller.setValue(4, 1, 1);        controller.setValue(4, 3, 2);
        controller.setValue(4, 6, 8);        controller.setValue(4, 7, 9);
        controller.setValue(4, 8, 3);        controller.setValue(5, 0, 8);
        controller.setValue(5, 3, 3);        controller.setValue(5, 4, 6);
        controller.setValue(5, 6, 5);        controller.setValue(6, 0, 1);
        controller.setValue(6, 1, 5);        controller.setValue(6, 2, 3);
        controller.setValue(6, 5, 2);        controller.setValue(6, 7, 8);
        controller.setValue(7, 2, 8);        controller.setValue(7, 3, 5);
        controller.setValue(7, 4, 9);        controller.setValue(7, 5, 3);
        controller.setValue(7, 6, 2);        controller.setValue(7, 7, 1);
        controller.setValue(7, 8, 7);        controller.setValue(8, 1, 2);
        controller.setValue(8, 2, 9);        controller.setValue(8, 3, 4);
        controller.setValue(8, 5, 8);        controller.setValue(8, 7, 6);

        assertFalse(controller.isSolved());
        assertEquals(0, controller.getErrorList().size());
    }

    @Test
    public void solveTest4() {
        controller.setValue(1, 2, 5);   // Produces 2 conflicts

        String result = "[List [Conflict [5 (0|0)] [5 (1|2)]], [Conflict [5 (1|2)] [5 (1|7)]]]";

        assertFalse(controller.isSolved());
        assertEquals(2, controller.getErrorList().size());
        assertEquals(result, controller.getErrorList().toString());
    }
}
