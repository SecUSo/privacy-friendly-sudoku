package org.secuso.privacyfriendlysudoku.game.solver;

import org.junit.Test;
import org.secuso.privacyfriendlysudoku.controller.qqwing.QQWing;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;

import static org.junit.Assert.assertTrue;

/**
 * Testing QQWing Class.
 */
public class QQWingTest {

    /**
     * Purpose: When putting a puzzle with single solution in QQWing class,
     *          test hasUniqueSolution().
     * Input: Input puzzle with single solution.
     * Expected: hasUniqueSolution() outputs True.
     */
    @Test
    public void testHasUniqueSolutionSingle() {
        int[] singleSolution = new int[]{0,0,0,0,4,1,0,0,0,
                                         0,6,0,0,0,0,2,0,0,
                                         0,0,0,0,0,0,0,0,0,
                                         3,2,0,6,0,0,0,0,0,
                                         0,0,0,0,5,0,0,4,1,
                                         7,0,0,0,0,0,0,0,0,
                                         0,0,0,2,0,0,3,0,0,
                                         0,4,8,0,0,0,0,0,0,
                                         5,0,1,0,0,0,0,0,0};

        QQWing verifier = new QQWing(GameType.Default_9x9, GameDifficulty.Easy);
        verifier.setRecordHistory(true);
        verifier.setPuzzle(singleSolution);
        verifier.solve();

        assertTrue(verifier.hasUniqueSolution());
    }
}
