package algorithms.mazeGenerators;

import java.util.Random;
import java.util.LinkedList;
import java.util.Queue;

public class SimpleMazeGenerator extends AMazeGenerator {

    @Override
    public Maze generate(int rows, int columns) {
        Maze maze;
        do {
            maze = createMaze(rows, columns);
        } while (!isMazeReachable(maze));
        return maze;
    }

    private Maze createMaze(int rows, int columns) {
        Maze maze = new Maze(rows, columns);

        Position start = getRandomBorderPosition(rows, columns);
        Position goal;
        do {
            goal = getRandomBorderPosition(rows, columns);
        } while (goal.equals(start));

        maze.setStartPosition(start);
        maze.setGoalPosition(goal);

        // Initialize the maze as mostly open
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                maze.setCell(row, column, 0); // Path
            }
        }

        // Add a limited number of random walls
        int numberOfWalls = (int) (rows * columns * 0.15); // Adjust the wall density (e.g., 15%)
        Random random = new Random();
        for (int i = 0; i < numberOfWalls; i++) {
            int row = random.nextInt(rows);
            int column = random.nextInt(columns);
            // Don't place walls on the start or goal
            if (!maze.getStartPosition().equals(new Position(row, column)) &&
                    !maze.getGoalPosition().equals(new Position(row, column))) {
                maze.setCell(row, column, 1); // Wall
            }
        }

        return maze;
    }

    private boolean isMazeReachable(Maze maze) {
        Position start = maze.getStartPosition();
        Position goal = maze.getGoalPosition();
        int rows = maze.getRows();
        int columns = maze.getColumns();

        boolean[][] visited = new boolean[rows][columns];
        Queue<Position> queue = new LinkedList<>();
        queue.add(start);
        visited[start.getRowIndex()][start.getColumnIndex()] = true;

        while (!queue.isEmpty()) {
            Position current = queue.poll();
            if (current.equals(goal)) {
                return true; // Path exists
            }

            // Explore neighboring cells (up, down, left, right)
            for (Position neighbor : getNeighbors(current, rows, columns)) {
                if (!visited[neighbor.getRowIndex()][neighbor.getColumnIndex()] &&
                        maze.getCell(neighbor.getRowIndex(), neighbor.getColumnIndex()) == 0) { // Path
                    visited[neighbor.getRowIndex()][neighbor.getColumnIndex()] = true;
                    queue.add(neighbor);
                }
            }
        }

        return false; // No path to goal
    }

    private Iterable<Position> getNeighbors(Position pos, int rows, int columns) {
        LinkedList<Position> neighbors = new LinkedList<>();

        int row = pos.getRowIndex();
        int column = pos.getColumnIndex();

        if (row > 0) neighbors.add(new Position(row - 1, column)); // Up
        if (row < rows - 1) neighbors.add(new Position(row + 1, column)); // Down
        if (column > 0) neighbors.add(new Position(row, column - 1)); // Left
        if (column < columns - 1) neighbors.add(new Position(row, column + 1)); // Right

        return neighbors;
    }
}
