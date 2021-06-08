package org.secuso.privacyfriendlysudoku.controller;

import org.junit.Before;
import org.junit.Test;
import org.secuso.privacyfriendlysudoku.controller.helper.GameInfoContainer;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;

import static org.junit.Assert.*;

public class UndoRedoTest {
    GameController controller;

    @Before
    public void init() {
        controller = new GameController();
        controller.loadLevel(new GameInfoContainer(1, GameDifficulty.Moderate, GameType.Default_9x9,
                new int[] {
                        0, 0, 0, 0, 4, 2, 9, 0, 0,
                        0, 2, 0, 5, 7, 0, 0, 6, 0,
                        4, 0, 0, 3, 0, 0, 0, 0, 0,
                        8, 0, 0, 0, 0, 0, 6, 4, 0,
                        3, 1, 0, 0, 0, 0, 0, 8, 2,
                        0, 9, 4, 0, 0, 0, 0, 0, 1,
                        0, 0, 0, 0, 0, 7, 0, 0, 8,
                        0, 3, 0, 0, 2, 6, 0, 1, 0,
                        0, 0, 8, 4, 1, 0, 0, 0, 0
                }, null, null));
    }

    /**
     * Purpose: test undo and redo for setting values by selecting cell and value
     */
    @Test
    public void setValueTest() {
        // simple undo redo
        controller.selectCell(0, 0);
        controller.selectValue(1);
        controller.unDo();
        assertEquals(0, controller.getValue(0, 0));
        controller.reDo();
        assertEquals(1, controller.getValue(0, 0));

        // progress
        controller.selectCell(4, 4);
        controller.selectValue(7);
        controller.selectCell(2, 6);
        controller.selectValue(5);
        controller.selectValue(9);
        controller.selectCell(7, 2);
        controller.selectValue(2);
        controller.selectValue(3);
        controller.selectCell(4, 4);
        controller.selectValue(4);

        controller.unDo();
        assertEquals(7, controller.getValue(4, 4));
        controller.unDo();
        assertEquals(2, controller.getValue(7, 2));
        controller.unDo();
        assertEquals(0, controller.getValue(7, 2));
        controller.unDo();
        assertEquals(5, controller.getValue(2, 6));
        controller.unDo();
        assertEquals(0, controller.getValue(2, 6));
        controller.unDo();
        assertEquals(0, controller.getValue(4, 4));

        controller.reDo();
        assertEquals(7, controller.getValue(4, 4));
        controller.reDo();
        assertEquals(5, controller.getValue(2, 6));
        controller.reDo();
        assertEquals(9, controller.getValue(2, 6));
        controller.reDo();
        assertEquals(2, controller.getValue(7, 2));
        controller.reDo();
        assertEquals(3, controller.getValue(7, 2));
        controller.reDo();
        assertEquals(4, controller.getValue(4, 4));
    }
    /**
     * Purpose: test undo and redo for setting values by selecting value and cell
     */
    @Test
    public void setValueTest2() {
        controller.selectValue(1);
        controller.selectCell(4, 4);
        controller.unDo();
        assertEquals(0, controller.getValue(4, 4));
        controller.reDo();
        assertEquals(1, controller.getValue(4, 4));

        controller.selectCell(0, 0);
        controller.selectCell(2, 8);
        controller.selectCell(3, 2);
        controller.selectCell(6, 0);
        controller.selectValue(7);
        controller.selectCell(3, 2);
        controller.selectCell(7, 0);

        controller.unDo();
        assertEquals(0, controller.getValue(7, 0));
        controller.unDo();
        assertEquals(1, controller.getValue(3, 2));
        controller.unDo();
        assertEquals(0, controller.getValue(6, 0));
        controller.unDo();
        assertEquals(0, controller.getValue(3, 2));
        controller.unDo();
        assertEquals(0, controller.getValue(2, 8));
        controller.unDo();
        assertEquals(0, controller.getValue(0, 0));

        controller.reDo();
        assertEquals(1, controller.getValue(0, 0));
        controller.reDo();
        assertEquals(1, controller.getValue(2, 8));
        controller.reDo();
        assertEquals(1, controller.getValue(3, 2));
        controller.reDo();
        assertEquals(1, controller.getValue(6, 0));
        controller.reDo();
        assertEquals(7, controller.getValue(3, 2));
        controller.reDo();
        assertEquals(7, controller.getValue(7, 0));
    }

