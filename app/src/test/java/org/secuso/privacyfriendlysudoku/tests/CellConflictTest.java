package org.secuso.privacyfriendlysudoku.tests;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.secuso.privacyfriendlysudoku.controller.GameController;
import org.secuso.privacyfriendlysudoku.controller.helper.GameInfoContainer;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CellConflictTest {
    GameController controller;

    @Before
    public void init() {
        controller = new GameController();
        controller.loadLevel(new GameInfoContainer(3, GameDifficulty.Easy, GameType.Default_9x9,
                new int[]{0, 8, 0, 2, 0, 9, 6, 0, 0,
                        0, 0, 0, 0, 0, 0, 9, 0, 1,
                        1, 9, 6, 0, 8, 0, 0, 0, 0,
                        0, 0, 4, 6, 0, 2, 0, 5, 0,
                        0, 0, 0, 0, 9, 0, 0, 0, 0,
                        0, 2, 0, 1, 0, 5, 8, 0, 0,
                        0, 0, 0, 0, 3, 0, 5, 1, 4,
                        3, 0, 7, 0, 0, 0, 0, 0, 0,
                        0, 0, 1, 5, 0, 8, 0, 6, 0}
                , null, null));
    }

    /**
     * Purpose: Check if input value is conflict or not
     * Input: checkInputError (1, 3, 8) -> update errorList
     * Expected:
     *  conflict on section
     */
    @Test
    public void testCheckInputError1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        controller.setValue(1, 3, 8);

        Method method = controller.getClass().getDeclaredMethod("checkInputError", int.class, int.class);
        method.setAccessible(true);
        method.invoke(controller, 1, 3);

        String conflictResult = "[List [Conflict [8 (1|3)] [8 (2|4)]]]";
        assertEquals(1, controller.getErrorList().size());
        assertEquals(conflictResult, controller.getErrorList().toString());
    }

    /**
     * Purpose: Check if input value is conflict or not
     * Input: checkInputError (0, 8, 8) -> update errorList
     * Expected:
     *  conflict on horizontal line
     */
    @Test
    public void testCheckInputError2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        controller.setValue(0, 8, 8);

        Method method = controller.getClass().getDeclaredMethod("checkInputError", int.class, int.class);
        method.setAccessible(true);
        method.invoke(controller, 0, 8);

        String conflictResult = "[List [Conflict [8 (0|8)] [8 (0|1)]]]";
        assertEquals(1, controller.getErrorList().size());
        assertEquals(conflictResult, controller.getErrorList().toString());
    }

    /**
     * Purpose: Check if input value is conflict or not
     * Input: checkInputError (8, 4, 9) -> update errorList
     * Expected:
     *  conflict on vertical line
     */
    @Test
    public void testCheckInputError3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        controller.setValue(8, 4, 9);

        Method method = controller.getClass().getDeclaredMethod("checkInputError", int.class, int.class);
        method.setAccessible(true);
        method.invoke(controller, 8, 4);

        String conflictResult = "[List [Conflict [9 (8|4)] [9 (4|4)]]]";
        assertEquals(1, controller.getErrorList().size());
        assertEquals(conflictResult, controller.getErrorList().toString());
    }

    /**
     * Purpose: Check if input value is conflict or not
     * Input: checkInputError (0, 0, 8) -> update errorList
     * Expected:
     *  conflict on section and horizontal line
     */
    @Test
    public void testCheckInputError4() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        controller.setValue(0, 0, 8);

        Method method = controller.getClass().getDeclaredMethod("checkInputError", int.class, int.class);
        method.setAccessible(true);
        method.invoke(controller, 0, 0);

        String conflictResult = "[List [Conflict [8 (0|0)] [8 (0|1)]], [Conflict [8 (0|0)] [8 (0|1)]]]";
        assertEquals(2, controller.getErrorList().size());
        assertEquals(conflictResult, controller.getErrorList().toString());
    }

    /**
     * Purpose: Check if input value is conflict or not
     * Input: checkInputError (0, 8, 1) -> update errorList
     * Expected:
     *  conflict on section and vertical line
     */
    @Test
    public void testCheckInputError5() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        controller.setValue(0, 8, 1);

        Method method = controller.getClass().getDeclaredMethod("checkInputError", int.class, int.class);
        method.setAccessible(true);
        method.invoke(controller, 0, 8);

        String conflictResult = "[List [Conflict [1 (0|8)] [1 (1|8)]], [Conflict [1 (0|8)] [1 (1|8)]]]";
        assertEquals(2, controller.getErrorList().size());
        assertEquals(conflictResult, controller.getErrorList().toString());
    }

    /**
     * Purpose: Check if input value is conflict or not
     * Input: checkInputError (2, 3, 1) -> update errorList
     * Expected:
     *  conflict on horizontal and vertical line
     */
    @Test
    public void testCheckInputError6() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        controller.setValue(2, 3, 1);

        Method method = controller.getClass().getDeclaredMethod("checkInputError", int.class, int.class);
        method.setAccessible(true);
        method.invoke(controller, 2, 3);

        String conflictResult = "[List [Conflict [1 (2|3)] [1 (2|0)]], [Conflict [1 (2|3)] [1 (5|3)]]]";
        assertEquals(2, controller.getErrorList().size());
        assertEquals(conflictResult, controller.getErrorList().toString());
    }

    /**
     * Purpose: Check if input value is conflict or not
     * Input: checkInputError (6, 0, 3) -> update errorList
     * Expected:
     *  conflict on section and horizontal and vertical line
     */
    @Test
    public void testCheckInputError7() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        controller.setValue(6, 0, 3);

        Method method = controller.getClass().getDeclaredMethod("checkInputError", int.class, int.class);
        method.setAccessible(true);
        method.invoke(controller, 6, 0);

        String conflictResult = "[List [Conflict [3 (6|0)] [3 (6|4)]], [Conflict [3 (6|0)] [3 (7|0)]], [Conflict [3 (6|0)] [3 (7|0)]]]";
        assertEquals(3, controller.getErrorList().size());
        assertEquals(conflictResult, controller.getErrorList().toString());
    }
}
