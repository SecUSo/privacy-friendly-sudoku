/*
 This file is part of Privacy Friendly Sudoku.

 Privacy Friendly Sudoku is free software:
 you can redistribute it and/or modify it under the terms of the
 GNU General Public License as published by the Free Software Foundation,
 either version 3 of the License, or any later version.

 Privacy Friendly Sudoku is distributed in the hope
 that it will be useful, but WITHOUT ANY WARRANTY; without even
 the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Privacy Friendly Sudoku. If not, see <http://www.gnu.org/licenses/>.
 */
package org.secuso.privacyfriendlysudoku.controller;

import org.junit.Before;
import org.junit.Test;

import org.secuso.privacyfriendlysudoku.controller.helper.GameInfoContainer;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;

import static org.junit.Assert.*;

/**
 * Created by Chris on 10.11.2015.
 */
public class GameControllerTest {

    GameController controller;
    GameController controller2;

    @Before
    public void init() {

        controller = new GameController();
        controller.loadLevel(new GameInfoContainer(3, GameDifficulty.Easy, GameType.Default_9x9,
                new int[]{5, 0, 1, 9, 0, 0, 0, 0, 0,
                        2, 0, 0, 0, 0, 4, 9, 5, 0,
                        3, 9, 0, 7, 0, 0, 0, 2, 6,
                        0, 3, 0, 0, 0, 1, 0, 7, 2,
                        0, 0, 6, 0, 5, 7, 0, 0, 0,
                        0, 7, 2, 0, 0, 9, 0, 4, 1,
                        0, 0, 0, 0, 7, 0, 4, 0, 9,
                        6, 4, 0, 0, 0, 0, 0, 0, 0,
                        7, 0, 0, 0, 1, 0, 3, 0, 5}
                , null, null));


        controller2 = new GameController();
        //controller2 = GameController.getInstance();
        controller2.loadLevel(new GameInfoContainer(2, GameDifficulty.Easy, GameType.Default_12x12,
                new int[]{0, 2, 1, 0, 0, 6, 0, 0, 0, 8, 9, 0,
                        10, 0, 12, 0, 0, 2, 1, 11, 0, 0, 0, 6,
                        6, 0, 0, 4, 0, 12, 0, 0, 0, 0, 2, 1,
                        0, 0, 0, 5, 0, 0, 0, 4, 11, 10, 0, 0,
                        0, 10, 0, 1, 0, 0, 6, 0, 0, 0, 0, 0,
                        0, 7, 0, 0, 11, 0, 0, 0, 0, 12, 8, 9,
                        2, 1, 11, 0, 0, 0, 0, 7, 0, 0, 6, 0,
                        0, 0, 0, 0, 0, 5, 0, 0, 4, 0, 10, 0,
                        0, 0, 7, 3, 9, 0, 0, 0, 1, 0, 0, 0,
                        1, 5, 0, 0, 0, 0, 4, 0, 10, 0, 0, 11,
                        9, 0, 0, 0, 1, 10, 2, 0, 0, 6, 0, 7,
                        0, 6, 10, 0, 0, 0, 8, 0, 0, 1, 12, 0}
                , null, null));

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

    @Test
    public void deleteTest() {
        controller.setValue(1, 2, 5);
        assertEquals(5, controller.getValue(1, 2));
        controller.deleteValue(1, 2);
        assertEquals(0, controller.getValue(1, 2));
    }

    @Test
    public void createNoteTest() {
        controller.setNote(1, 2, 5);
        controller.setNote(1, 2, 9);

        boolean[] result = {false, false, false, false, true, false, false, false, true};

        assertArrayEquals(result, controller.getNotes(1, 2));
    }

    @Test
    public void deleteNoteTest() {
        controller.setNote(1, 2, 5);
        controller.setNote(1, 2, 9);
        controller.deleteNote(1, 2, 5);

        boolean[] result = {false, false, false, false, false, false, false, false, true};

        assertArrayEquals(result, controller.getNotes(1, 2));
    }

    @Test
    public void toggleNoteTest() {
        controller.toggleNote(1,2,5);
        controller.toggleNote(1, 2, 9);
        controller.toggleNote(1, 2, 5);
        controller.toggleNote(1, 2, 4);

        boolean[] result = {false, false, false, true, false, false, false, false, true};

        assertArrayEquals(result, controller.getNotes(1, 2));
    }

    @Test
    public void selectCellTest() {

        controller.selectCell(0, 1);
        assertEquals(1, controller.getSelectedCol());
        assertEquals(0, controller.getSelectedRow());
    }

    /**
     * Purpose: use hints for valid and invalid cells
     * Input: (row, column)
     * (0, 6), (2, 3)(fixed cell), (8, 5), (4, 1),
     * (7, 3)(filled with 4 already), no cell selected
     * Expected: (cell value, used hints)
     * (7, 1), (, 1), (8, 2), (1, 3), (5, 4), (, 4)
     */
    @Test
    public void hintTest() {
        controller.selectCell(0, 6);
        controller.hint();
        assertEquals(7, controller.getValue(0, 6));
        assertEquals(1, controller.getUsedHints());

        controller.selectCell(2, 3);
        controller.hint();
        assertEquals(1, controller.getUsedHints());

        controller.selectCell(8, 5);
        controller.hint();
        assertEquals(8, controller.getValue(8, 5));
        assertEquals(2, controller.getUsedHints());

        controller.selectCell(4, 1);
        controller.hint();
        assertEquals(1, controller.getValue(4, 1));
        assertEquals(3, controller.getUsedHints());

        controller.selectCell(7, 3);
        controller.selectValue(4);
        controller.hint();
        assertEquals(5, controller.getValue(7, 3));
        assertEquals(4, controller.getUsedHints());

        controller.selectCell(7, 3);
        controller.hint();
        assertEquals(4, controller.getUsedHints());
    }
}
