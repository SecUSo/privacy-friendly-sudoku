package tu_darmstadt.sudoku.game;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Christopher Beckmann on 06.11.2015.
 */
public class GameBoard implements Cloneable {

    //private int id;
    private GameType gameType;
    private int sectionHeight;
    private int sectionWidth;
    //private List additionalSections
    private int size;
    private GameCell[][] field;
    private List<IModelChangedListener> modelChangedListeners = new LinkedList<>();

    public GameBoard(int size, int sectionHeight, int sectionWidth) {
        this.sectionHeight = sectionHeight;
        this.sectionWidth = sectionWidth;
        this.size = size;

        field = new GameCell[size][size];
    }

    public void reset() {
        actionOnCells(new ICellAction<Boolean>() {
            @Override
            public Boolean action(GameCell gc, Boolean existing) {
                gc.reset();
                return true;
            }
        }, true);
    }

    /*public void initCells(int[][] level) {
        // Initit the game field
        int[] oneDimension = new int[size*size];
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                oneDimension[i*size+j] = level[i][j];
            }
        }
        initCells(oneDimension);
    }*/

    public void initCells(int[] level) {
        int count = 0;
        if(level.length != size*size) {
            throw new IllegalArgumentException("Levelarray must have length of "+size*size+".");
        }
        // Initit the game field with a 1 dimension array
        for(int i = 0; i < size*size; i++) {
            int row = (int)Math.floor(i/size);
            int col = i%size;
            if(level[i] != 0) count++;
            field[row][col] = new GameCell(row,col,size,level[i]);
        }
        //if(count < 17) throw new IllegalArgumentException("There must be at least 17 fixed values.");
    }

    public GameCell getCell(int row, int col) {
        return field[row][col];
    }

    public GameCell[][] getField() {
        return field;
    }

    public LinkedList<GameCell> getRow(final int row) {
        LinkedList<GameCell> result = new LinkedList<GameCell>();
        for(int i = 0; i < size; i++) {
            result.add(field[row][i]);
        }
        return result;
    }

    public LinkedList<GameCell> getColumn(final int col) {
        LinkedList<GameCell> result = new LinkedList<GameCell>();
        for(int i = 0; i < size; i++) {
            result.add(field[i][col]);
        }
        return result;
    }

    public LinkedList<GameCell> getSection(final int sec) {
        return actionOnCells(new ICellAction<LinkedList<GameCell>>() {
            @Override
            public LinkedList<GameCell> action(GameCell gc, LinkedList<GameCell> existing) {
                if ((int) (Math.floor(gc.getRow() / sectionHeight) * sectionHeight + Math.floor(gc.getCol() / sectionWidth)) == sec) {
                    existing.add(gc);
                }
                return existing;
            }
        }, new LinkedList<GameCell>());
    }

    public LinkedList<GameCell> getSection(int row, int col) {
        int sec = (int) (Math.floor(row / sectionHeight) * sectionHeight + Math.floor(col / sectionWidth));
        return getSection(sec);
    }

    public int getSize() {
        return size;
    }

    public <T> T actionOnCells(ICellAction<T> ca, T existing) {
        for(int i = 0; i < field.length; i++) {
            for(int j = 0; j < field[i].length; j++) {
                existing = ca.action(field[i][j], existing);
            }
        }
        return existing;
    }

    public boolean isSolved(final List<CellConflict> errorList) {
        boolean solved = true;

        if(errorList == null) {
            throw new IllegalArgumentException("ErrorList may not be null.");
        }
        errorList.clear();

        // this will automatically build the CellConflict list. so we reset it before we call the checks

        for(int i = 0; i < size; i++) {
            if(!checkList(getRow(i), errorList)) solved = false;
            if(!checkList(getColumn(i), errorList)) solved = false;
            if(!checkList(getSection(i), errorList)) solved = false;
        }
        return solved;
    }

    /**
     * Checks the given list if every number occurs only once.
     * This method will automatically build the errorList.
     * @param list the list of {@link GameCell}s that is supposed to be tested.
     * @return true if every cell has a value and every value occurs only once
     */
    private boolean checkList(final List<GameCell> list, final List<CellConflict> errorList) {
        boolean isNothingEmpty = true;

        for(int i = 0; i < list.size(); i++) {
            for(int j = i + 1; j < list.size(); j++) {
                GameCell c1 = list.get(i);
                GameCell c2 = list.get(j);

                if(c1.getValue() == 0 || c2.getValue() == 0) {
                    isNothingEmpty = false;
                }

                // Same value in one set should not exist
                if(c1.getValue() != 0 && c1.getValue() == c2.getValue()) {
                    // we found an error..
                    if(errorList != null) {
                        errorList.add(new CellConflict(c1, c2));
                    }
                }
            }
        }
        return isNothingEmpty ? (errorList.size() == 0) : false;
    }

    @Override
    public GameBoard clone() throws CloneNotSupportedException {
        GameBoard clone = (GameBoard) super.clone();

        GameCell[][] cloneField = new GameCell[size][size];
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                cloneField[i][j] = field[i][j].clone();
            }
        }
        clone.field = cloneField;

        return clone;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[GameBoard: \n");

        for (int i = 0; i < size; i++) {

            for (int j = 0; j < size; j++) {
                if (j % sectionWidth == 0) {
                    sb.append("\t");
                }

                sb.append(getField()[i][j]);
                sb.append(" ");
            }

            sb.append("]");
        }
        return sb.toString();
    }

    public boolean isFilled() {
        return actionOnCells(new ICellAction<Boolean>() {
            @Override
            public Boolean action(GameCell gc, Boolean existing) {
                return gc.hasValue() ? existing : false;
            }
        }, true);
    }

    public void registerOnModelChangeListener(final IModelChangedListener listener) {
        if(!modelChangedListeners.contains(listener)) {
            actionOnCells(new ICellAction<Boolean>() {

                @Override
                public Boolean action(GameCell gc, Boolean existing) {
                    gc.registerOnModelChangeListener(listener);
                    return existing;
                }
            }, false);
            modelChangedListeners.add(listener);
        }
    }

    public void deleteOnModelChangeListener(final IModelChangedListener listener) {
        if(modelChangedListeners.contains(listener)) {
            actionOnCells(new ICellAction<Boolean>() {

                @Override
                public Boolean action(GameCell gc, Boolean existing) {
                    gc.removeOnModelChangeListener(listener);
                    return existing;
                }
            }, false);
            modelChangedListeners.remove(listener);
        }
    }
}
