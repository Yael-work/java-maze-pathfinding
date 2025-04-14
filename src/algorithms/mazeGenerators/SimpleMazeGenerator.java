//package algorithms.mazeGenerators;
//import java.util.Random;
//
//public class SimpleMazeGenerator extends AMazeGenerator {
//    private static final Random random = new Random();
//
//    @Override
//    public Maze generate(int rows, int columns){
//        Maze maze;
//        do {
//            maze = new Maze (rows , columns);
////            Position start = new Position(0,0);
////            Position end = new Position(rows - 1, columns - 1);
////            maze.setStartPosition(start);
////            maze.setGoalPosition(end);
//
//            Position start = getRandomBorderPosition(rows, columns);
//            Position goal;
//            do {
//                goal = getRandomBorderPosition(rows, columns);
//            } while (goal.equals(start));
//            maze.setStartPosition(start);
//            maze.setGoalPosition(goal);
//
//            // Fill the maze with walls - 0.3 probability of being a wall
//            for (int row = 0 ; row < rows ; row++) {
//                for (int column = 0 ; column < columns ; column++) {
//                    if ((row == 0 && column == 0) || (row == rows - 1 && column == columns - 1))
//                        continue;
//                    if (random.nextDouble() <= 0.3) {
//                        maze.setCell(row, column, 1); // Wall
//                    } else {
//                        maze.setCell(row, column, 0); // Path
//                    }
//                }
//            }
//        }
//        while (!isReachble(maze));
//        return maze;
//    }
//    // isReachble method checks if the maze is reachable from start to end
//    // helper function
//    private boolean isReachble(Maze maze) {
//        int rows = maze.getRows();
//        int columns = maze.getColumns();
//        boolean[][] visited = new boolean[rows][columns];
//        int end_row = maze.getGoalPosition().getRowIndex();
//        int end_col = maze.getGoalPosition().getColumnIndex();
//        return dfs(maze, end_row, end_col, visited);
//    }
//
//    // Depth First Search
//    // checks if the maze is reachable from start to end
//    private boolean dfs(Maze maze, int row, int col, boolean[][] visited) {
//        if (row < 0 || row >= maze.getRows() || col < 0 || col >= maze.getColumns()) {
//            return false; // out of bounds
//        }
//        if (visited[row][col]) {
//            return false; // already visited
//        }
//        if (maze.getCell(row, col) == 1) {
//            return false; // Wall
//        }
//        if (row == 0 && col == 0) {
//            return true; // Reached the start position
//        }
//        visited[row][col] = true;
//        boolean found = dfs(maze, row - 1, col, visited) || // Up
//                        dfs(maze, row + 1, col, visited) || // Down
//                        dfs(maze, row, col - 1, visited) || // Left
//                        dfs(maze, row, col + 1, visited);   // Right
//        visited[row][col] = false; // Backtrack
//        return found;
//    }
//}

//package algorithms.mazeGenerators;
//import java.util.Random;
//
//public class SimpleMazeGenerator extends AMazeGenerator {
//
//    @Override
//    public Maze generate(int rows, int columns){
//        Maze maze = new Maze (rows , columns);
//
//        Position start = getRandomBorderPosition(rows, columns);
//        Position goal;
//        do {
//            goal = getRandomBorderPosition(rows, columns);
//        } while (goal.equals(start));
//        maze.setStartPosition(start);
//        maze.setGoalPosition(goal);
//
//        // Initialize the maze as mostly open
//        for (int row = 0 ; row < rows ; row++) {
//            for (int column = 0 ; column < columns ; column++) {
//                maze.setCell(row, column, 0); // Path
//            }
//        }
//
//        // Add a limited number of random walls
//        int numberOfWalls = (int) (rows * columns * 0.15); // Adjust the wall density (e.g., 15%)
//        for (int i = 0; i < numberOfWalls; i++) {
//            int row = random.nextInt(rows);
//            int column = random.nextInt(columns);
//            // Don't place walls on the start or goal
//            if (!maze.getStartPosition().equals(new Position(row, column)) &&
//                    !maze.getGoalPosition().equals(new Position(row, column))) {
//                maze.setCell(row, column, 1); // Wall
//            }
//        }
//
//        while (!isReachble(maze));
//        return maze;
//    }
//
//
//        // isReachble method checks if the maze is reachable from start to end
//    // helper function
//    private boolean isReachble(Maze maze) {
//        int rows = maze.getRows();
//        int columns = maze.getColumns();
//        boolean[][] visited = new boolean[rows][columns];
//        int end_row = maze.getGoalPosition().getRowIndex();
//        int end_col = maze.getGoalPosition().getColumnIndex();
//        return dfs(maze, end_row, end_col, visited);
//    }
//
//    // Depth First Search
//    // checks if the maze is reachable from start to end
//    private boolean dfs(Maze maze, int row, int col, boolean[][] visited) {
//        if (row < 0 || row >= maze.getRows() || col < 0 || col >= maze.getColumns()) {
//            return false; // out of bounds
//        }
//        if (visited[row][col]) {
//            return false; // already visited
//        }
//        if (maze.getCell(row, col) == 1) {
//            return false; // Wall
//        }
//        if (row == 0 && col == 0) {
//            return true; // Reached the start position
//        }
//        visited[row][col] = true;
//        boolean found = dfs(maze, row - 1, col, visited) || // Up
//                        dfs(maze, row + 1, col, visited) || // Down
//                        dfs(maze, row, col - 1, visited) || // Left
//                        dfs(maze, row, col + 1, visited);   // Right
//        visited[row][col] = false; // Backtrack
//        return found;
//    }
//}

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
