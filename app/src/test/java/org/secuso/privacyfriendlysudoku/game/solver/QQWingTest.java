package org.secuso.privacyfriendlysudoku.game.solver;

import org.junit.Test;
import org.secuso.privacyfriendlysudoku.controller.qqwing.QQWing;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

    /**
     * Purpose: When putting a puzzle with multiple solutions in QQWing class,
     *          test hasUniqueSolution().
     * Input: Input puzzle with multiple solutions.
     * Expected: hasUniqueSolution() outputs False.
     */
    @Test
    public void testHasUniqueSolutionMulti() {
        int[] multiSolution = { 1,0,0,0,0,6,
                4,0,6,1,0,0,
                0,0,2,3,0,5,
                0,4,0,0,1,0,
                0,6,0,2,0,0,
                0,3,0,5,0,1};

        QQWing verifier = new QQWing(GameType.Default_6x6, GameDifficulty.Easy);
        verifier.setRecordHistory(true);
        verifier.setPuzzle(multiSolution);
        verifier.solve();

        assertFalse(verifier.hasUniqueSolution());
    }

    /**
     * Purpose: When putting a puzzle with none solution in QQWing class,
     *          test hasUniqueSolution().
     * Input: Input puzzle with none solution.
     * Expected: hasUniqueSolution() outputs False.
     */
    @Test
    public void testHasUniqueSolutionNone() {
        int[] noSolution = {1,2,0,0,0,6,
                4,0,6,1,3,0,
                0,0,2,3,0,5,
                0,4,0,0,1,0,
                0,5,0,2,0,0,
                0,3,0,5,0,1};

        QQWing verifier = new QQWing(GameType.Default_6x6, GameDifficulty.Easy);
        verifier.setRecordHistory(true);
        verifier.setPuzzle(noSolution);
        verifier.solve();

        assertFalse(verifier.hasUniqueSolution());
    }

    /**
     * Purpose: When putting a puzzle with the solution shorter than GameType
     *          in QQWing class, test whether an exception is raised
     *          in hasUniqueSolution().
     * Input: Input puzzle with the solution shorter than GameType.
     * Expected: ArrayIndexOutOfBoundsException is raised.
     */
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testHasUniqueSolutionTooShort() {
        int[] tooShort = {  1,2,0,
                            4,0,6,
                            0,0,2};

        QQWing verifier = new QQWing(GameType.Default_6x6, GameDifficulty.Easy);
        verifier.setRecordHistory(true);
        verifier.setPuzzle(tooShort);
        verifier.solve();

        assertFalse(verifier.hasUniqueSolution());
    }

    /**
     * Purpose: When putting a puzzle with the solution longer than GameType
     *          in QQWing class, test hasUniqueSolution().
     * Input: Input puzzle with the solution longer than GameType.
     * Expected: hasUniqueSolution() outputs False.
     */
    @Test
    public void testHasUniqueSolutionTooLong() {
        int[] tooLong ={0,0,0,0,4,1,0,0,0,
                        0,6,0,0,0,0,2,0,0,
                        0,0,0,0,0,0,0,0,0,
                        3,2,0,6,0,0,0,0,0,
                        0,0,0,0,5,0,0,4,1,
                        7,0,0,0,0,0,0,0,0,
                        0,0,0,2,0,0,3,0,0,
                        0,4,8,0,0,0,0,0,0,
                        5,0,1,0,0,0,0,0,0};

        QQWing verifier = new QQWing(GameType.Default_6x6, GameDifficulty.Easy);
        verifier.setRecordHistory(true);
        verifier.setPuzzle(tooLong);
        verifier.solve();

        assertFalse(verifier.hasUniqueSolution());
    }

    /**
     * Purpose: When putting null puzzle in QQWing Class,
     *          test hasUniqueSolution().
     * Input: Input null puzzle.
     * Expected: hasUniqueSolution() outputs False.
     */
    @Test
    public void testHasUniqueSolutionNull() {
        int[] nullSolution = null;

        QQWing verifier = new QQWing(GameType.Default_6x6, GameDifficulty.Easy);
        verifier.setRecordHistory(true);
        verifier.setPuzzle(nullSolution);
        verifier.solve();

        assertFalse(verifier.hasUniqueSolution());
    }

    /**
     * Purpose: Test getDifficulty when GameDifficulty is Easy.
     * Input: Input puzzle whose GameDifficulty is Easy.
     * Expected: GameDifficulty.Easy
     */
    @Test
    public void testGetDifficultyEasy() {
        int[] puzzle = {0, 0, 3, 0, 0, 0,
                        0, 5, 1, 3, 0, 4,
                        0, 0, 0, 0, 0, 1,
                        0, 0, 0, 0, 0, 5,
                        0, 0, 4, 0, 0, 0,
                        0, 3, 6, 5, 0, 0};

        QQWing qqwing = new QQWing(GameType.Default_6x6, GameDifficulty.Unspecified);
        qqwing.setRecordHistory(true);
        qqwing.setPuzzle(puzzle);
        qqwing.solve();

        assertEquals(GameDifficulty.Easy, qqwing.getDifficulty());
    }
}
