package algorithms.mazeGenerators;
import java.util.Arrays;

public class Maze {
    private final int columns;
    private final int rows;
    private final int[][] map;
    private Position startPosition;
    private Position goalPosition;

    public Maze(int rows , int columns) {
    this.rows = rows;
    this.columns = columns;
    this.map = new int[rows][columns];
    }
    public Maze(int[][] map, Position start, Position goal) {
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
}