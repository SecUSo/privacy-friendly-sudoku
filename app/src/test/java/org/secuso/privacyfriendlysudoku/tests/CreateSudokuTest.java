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

        String boardContent = presenter.getGameController().getCodeOfField();
        String inputUri = "https://sudoku.secuso.org/" + boardContent;
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

        String boardContent = presenter.getGameController().getCodeOfField();
        String inputUri = "https://" + boardContent;
        Pair<String, StringBuilder> importReult = (Pair<String, StringBuilder>) method.invoke(presenter, inputUri);
        String inputSudoku = importReult.first;

        assertNull(inputSudoku);
    }

    /**
     * Purpose: Check if input sudoku is valid or not
     * Input: isValidInputSudoku valid sudoku
     * Expected:
     *  return true
     */
    @Test
    public void testValidSudoku1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = presenter.getClass().getDeclaredMethod("isValidInputSudoku", String.class);
        method.setAccessible(true);

        String sudoku = presenter.getGameController().getCodeOfField();
        boolean result = (boolean)method.invoke(presenter, sudoku);
        assertTrue(result);
    }

    /**
     * Purpose: Check if input sudoku is valid or not
     * Input: isValidInputSudoku invalid size sudoku
     * Expected:
     *  return false
     */
    @Test
    public void testValidSudoku2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = presenter.getClass().getDeclaredMethod("isValidInputSudoku", String.class);
        method.setAccessible(true);

        String sudoku = "080209600000000901196080000004602050000090000020105";
        boolean result = (boolean)method.invoke(presenter, sudoku);
        assertFalse(result);
    }
}
