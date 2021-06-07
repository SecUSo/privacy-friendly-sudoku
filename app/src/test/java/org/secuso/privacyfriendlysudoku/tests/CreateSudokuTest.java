package org.secuso.privacyfriendlysudoku.tests;

import androidx.core.util.Pair;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.secuso.privacyfriendlysudoku.controller.GameController;
import org.secuso.privacyfriendlysudoku.controller.helper.GameInfoContainer;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;
import org.secuso.privacyfriendlysudoku.ui.presenter.CreateSudokuPresenter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class CreateSudokuTest {
    CreateSudokuPresenter presenter;

    @Before
    public void init() {
        presenter = new CreateSudokuPresenter();
        GameController controller = new GameController();
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

        presenter.setGameController(controller);
    }

    /**
     * Purpose: Check if input uri is valid or not
     * Input: checkUri valid inputUri -> (inputSudoku, errorMessage)
     * Expected:
     *  inputSudoku != null
     */
    @Test
    public void testValidUri1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = presenter.getClass().getDeclaredMethod("checkUri", String.class);
        method.setAccessible(true);

        String inputUri = "https://sudoku.secuso.org/080209600000000901196080000004602050000090000020105800000030514307000000001508060";
        Pair<String, StringBuilder> importReult = (Pair<String, StringBuilder>) method.invoke(presenter, inputUri);
        String inputSudoku = importReult.first;

        assertNotNull(inputSudoku);
    }

    /**
     * Purpose: Check if input uri is valid or not
     * Input: checkUri invalid inputUri -> (inputSudoku, errorMessage)
     * Expected:
     *  inputSudoku == null
     */
    @Test
    public void testValidUri2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = presenter.getClass().getDeclaredMethod("checkUri", String.class);
        method.setAccessible(true);

        String inputUri = "https://080209600000000901196080000004602050000090000020105800000030514307000000001508060";
        Pair<String, StringBuilder> importReult = (Pair<String, StringBuilder>) method.invoke(presenter, inputUri);
        String inputSudoku = importReult.first;

        assertNull(inputSudoku);
    }
}
