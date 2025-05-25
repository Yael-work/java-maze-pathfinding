package algorithms.mazeGenerators;
import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
/**
 * represents a 2d maze with walls and paths.
 * includes a start and end position, and can be used as a base for search problems.
 */

public class Maze implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final int columns;
    private final int rows;
    private final int[][] map;
    private Position startPosition;
    private Position goalPosition;


    public Maze(int rows , int columns) {
        if (rows < 0 || columns < 0) {
            throw new IllegalArgumentException("Maze must have non negative rows and columns");
        }
        this.rows = rows;
        this.columns = columns;
        this.map = new int[rows][columns];
    }

    public Maze(int[][] map, Position start, Position goal) {
        if (start == null || goal == null || map == null) {
            throw new NullPointerException("Start, goal and map must not be null");
        }
        if (start.getRowIndex() < 0 || start.getColumnIndex() < 0 || goal.getRowIndex() < 0 || goal.getColumnIndex() < 0 )
        {
            throw new IllegalArgumentException("Maze must have non negative rows and columns");
        }

        this.rows = map.length;
        this.columns = map[0].length;
        this.map = new int[rows][columns];
        for (int i = 0; i < rows; i++) {
            this.map[i] = Arrays.copyOf(map[i], columns);
        }
        this.startPosition = start;
        this.goalPosition = goal;
    }
    public int getRows() {
        return rows;
    }
    public int getColumns() {
        return columns;
    }
    public int getCell(int r, int c) {
        return map[r][c];
    }

    public void setCell(int r, int c, int val) {
        map[r][c] = val;
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Position pos) {
        this.startPosition = pos;
    }

    public Position getGoalPosition() {
        return goalPosition;
    }

    public void setGoalPosition(Position pos) {
        this.goalPosition = pos;
    }
    public void print() {
        for (int r = 0; r < rows; r++) {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < columns; c++) {
                if (startPosition.getRowIndex() == r && startPosition.getColumnIndex() == c) {
                    sb.append('S');
                } else if (goalPosition.getRowIndex() == r && goalPosition.getColumnIndex() == c) {
                    sb.append('E');
                } else {
                    sb.append(map[r][c]);
                }
            }
            System.out.println(sb.toString());
        }
    }
    public byte[] toByteArray() {
        // Header: 6 shorts (2 bytes each) = 12 bytes
        byte[] byteArray = new byte[12 + rows * columns];

        // Store number of rows and columns (2 bytes each)
        byteArray[0] = (byte) (rows >> 8);
        byteArray[1] = (byte) (rows);
        byteArray[2] = (byte) (columns >> 8);
        byteArray[3] = (byte) (columns);

        // Start position
        byteArray[4] = (byte) (startPosition.getRowIndex() >> 8);
        byteArray[5] = (byte) (startPosition.getRowIndex());
        byteArray[6] = (byte) (startPosition.getColumnIndex() >> 8);
        byteArray[7] = (byte) (startPosition.getColumnIndex());

        // Goal position
        byteArray[8] = (byte) (goalPosition.getRowIndex() >> 8);
        byteArray[9] = (byte) (goalPosition.getRowIndex());
        byteArray[10] = (byte) (goalPosition.getColumnIndex() >> 8);
        byteArray[11] = (byte) (goalPosition.getColumnIndex());

        // Maze data
        int index = 12;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                byteArray[index++] = (byte) map[r][c];
            }
        }

        return byteArray;
    }
    public Maze(byte[] byteArray) {
        // Read dimensions
        int rows = ((byteArray[0] & 0xFF) << 8) | (byteArray[1] & 0xFF);
        int columns = ((byteArray[2] & 0xFF) << 8) | (byteArray[3] & 0xFF);

        this.rows = rows;
        this.columns = columns;
        this.map = new int[rows][columns];

        // Read start position
        int startRow = ((byteArray[4] & 0xFF) << 8) | (byteArray[5] & 0xFF);
        int startCol = ((byteArray[6] & 0xFF) << 8) | (byteArray[7] & 0xFF);
        this.startPosition = new Position(startRow, startCol);

        // Read goal position
        int goalRow = ((byteArray[8] & 0xFF) << 8) | (byteArray[9] & 0xFF);
        int goalCol = ((byteArray[10] & 0xFF) << 8) | (byteArray[11] & 0xFF);
        this.goalPosition = new Position(goalRow, goalCol);

        // Read maze data
        int index = 12;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                this.map[r][c] = byteArray[index++];
            }
        }
    }

}