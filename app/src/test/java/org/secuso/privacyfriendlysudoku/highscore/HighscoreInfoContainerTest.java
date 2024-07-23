package org.secuso.privacyfriendlysudoku.highscore;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.secuso.privacyfriendlysudoku.controller.GameController;
import org.secuso.privacyfriendlysudoku.controller.helper.GameInfoContainer;
import org.secuso.privacyfriendlysudoku.controller.helper.HighscoreInfoContainer;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;

import java.io.File;

import static org.junit.Assert.*;

public class HighscoreInfoContainerTest {

    GameController controller;
    HighscoreInfoContainer highscoreInfoContainer1;
    HighscoreInfoContainer highscoreInfoContainer2;
    HighscoreInfoContainer highscoreInfoContainer3;

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

        highscoreInfoContainer1 = new HighscoreInfoContainer(GameType.Default_9x9,GameDifficulty.Easy);
        highscoreInfoContainer2 = new HighscoreInfoContainer();
        highscoreInfoContainer3 = new HighscoreInfoContainer(GameType.Default_6x6,GameDifficulty.Moderate);
    }

    /**
     * Purpose: Check adding gameController about highscoreInfoContainer1 - 9x9, Easy
     * Input : add No -> GameController with 9x9, Easy
     * Expected : Success
     *            Equal controller information and highscoreInfoContainer1 information
     */

    @Test
    public void addTest1() {
        assertEquals(GameDifficulty.Easy,highscoreInfoContainer1.getDifficulty());
        assertEquals(GameType.Default_9x9,highscoreInfoContainer1.getGameType());
        assertEquals(0,highscoreInfoContainer1.getNumberOfGames());
        assertEquals(Integer.MAX_VALUE,highscoreInfoContainer1.getMinTime());
        assertEquals(0,highscoreInfoContainer1.getNumberOfGamesNoHints());
        assertEquals(0,highscoreInfoContainer1.getTimeNoHints());

        highscoreInfoContainer1.add(controller);

        assertEquals(GameDifficulty.Easy,highscoreInfoContainer1.getDifficulty());
        assertEquals(GameType.Default_9x9,highscoreInfoContainer1.getGameType());
        assertEquals(1,highscoreInfoContainer1.getNumberOfGames());

        if(controller.getUsedHints()==0){
            assertEquals(controller.getTime(),highscoreInfoContainer1.getMinTime());
            assertEquals(1,highscoreInfoContainer1.getNumberOfGamesNoHints());
            assertEquals(controller.getTime(),highscoreInfoContainer1.getTimeNoHints());
        }
        else{
            assertEquals(Integer.MAX_VALUE,highscoreInfoContainer1.getMinTime());
            assertEquals(0,highscoreInfoContainer1.getNumberOfGamesNoHints());
            assertEquals(0,highscoreInfoContainer1.getTimeNoHints());
        }
    }

    /**
     * Purpose: Check adding gameController about highscoreInfoContainer2
     * Input : add No -> GameController with 9x9, Easy
     * Expected : Success
     *            Equal controller information and highscoreInfoContainer2 information
     */

    @Test
    public void addTest2() {
        assertEquals(null,highscoreInfoContainer2.getDifficulty());
        assertEquals(null,highscoreInfoContainer2.getGameType());
        assertEquals(0,highscoreInfoContainer2.getNumberOfGames());
        assertEquals(Integer.MAX_VALUE,highscoreInfoContainer2.getMinTime());
        assertEquals(0,highscoreInfoContainer2.getNumberOfGamesNoHints());
        assertEquals(0,highscoreInfoContainer2.getTimeNoHints());

        highscoreInfoContainer2.add(controller);

        assertEquals(GameDifficulty.Easy,highscoreInfoContainer2.getDifficulty());
        assertEquals(GameType.Default_9x9,highscoreInfoContainer2.getGameType());
        assertEquals(1,highscoreInfoContainer2.getNumberOfGames());

        if(controller.getUsedHints()==0){
            assertEquals(controller.getTime(),highscoreInfoContainer2.getMinTime());
            assertEquals(1,highscoreInfoContainer2.getNumberOfGamesNoHints());
            assertEquals(controller.getTime(),highscoreInfoContainer2.getTimeNoHints());
        }
        else{
            assertEquals(Integer.MAX_VALUE,highscoreInfoContainer2.getMinTime());
            assertEquals(0,highscoreInfoContainer2.getNumberOfGamesNoHints());
            assertEquals(0,highscoreInfoContainer2.getTimeNoHints());
        }
    }

    /**
     * Purpose: Check adding gameController about highscoreInfoContainer3 - 6x6, Moderate
     * Input : add No -> GameController with 9x9, Easy
     * Expected : Success
     *            Not Equal controller information and highscoreInfoContainer3 information
     *            about GameDifficulty, GameType
     *            And Equal about Remainder
     */

    @Test
    public void addTest3() {
        assertEquals(GameDifficulty.Moderate,highscoreInfoContainer3.getDifficulty());
        assertEquals(GameType.Default_6x6,highscoreInfoContainer3.getGameType());
        assertEquals(0,highscoreInfoContainer3.getNumberOfGames());
        assertEquals(Integer.MAX_VALUE,highscoreInfoContainer3.getMinTime());
        assertEquals(0,highscoreInfoContainer3.getNumberOfGamesNoHints());
        assertEquals(0,highscoreInfoContainer3.getTimeNoHints());

        highscoreInfoContainer3.add(controller);

        assertEquals(GameDifficulty.Moderate,highscoreInfoContainer3.getDifficulty());
        assertEquals(GameType.Default_6x6,highscoreInfoContainer3.getGameType());
        assertEquals(1,highscoreInfoContainer3.getNumberOfGames());

        if(controller.getUsedHints()==0){
            assertEquals(controller.getTime(),highscoreInfoContainer3.getMinTime());
            assertEquals(1,highscoreInfoContainer3.getNumberOfGamesNoHints());
            assertEquals(controller.getTime(),highscoreInfoContainer3.getTimeNoHints());
        }
        else{
            assertEquals(Integer.MAX_VALUE,highscoreInfoContainer3.getMinTime());
            assertEquals(0,highscoreInfoContainer3.getNumberOfGamesNoHints());
            assertEquals(0,highscoreInfoContainer3.getTimeNoHints());
        }
    }

    /**
     * Purpose: Check Increasing Hints
     * Input : incHints 0 -> +1 -> 1
     * Expected : Success
     *            Equal 1 and highscoreInfoContainer1's HintsUsed
     */

    @Test
    public void incHintsTest(){
        assertEquals(0,highscoreInfoContainer1.getNumberOfHintsUsed());
        highscoreInfoContainer1.incHints();
        assertEquals(1,highscoreInfoContainer1.getNumberOfHintsUsed());
    }

    /**
     * Purpose: Check Increasing Time
     * Input : incTime 0 -> +1 -> 1
     * Expected : Success
     *            Equal 1 and highscoreInfoContainer1's Time
     */

    @Test
    public void incTimeTest() {
        assertEquals(0,highscoreInfoContainer1.getTime());
        highscoreInfoContainer1.incTime();
        assertEquals(1,highscoreInfoContainer1.getTime());
    }

    /**
     * Purpose: Check setting HighscoreInfoContatiner Object from File
     * Input : setInfosFromFile highscoreInfoContainer1 -> highscoreInfoContatiner1
     * Expected : Success
     *            Equal Before and After
     */

    @Test
    public void setInfosFromFileTest1() {
        assertEquals(null,highscoreInfoContainer2.getDifficulty());
        assertEquals(null,highscoreInfoContainer2.getGameType());
        assertEquals(0,highscoreInfoContainer2.getNumberOfGames());
        assertEquals(Integer.MAX_VALUE,highscoreInfoContainer2.getMinTime());
        assertEquals(0,highscoreInfoContainer2.getNumberOfGamesNoHints());
        assertEquals(0,highscoreInfoContainer2.getTimeNoHints());
        assertEquals(0,highscoreInfoContainer2.getNumberOfHintsUsed());
        assertEquals(0,highscoreInfoContainer2.getTime());
        String s = highscoreInfoContainer1.getActualStats();
        highscoreInfoContainer2.setInfosFromFile(s);
        assertEquals(GameDifficulty.Easy,highscoreInfoContainer2.getDifficulty());
        assertEquals(GameType.Default_9x9,highscoreInfoContainer2.getGameType());
        assertEquals(0,highscoreInfoContainer2.getNumberOfGames());
        assertEquals(Integer.MAX_VALUE,highscoreInfoContainer2.getMinTime());
        assertEquals(0,highscoreInfoContainer2.getNumberOfGamesNoHints());
        assertEquals(0,highscoreInfoContainer2.getTimeNoHints());
        assertEquals(0,highscoreInfoContainer2.getNumberOfHintsUsed());
        assertEquals(0,highscoreInfoContainer2.getTime());
    }

    /**
     * Purpose: Check setting HighscoreInfoContatiner Object from File
     * Input : setInfosFromFile highscoreInfoContainer3 -> highscoreInfoContatiner1
     * Expected : Success
     *            Chage highscoreInfoContainer3 information equal to highscoreInfoContainer1 information
     */

    @Test
    public void setInfosFromFileTest2() {
        assertEquals(GameDifficulty.Moderate,highscoreInfoContainer3.getDifficulty());
        assertEquals(GameType.Default_6x6,highscoreInfoContainer3.getGameType());
        assertEquals(0,highscoreInfoContainer3.getNumberOfGames());
        assertEquals(Integer.MAX_VALUE,highscoreInfoContainer3.getMinTime());
        assertEquals(0,highscoreInfoContainer3.getNumberOfGamesNoHints());
        assertEquals(0,highscoreInfoContainer3.getTimeNoHints());
        assertEquals(0,highscoreInfoContainer3.getNumberOfHintsUsed());
        assertEquals(0,highscoreInfoContainer3.getTime());
        String s = highscoreInfoContainer1.getActualStats();
        highscoreInfoContainer3.setInfosFromFile(s);
        assertEquals(highscoreInfoContainer1.getDifficulty(),highscoreInfoContainer3.getDifficulty());
        assertEquals(highscoreInfoContainer1.getGameType(),highscoreInfoContainer3.getGameType());
        assertEquals(highscoreInfoContainer1.getNumberOfGames(),highscoreInfoContainer3.getNumberOfGames());
        assertEquals(highscoreInfoContainer1.getMinTime(),highscoreInfoContainer3.getMinTime());
        assertEquals(highscoreInfoContainer1.getNumberOfGamesNoHints(),highscoreInfoContainer3.getNumberOfGamesNoHints());
        assertEquals(highscoreInfoContainer1.getTimeNoHints(),highscoreInfoContainer3.getTimeNoHints());
        assertEquals(highscoreInfoContainer1.getNumberOfHintsUsed(),highscoreInfoContainer3.getNumberOfHintsUsed());
        assertEquals(highscoreInfoContainer1.getTime(),highscoreInfoContainer3.getTime());
    }

}
