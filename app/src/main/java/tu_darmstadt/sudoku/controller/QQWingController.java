package tu_darmstadt.sudoku.controller;

import android.os.*;
import android.util.Log;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Date;
import java.util.concurrent.atomic.AtomicIntegerArray;

import tu_darmstadt.sudoku.controller.qqwing.Action;
import tu_darmstadt.sudoku.controller.qqwing.PrintStyle;
import tu_darmstadt.sudoku.controller.qqwing.QQWing;
import tu_darmstadt.sudoku.controller.qqwing.Symmetry;
import tu_darmstadt.sudoku.game.GameBoard;
import tu_darmstadt.sudoku.game.GameDifficulty;
import tu_darmstadt.sudoku.game.GameType;

/**
 * Created by Chris on 21.11.2015.
 */
public class QQWingController {

    final QQWingOptions opts = new QQWingOptions();

    private int[] level;
    private int[] solution;
    private LinkedList<int[]> generated = new LinkedList<>();
    private boolean solveImpossible = false;

    public int[] generate(GameType type, GameDifficulty difficulty) {
        generated.clear();
        opts.gameDifficulty = difficulty;
        opts.action = Action.GENERATE;
        opts.needNow = true;
        opts.printSolution = false;
        opts.threads = Runtime.getRuntime().availableProcessors();
        opts.gameType = type;
        doAction();
        return generated.poll();
    }

    public LinkedList<int[]> generateMultiple(GameType type, GameDifficulty difficulty, int amount) {
        generated.clear();
        opts.numberToGenerate = amount;
        opts.gameDifficulty = difficulty;
        opts.needNow = true;
        opts.action = Action.GENERATE;
        opts.printSolution = false;
        opts.threads = Runtime.getRuntime().availableProcessors();
        opts.gameType = type;
        doAction();
        return generated;
    }

    public int[] solve(GameBoard gameBoard) {

        level = new int[gameBoard.getSize()*gameBoard.getSize()];
        solveImpossible = false;

        for(int i = 0; i < gameBoard.getSize(); i++) {
            for(int j = 0; j < gameBoard.getSize(); j++) {
                if(gameBoard.getCell(i,j).isFixed()) {
                    level[gameBoard.getSize() * i + j] = gameBoard.getCell(i,j).getValue();
                }
            }
        }

        opts.needNow = true;
        opts.action = Action.SOLVE;
        opts.printSolution = true;
        opts.threads = 1;
        opts.gameType = gameBoard.getGameType();
        doAction();
        if(solveImpossible) {
            // TODO: do something else.
        }
        return solution;
    }

    private void doAction() {
        // The number of puzzles solved or generated.
        final AtomicInteger puzzleCount = new AtomicInteger(0);
        final AtomicBoolean done = new AtomicBoolean(false);

        Thread[] threads = new Thread[opts.threads];
        for (int threadCount = 0; threadCount < threads.length; threadCount++) {
            threads[threadCount] = new Thread(
                    new Runnable() {

                        // Create a new puzzle board
                        // and set the options
                        private QQWing ss = createQQWing();

                        private QQWing createQQWing() {
                            QQWing ss = new QQWing(opts.gameType, opts.gameDifficulty);
                            ss.setRecordHistory(opts.printHistory || opts.printInstructions || opts.printStats || opts.gameDifficulty != GameDifficulty.Unspecified);
                            ss.setLogHistory(opts.logHistory);
                            ss.setPrintStyle(opts.printStyle);
                            return ss;
                        }

                        @Override
                        public void run() {

                            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

                            try {

                                // Solve puzzle or generate puzzles
                                // until end of input for solving, or
                                // until we have generated the specified number.
                                while (!done.get()) {

                                    // Record whether the puzzle was possible or
                                    // not,
                                    // so that we don't try to solve impossible
                                    // givens.
                                    boolean havePuzzle;

                                    if (opts.action == Action.GENERATE) {
                                        // Generate a puzzle
                                        havePuzzle = ss.generatePuzzleSymmetry(opts.symmetry);

                                    } else {
                                        // Read the next puzzle on STDIN
                                        int[] puzzle = new int[QQWing.BOARD_SIZE];
                                        if (getPuzzleToSolve(puzzle)) {
                                            havePuzzle = ss.setPuzzle(puzzle);
                                            if (havePuzzle) {
                                                puzzleCount.getAndDecrement();
                                            } else {
                                                // TODO: Puzzle to solve is impossible.
                                                solveImpossible = true;
                                            }
                                        } else {
                                            // Set loop to terminate when nothing is
                                            // left on STDIN
                                            havePuzzle = false;
                                            done.set(true);
                                        }
                                        puzzle = null;
                                    }

                                    int solutions = 0;

                                    if (havePuzzle) {

                                        // Count the solutions if requested.
                                        // (Must be done before solving, as it would
                                        // mess up the stats.)
                                        //if (opts.countSolutions) {
                                        //    solutions = ss.countSolutions();
                                        //}

                                        // Solve the puzzle
                                        if (opts.printSolution || opts.printHistory || opts.printStats || opts.printInstructions || opts.gameDifficulty != GameDifficulty.Unspecified) {
                                            ss.solve();
                                            solution = ss.getSolution();
                                        }

                                        // Bail out if it didn't meet the difficulty
                                        // standards for generation
                                        if (opts.action == Action.GENERATE) {
                                            if (opts.gameDifficulty != GameDifficulty.Unspecified && opts.gameDifficulty != ss.getDifficulty()) {
                                                havePuzzle = false;
                                                // check if other threads have
                                                // finished the job
                                                if (puzzleCount.get() >= opts.numberToGenerate) done.set(true);
                                            } else {
                                                int numDone = puzzleCount.incrementAndGet();
                                                if (numDone >= opts.numberToGenerate) done.set(true);
                                                if (numDone > opts.numberToGenerate) havePuzzle = false;
                                            }
                                        }
                                        if(havePuzzle) {
                                            generated.add(ss.getPuzzle());
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("QQWing", "Exception Occured", e);
                                return;
                            }
                        }

                    }
            );
            threads[threadCount].start();
        }

        if(opts.needNow) {
            for (int i = 0; i < threads.length; i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class QQWingOptions {
        // defaults for options
        boolean needNow = false;
        boolean printPuzzle = false;
        boolean printSolution = false;
        boolean printHistory = false;
        boolean printInstructions = false;
        boolean timer = false;
        boolean countSolutions = false;
        Action action = Action.NONE;
        boolean logHistory = false;
        PrintStyle printStyle = PrintStyle.READABLE;
        int numberToGenerate = 1;
        boolean printStats = false;
        GameDifficulty gameDifficulty = GameDifficulty.Unspecified;
        GameType gameType = GameType.Unspecified;
        Symmetry symmetry = Symmetry.NONE;
        int threads = Runtime.getRuntime().availableProcessors();
    }

    private static long getMicroseconds() {
        return new Date().getTime() * 1000;
    }

    private boolean getPuzzleToSolve(int[] puzzle) {
        if(level != null) {
            if(puzzle.length == level.length) {
                for(int i = 0; i < level.length; i++) {
                    puzzle[i] = level[i];
                }
            }
            level = null;
            return true;
        }
        return false;
    }

}
