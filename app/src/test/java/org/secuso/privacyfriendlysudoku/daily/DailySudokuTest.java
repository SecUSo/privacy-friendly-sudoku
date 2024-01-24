package org.secuso.privacyfriendlysudoku.daily;

import org.junit.Before;
import org.junit.Test;

import org.secuso.privacyfriendlysudoku.controller.database.model.DailySudoku;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;
import org.secuso.privacyfriendlysudoku.ui.GameActivity;

import java.util.Calendar;

import static org.junit.Assert.*;

public class DailySudokuTest {
    DailySudoku dailySudoku;

    @Before
    public void init() {

        Calendar currentDate = Calendar.getInstance();
        int day = currentDate.get(Calendar.DAY_OF_MONTH);
        int month = currentDate.get(Calendar.MONTH);
        int year = currentDate.get(Calendar.YEAR);
        int id = day * 1000000 + (month + 1) * 10000 + year;

        dailySudoku = new DailySudoku(id,GameDifficulty.Easy, GameType.Default_9x9,
                new int[]{5, 0, 1, 9, 0, 0, 0, 0, 0,
                        2, 0, 0, 0, 0, 4, 9, 5, 0,
                        3, 9, 0, 7, 0, 0, 0, 2, 6,
                        0, 3, 0, 0, 0, 1, 0, 7, 2,
                        0, 0, 6, 0, 5, 7, 0, 0, 0,
                        0, 7, 2, 0, 0, 9, 0, 4, 1,
                        0, 0, 0, 0, 7, 0, 4, 0, 9,
                        6, 4, 0, 0, 0, 0, 0, 0, 0,
                        7, 0, 0, 0, 1, 0, 3, 0, 5},0,GameActivity.timeToString(0));

    }

//    @Test
//    public void getHintUsedTest() {
//        assertEquals(0,dailySudoku.getHintsUsed());
//    }

    /**
     * Purpose: Check setting HintUsed variable
     * Input : setHintsUsed 0->5
     * Expected : Success
     *            Equal dailySudoku's HintsUsed and 5
     */

    @Test
    public void setHintsUsedTest() {
        assertEquals(0,dailySudoku.getHintsUsed());
        dailySudoku.setHintsUsed(5);
        assertEquals(5,dailySudoku.getHintsUsed());
    }

//    @Test
//    public void getTimeNeededTest() { assertEquals("00:00:00",dailySudoku.getTimeNeeded());
//    }

    /**
     * Purpose: Check setting TimeNeeded variable
     * Input : setTimeNeeded "00:00:00" -> "00:00:10"
     * Expected : Success
     *            Equal dailySudoku's TimeNeeded and "00:00:10"
     */

    @Test
    public void setTimeNeededTest() {
        assertEquals("00:00:00",dailySudoku.getTimeNeeded());
        dailySudoku.setTimeNeeded(GameActivity.timeToString(10));
        assertEquals("00:00:10",dailySudoku.getTimeNeeded());
    }

//    @Test
//    public void getTimeNeededInSecondsTest() {
//        int second = dailySudoku.getTimeNeededInSeconds();
//        assertEquals(0,second);
//    }

    /**
     * Purpose: Check Today OrderingDataID value
     * Input : getOrderingDataID 20210508 (Today = 2021/06/08) (Month-1)
     * Expected : Success
     *            Equal dailySudoku's OrderingDataID and 20210508
     */

    @Test
    public void getOrderingDateIDTest() {
        int id = dailySudoku.getOrderingDateID();
        assertEquals(20210508,id);
    }
}
