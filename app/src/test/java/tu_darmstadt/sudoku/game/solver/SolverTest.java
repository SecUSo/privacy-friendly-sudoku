package tu_darmstadt.sudoku.game.solver;

import org.junit.Before;
import org.junit.Test;

import tu_darmstadt.sudoku.controller.GameController;
import tu_darmstadt.sudoku.game.GameType;

/**
 * Created by Chris on 12.11.2015.
 */
public class SolverTest {

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

        controller.loadLevel(GameType.Default_9x9,
                new int[]{0,0,0,0,4,1,0,0,0,0,6,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,3,2,0,6,0,0,0,0,0,0,0,0,0,5,0,0,4,1,7,0,0,0,0,0,0,0,0,0,0,0,2,0,0,3,0,0,0,4,8,0,0,0,0,0,0,5,0,1,0,0,0,0,0,0},
                null,
                null);
    }

    //000041000060000200000000000320600000000050041700000000000200300048000000501000000

    //501900000200004950390700026030001072006057000072009041000070409640000000700010305

    @Test
    public void solveTest() {

    }
}
