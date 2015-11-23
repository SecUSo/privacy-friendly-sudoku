package tu_darmstadt.sudoku.game.solver;

import android.util.Log;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import tu_darmstadt.sudoku.controller.GameController;
import tu_darmstadt.sudoku.controller.helper.GameInfoContainer;
import tu_darmstadt.sudoku.game.GameBoard;
import tu_darmstadt.sudoku.game.GameDifficulty;
import tu_darmstadt.sudoku.game.GameType;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Chris on 12.11.2015.
 */
public class SolverTest {

    GameController controller;

    @Before
    public void init() {
        controller = new GameController();
        controller.loadLevel(new GameInfoContainer(0, GameDifficulty.Easy  ,GameType.Default_9x9,
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

        /*controller.loadLevel(new GameInfoContainer(3, GameType.Default_9x9,
                new int[]{0,0,0,0,4,1,0,0,0,0,6,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,3,2,0,6,0,0,0,0,0,0,0,0,0,5,0,0,4,1,7,0,0,0,0,0,0,0,0,0,0,0,2,0,0,3,0,0,0,4,8,0,0,0,0,0,0,5,0,1,0,0,0,0,0,0}
                , null, null));*/
    }

    @Test
    public void solveSingleSolution1() {
        int[] result = controller.solve();

        /*for(GameBoard gb : result) {
            assertEquals("[GameBoard: \n" +
                            "\t[5 (0|0)] [8 (0|1)] [1 (0|2)] \t[9 (0|3)] [2 (0|4)] [6 (0|5)] \t[7 (0|6)] [3 (0|7)] [4 (0|8)] ]" +
                            "\t[2 (1|0)] [6 (1|1)] [7 (1|2)] \t[1 (1|3)] [3 (1|4)] [4 (1|5)] \t[9 (1|6)] [5 (1|7)] [8 (1|8)] ]" +
                            "\t[3 (2|0)] [9 (2|1)] [4 (2|2)] \t[7 (2|3)] [8 (2|4)] [5 (2|5)] \t[1 (2|6)] [2 (2|7)] [6 (2|8)] ]" +
                            "\t[9 (3|0)] [3 (3|1)] [5 (3|2)] \t[8 (3|3)] [4 (3|4)] [1 (3|5)] \t[6 (3|6)] [7 (3|7)] [2 (3|8)] ]" +
                            "\t[4 (4|0)] [1 (4|1)] [6 (4|2)] \t[2 (4|3)] [5 (4|4)] [7 (4|5)] \t[8 (4|6)] [9 (4|7)] [3 (4|8)] ]" +
                            "\t[8 (5|0)] [7 (5|1)] [2 (5|2)] \t[3 (5|3)] [6 (5|4)] [9 (5|5)] \t[5 (5|6)] [4 (5|7)] [1 (5|8)] ]" +
                            "\t[1 (6|0)] [5 (6|1)] [3 (6|2)] \t[6 (6|3)] [7 (6|4)] [2 (6|5)] \t[4 (6|6)] [8 (6|7)] [9 (6|8)] ]" +
                            "\t[6 (7|0)] [4 (7|1)] [8 (7|2)] \t[5 (7|3)] [9 (7|4)] [3 (7|5)] \t[2 (7|6)] [1 (7|7)] [7 (7|8)] ]" +
                            "\t[7 (8|0)] [2 (8|1)] [9 (8|2)] \t[4 (8|3)] [1 (8|4)] [8 (8|5)] \t[3 (8|6)] [6 (8|7)] [5 (8|8)] ]",
                    gb.toString());
        }*/
    }

    @Test
    public void solveSingleSolution2() {
        controller.loadLevel(new GameInfoContainer(0, GameDifficulty.Easy, GameType.Default_9x9,
                new int[]{0,0,0,0,4,1,0,0,0,0,6,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,3,2,0,6,0,0,0,0,0,0,0,0,0,5,0,0,4,1,7,0,0,0,0,0,0,0,0,0,0,0,2,0,0,3,0,0,0,4,8,0,0,0,0,0,0,5,0,1,0,0,0,0,0,0}
                , null, null));

        int[] result = controller.solve();

        /*for(GameBoard gb : result) {
            assertEquals("[GameBoard: \n" +
                            "\t[8 (0|0)] [7 (0|1)] [2 (0|2)] \t[9 (0|3)] [4 (0|4)] [1 (0|5)] \t[5 (0|6)] [6 (0|7)] [3 (0|8)] ]"+
                            "\t[1 (1|0)] [6 (1|1)] [9 (1|2)] \t[5 (1|3)] [7 (1|4)] [3 (1|5)] \t[2 (1|6)] [8 (1|7)] [4 (1|8)] ]"+
                            "\t[4 (2|0)] [5 (2|1)] [3 (2|2)] \t[8 (2|3)] [2 (2|4)] [6 (2|5)] \t[1 (2|6)] [9 (2|7)] [7 (2|8)] ]" +
                            "\t[3 (3|0)] [2 (3|1)] [4 (3|2)] \t[6 (3|3)] [1 (3|4)] [7 (3|5)] \t[8 (3|6)] [5 (3|7)] [9 (3|8)] ]" +
                            "\t[9 (4|0)] [8 (4|1)] [6 (4|2)] \t[3 (4|3)] [5 (4|4)] [2 (4|5)] \t[7 (4|6)] [4 (4|7)] [1 (4|8)] ]" +
                            "\t[7 (5|0)] [1 (5|1)] [5 (5|2)] \t[4 (5|3)] [9 (5|4)] [8 (5|5)] \t[6 (5|6)] [3 (5|7)] [2 (5|8)] ]" +
                            "\t[6 (6|0)] [9 (6|1)] [7 (6|2)] \t[2 (6|3)] [8 (6|4)] [4 (6|5)] \t[3 (6|6)] [1 (6|7)] [5 (6|8)] ]" +
                            "\t[2 (7|0)] [4 (7|1)] [8 (7|2)] \t[1 (7|3)] [3 (7|4)] [5 (7|5)] \t[9 (7|6)] [7 (7|7)] [6 (7|8)] ]" +
                            "\t[5 (8|0)] [3 (8|1)] [1 (8|2)] \t[7 (8|3)] [6 (8|4)] [9 (8|5)] \t[4 (8|6)] [2 (8|7)] [8 (8|8)] ]",
                    gb.toString());
        }*/
    }

    @Test
    public void solveMultipleSolutions1() {
        controller.loadLevel(new GameInfoContainer(0, GameDifficulty.Easy, GameType.Default_6x6,
                new int[]{1,0,0,0,0,6,
                        4,0,6,1,0,0,
                        0,0,2,3,0,5,
                        0,4,0,0,1,0,
                        0,6,0,2,0,0,
                        0,3,0,5,0,1}, null,null));

        int[] result = controller.solve();

        /*assertEquals(2, result.size());

        for(GameBoard gb : result) {
            assertEquals("[GameBoard: \n" +
                            "\t[8 (0|0)] [7 (0|1)] [2 (0|2)] \t[9 (0|3)] [4 (0|4)] [1 (0|5)] \t[5 (0|6)] [6 (0|7)] [3 (0|8)] ]"+
                            "\t[1 (1|0)] [6 (1|1)] [9 (1|2)] \t[5 (1|3)] [7 (1|4)] [3 (1|5)] \t[2 (1|6)] [8 (1|7)] [4 (1|8)] ]"+
                            "\t[4 (2|0)] [5 (2|1)] [3 (2|2)] \t[8 (2|3)] [2 (2|4)] [6 (2|5)] \t[1 (2|6)] [9 (2|7)] [7 (2|8)] ]" +
                            "\t[3 (3|0)] [2 (3|1)] [4 (3|2)] \t[6 (3|3)] [1 (3|4)] [7 (3|5)] \t[8 (3|6)] [5 (3|7)] [9 (3|8)] ]" +
                            "\t[9 (4|0)] [8 (4|1)] [6 (4|2)] \t[3 (4|3)] [5 (4|4)] [2 (4|5)] \t[7 (4|6)] [4 (4|7)] [1 (4|8)] ]" +
                            "\t[7 (5|0)] [1 (5|1)] [5 (5|2)] \t[4 (5|3)] [9 (5|4)] [8 (5|5)] \t[6 (5|6)] [3 (5|7)] [2 (5|8)] ]" +
                            "\t[6 (6|0)] [9 (6|1)] [7 (6|2)] \t[2 (6|3)] [8 (6|4)] [4 (6|5)] \t[3 (6|6)] [1 (6|7)] [5 (6|8)] ]" +
                            "\t[2 (7|0)] [4 (7|1)] [8 (7|2)] \t[1 (7|3)] [3 (7|4)] [5 (7|5)] \t[9 (7|6)] [7 (7|7)] [6 (7|8)] ]" +
                            "\t[5 (8|0)] [3 (8|1)] [1 (8|2)] \t[7 (8|3)] [6 (8|4)] [9 (8|5)] \t[4 (8|6)] [2 (8|7)] [8 (8|8)] ]",
                    gb.toString());
        }*/
    }

    @Test
    public void solveNotSolvableTest() {
        controller.loadLevel(new GameInfoContainer(0, GameDifficulty.Easy, GameType.Default_6x6,
                new int[]{1,2,0,0,0,6,
                          4,0,6,1,3,0,
                          0,0,2,3,0,5,
                          0,4,0,0,1,0,
                          0,5,0,2,0,0,
                          0,3,0,5,0,1}, null,null));

       int[] result = controller.solve();

        //assertEquals(0, result.size());
    }

}