    /**
     * Purpose: test undo and redo for notes
     */
    @Test
    public void noteTest() {
        controller.setNoteStatus(true);
        controller.selectCell(0, 0);
        controller.selectValue(1);
        controller.selectValue(2);
        controller.selectValue(2);
        controller.selectValue(5);
        controller.selectCell(0, 0);
        controller.selectValue(8);
        controller.selectCell(1, 2);
        controller.selectCell(5, 4);

        boolean[] result = new boolean[] {
                false, false, false, false, false, false, false, false, false
        };
        controller.unDo();
        assertArrayEquals(result, controller.getNotes(5, 4));
        controller.unDo();
        assertArrayEquals(result, controller.getNotes(1, 2));
        controller.unDo();
        result[0] = true;
        assertArrayEquals(result, controller.getNotes(0, 0));
        controller.unDo();
        result[1] = true;
        assertArrayEquals(result, controller.getNotes(0, 0));
        controller.unDo();
        result[1] = false;
        assertArrayEquals(result, controller.getNotes(0, 0));
        controller.unDo();
        result[0] = false;
        assertArrayEquals(result, controller.getNotes(0, 0));

        controller.reDo();
        result[0] = true;
        assertArrayEquals(result, controller.getNotes(0, 0));
        controller.reDo();
        result[1] = true;
        assertArrayEquals(result, controller.getNotes(0, 0));
        controller.reDo();
        result[1] = false;
        assertArrayEquals(result, controller.getNotes(0, 0));
        controller.reDo();
        result[4] = true;
        assertArrayEquals(result, controller.getNotes(0, 0));
        controller.reDo();
        result[0] = false;
        result[4] = false;
        result[7] = true;
        assertArrayEquals(result, controller.getNotes(1, 2));
        controller.reDo();
        assertArrayEquals(result, controller.getNotes(5, 4));
    }

    /**
     * Purpose: test undo and redo for deleting and using hints
     */
    @Test
    public void totalTest() {
        controller.setNoteStatus(true);
        controller.selectCell(6, 7);
        controller.selectValue(2);
        controller.selectValue(3);
        controller.selectValue(9);
        controller.deleteSelectedCellsValue();

        controller.selectCell(8, 1);
        controller.hint();
        controller.setNoteStatus(false);
        controller.selectCell(6, 7);
        controller.selectValue(5);
        controller.deleteSelectedCellsValue();
        controller.selectCell(1, 8);
        controller.selectValue(2);
        controller.deleteSelectedCellsValue();

        boolean[] result = new boolean[] {
                false, true, true, false, false, false, false, false, true
        };
        controller.unDo();
        assertEquals(2, controller.getValue(1, 8));
        controller.unDo();
        controller.unDo();
        assertEquals(5, controller.getValue(6, 7));
        controller.unDo();
        assertEquals(0, controller.getValue(6, 7));
        controller.unDo();
        assertEquals(0, controller.getValue(8, 1));
        controller.unDo();
        assertArrayEquals(result, controller.getNotes(6, 7));

        controller.reDo();
        result[1] = false;
        result[2] = false;
        result[8] = false;
        assertArrayEquals(result, controller.getNotes(6, 7));
        controller.reDo();
        assertEquals(5, controller.getValue(8, 1));
        controller.reDo();
        assertEquals(5, controller.getValue(6, 7));
        controller.reDo();
        assertEquals(0, controller.getValue(6, 7));
        controller.reDo();
        assertEquals(2, controller.getValue(1, 8));
        controller.reDo();
        assertEquals(0, controller.getValue(1, 8));
    }
    /**
     * Purpose: test undo and redo after some undo and progress
     */
    @Test
    public void TotalTest2() {
        controller.setNoteStatus(true);
        controller.selectCell(1, 6);
        controller.selectValue(3);
        controller.selectValue(5);
        controller.selectValue(7);
        controller.selectCell(6, 6);
        controller.hint();
        controller.selectCell(6, 6);

        controller.selectValue(9);
        controller.selectCell(3, 2);
        controller.selectCell(6, 4);
        controller.selectValue(2);
        controller.setNoteStatus(false);
        controller.selectCell(5, 6);
        controller.selectCell(2, 2);

        controller.unDo();
        controller.unDo();
        controller.unDo();
        controller.selectCell(5, 5);
        controller.selectCell(3, 3);
        controller.selectValue(2);
        controller.selectCell(1, 6);
        controller.deleteSelectedCellsValue();

        boolean[] result = new boolean[] {
                false, false, true, false, true, false, true, false, false
        };
        controller.unDo();
        assertArrayEquals(result, controller.getNotes(1, 6));
        controller.unDo();
        assertEquals(0, controller.getValue(3, 3));
        controller.unDo();
        assertEquals(0, controller.getValue(5, 5));
        controller.unDo();
        result[2] = false;
        result[4] = false;
        result[6] = false;
        assertArrayEquals(result, controller.getNotes(3, 2));

        controller.reDo();
        result[8] = true;
        assertArrayEquals(result, controller.getNotes(3, 2));
        controller.reDo();
        assertEquals(2, controller.getValue(5, 5));
        controller.reDo();
        assertEquals(2, controller.getValue(3, 3));
        controller.reDo();
        result[8] = false;
        assertArrayEquals(result, controller.getNotes(1, 6));
    }
}
