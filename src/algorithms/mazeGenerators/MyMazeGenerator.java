// simplest way but not good

//package algorithms.mazeGenerators;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//import java.util.Stack;
//
//public class MyMazeGenerator extends AMazeGenerator {
//    private static final Random random = new Random();
//
//    @Override
//    public Maze generate(int rows, int columns) {
//        if (rows <= 0 || columns <= 0) {
//            throw new IllegalArgumentException("Rows and columns must be positive integers.");
//        }
//
//        Maze maze = new Maze(rows, columns);
//        // Initialize the maze with all walls (1s)
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < columns; j++) {
//                maze.setCell(i, j, 1);
//            }
//        }
//
//        // Start from a random cell (or a fixed one like (0,0))
//        Position start = new Position(0, 0);
//        maze.setStartPosition(start);
//
//        Stack<Position> stack = new Stack<>();
//        stack.push(start);
//        maze.setCell(start.getRowIndex(), start.getColumnIndex(), 0); // Mark as visited
//
//        while (!stack.isEmpty()) {
//            Position current = stack.pop();
//            List<Position> unvisitedNeighbors = getUnvisitedNeighbors(maze, current);
//
//            if (!unvisitedNeighbors.isEmpty()) {
//                stack.push(current); // Push current back to backtrack later
//
//                // Choose a random unvisited neighbor
//                Position next = unvisitedNeighbors.get(random.nextInt(unvisitedNeighbors.size()));
//
//                // Carve a path to the neighbor by setting the wall between them to 0
//                int rowDiff = next.getRowIndex() - current.getRowIndex();
//                int colDiff = next.getColumnIndex() - current.getColumnIndex();
//
//                if (rowDiff == 2) {
//                    maze.setCell(current.getRowIndex() + 1, current.getColumnIndex(), 0);
//                } else if (rowDiff == -2) {
//                    maze.setCell(current.getRowIndex() - 1, current.getColumnIndex(), 0);
//                } else if (colDiff == 2) {
//                    maze.setCell(current.getRowIndex(), current.getColumnIndex() + 1, 0);
//                } else if (colDiff == -2) {
//                    maze.setCell(current.getRowIndex(), current.getColumnIndex() - 1, 0);
//                }
//
//                maze.setCell(next.getRowIndex(), next.getColumnIndex(), 0); // Mark neighbor as visited
//                stack.push(next);
//            }
//        }
//
//        // Set the goal position (bottom-right corner)
//        maze.setGoalPosition(new Position(rows - 3, columns - 1));
//
//        return maze;
//    }
//
//    private List<Position> getUnvisitedNeighbors(Maze maze, Position pos) {
//        List<Position> neighbors = new ArrayList<>();
//        int row = pos.getRowIndex();
//        int col = pos.getColumnIndex();
//        int rows = maze.getRows();
//        int columns = maze.getColumns();
//
//        // Check neighbors with a gap of 2 to ensure walls between paths
//        // Up
//        if (row > 1 && maze.getCell(row - 2, col) == 1) {
//            neighbors.add(new Position(row - 2, col));
//        }
//        // Down
//        if (row < rows - 2 && maze.getCell(row + 2, col) == 1) {
//            neighbors.add(new Position(row + 2, col));
//        }
//        // Left
//        if (col > 1 && maze.getCell(row, col - 2) == 1) {
//            neighbors.add(new Position(row, col - 2));
//        }
//        // Right
//        if (col < columns - 2 && maze.getCell(row, col + 2) == 1) {
//            neighbors.add(new Position(row, col + 2));
//        }
//
//        // Shuffle the neighbors to ensure randomness in path carving
//        java.util.Collections.shuffle(neighbors, random);
//        return neighbors;
//    }
//}


//

//package algorithms.mazeGenerators;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//import java.util.Stack;
//
//public class MyMazeGenerator extends AMazeGenerator {
//    private static final Random random = new Random();
//
//    @Override
//    public Maze generate(int rows, int columns) {
//        if (rows <= 0 || columns <= 0) {
//            throw new IllegalArgumentException("Rows and columns must be positive integers.");
//        }
//
//        Maze maze = new Maze(rows, columns);
//        // Initialize the maze with all walls (1s)
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < columns; j++) {
//                maze.setCell(i, j, 1);
//            }
//        }
//
//        // Start from the top-left corner
//        Position start = new Position(0, 0);
//        maze.setStartPosition(start);
//
//        Stack<Position> stack = new Stack<>();
//        stack.push(start);
//        maze.setCell(start.getRowIndex(), start.getColumnIndex(), 0); // Mark as visited (empty path)
//
//        while (!stack.isEmpty()) {
//            Position current = stack.pop();
//            List<Position> unvisitedNeighbors = getUnvisitedNeighbors(maze, current);
//
//            if (!unvisitedNeighbors.isEmpty()) {
//                stack.push(current); // Push current back to backtrack later
//
//                // Choose a random unvisited neighbor
//                Position next = unvisitedNeighbors.get(random.nextInt(unvisitedNeighbors.size()));
//
//                // Carve a path to the neighbor
//                carvePath(maze, current, next);
//
//                maze.setCell(next.getRowIndex(), next.getColumnIndex(), 0); // Mark neighbor as visited
//                stack.push(next);
//            }
//        }
//
//        // Set the goal position (bottom-right corner)
//        Position goal = new Position(rows - 4, columns - 1);
//        maze.setGoalPosition(goal);
//
//        // Post-processing to ensure the goal is reachable
//        ensureGoalIsReachable(maze, goal);
//
//        return maze;
//    }
//
//    private List<Position> getUnvisitedNeighbors(Maze maze, Position pos) {
//        List<Position> neighbors = new ArrayList<>();
//        int row = pos.getRowIndex();
//        int col = pos.getColumnIndex();
//        int rows = maze.getRows();
//        int columns = maze.getColumns();
//
//        // Check neighbors with a gap of 2
//        if (row > 1 && maze.getCell(row - 2, col) == 1) neighbors.add(new Position(row - 2, col));
//        if (row < rows - 2 && maze.getCell(row + 2, col) == 1) neighbors.add(new Position(row + 2, col));
//        if (col > 1 && maze.getCell(row, col - 2) == 1) neighbors.add(new Position(row, col - 2));
//        if (col < columns - 2 && maze.getCell(row, col + 2) == 1) neighbors.add(new Position(row, col + 2));
//
//        java.util.Collections.shuffle(neighbors, random);
//        return neighbors;
//    }
//
//    private void carvePath(Maze maze, Position from, Position to) {
//        int rowDiff = to.getRowIndex() - from.getRowIndex();
//        int colDiff = to.getColumnIndex() - from.getColumnIndex();
//
//        if (rowDiff == 2) maze.setCell(from.getRowIndex() + 1, from.getColumnIndex(), 0);
//        else if (rowDiff == -2) maze.setCell(from.getRowIndex() - 1, from.getColumnIndex(), 0);
//        else if (colDiff == 2) maze.setCell(from.getRowIndex(), from.getColumnIndex() + 1, 0);
//        else if (colDiff == -2) maze.setCell(from.getRowIndex(), from.getColumnIndex() - 1, 0);
//    }
//
//    private void ensureGoalIsReachable(Maze maze, Position goal) {
//        int goalRow = goal.getRowIndex();
//        int goalCol = goal.getColumnIndex();
//        int rows = maze.getRows();
//        int columns = maze.getColumns();
//
//        if (maze.getCell(goalRow, goalCol) == 1) {
//            // Find an adjacent empty cell and carve a path to the goal
//            List<Position> adjacentEmpty = new ArrayList<>();
//            if (goalRow > 0 && maze.getCell(goalRow - 1, goalCol) == 0) adjacentEmpty.add(new Position(goalRow - 1, goalCol));
//            if (goalRow < rows - 1 && maze.getCell(goalRow + 1, goalCol) == 0) adjacentEmpty.add(new Position(goalRow + 1, goalCol));
//            if (goalCol > 0 && maze.getCell(goalRow, goalCol - 1) == 0) adjacentEmpty.add(new Position(goalRow, goalCol - 1));
//            if (goalCol < columns - 1 && maze.getCell(goalRow, goalCol + 1) == 0) adjacentEmpty.add(new Position(goalRow, goalCol + 1));
//
//            if (!adjacentEmpty.isEmpty()) {
//                Position neighbor = adjacentEmpty.get(random.nextInt(adjacentEmpty.size()));
//                maze.setCell(goalRow, goalCol, 0); // Make the goal an empty cell
//                carvePath(maze, neighbor, goal); // Carve a path to the goal (might be redundant if already adjacent)
//            } else {
//                // If no adjacent empty cell, we might need a more complex approach
//                // For simplicity, let's just try to carve from a nearby wall if possible
//                List<Position> adjacentWalls = new ArrayList<>();
//                if (goalRow > 0 && maze.getCell(goalRow - 1, goalCol) == 1) adjacentWalls.add(new Position(goalRow - 1, goalCol));
//                if (goalRow < rows - 1 && maze.getCell(goalRow + 1, goalCol) == 1) adjacentWalls.add(new Position(goalRow + 1, goalCol));
//                if (goalCol > 0 && maze.getCell(goalRow, goalCol - 1) == 1) adjacentWalls.add(new Position(goalRow, goalCol - 1));
//                if (goalCol < columns - 1 && maze.getCell(goalRow, goalCol + 1) == 1) adjacentWalls.add(new Position(goalRow, goalCol + 1));
//
//                if (!adjacentWalls.isEmpty()) {
//                    Position wallNeighbor = adjacentWalls.get(random.nextInt(adjacentWalls.size()));
//                    maze.setCell(goalRow, goalCol, 0);
//                    maze.setCell(wallNeighbor.getRowIndex(), wallNeighbor.getColumnIndex(), 0);
//                }
//            }
//        }
//    }
//}

//package algorithms.mazeGenerators;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//import java.util.Stack;
//
//public class MyMazeGenerator extends AMazeGenerator {
//    private static final Random random = new Random();
//
//    @Override
//    public Maze generate(int rows, int columns) {
//        if (rows <= 0 || columns <= 0) {
//            throw new IllegalArgumentException("Rows and columns must be positive integers.");
//        }
//
//        Maze maze = new Maze(rows, columns);
//        // Initialize the maze with all walls (1s)
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < columns; j++) {
//                maze.setCell(i, j, 1);
//            }
//        }
//
//        // Choose a random start position on the border
//        Position start = getRandomBorderPosition(rows, columns);
//        maze.setStartPosition(start);
//
//        Stack<Position> stack = new Stack<>();
//        stack.push(start);
//        maze.setCell(start.getRowIndex(), start.getColumnIndex(), 0); // Mark as visited (empty path)
//
//        while (!stack.isEmpty()) {
//            Position current = stack.pop();
//            List<Position> unvisitedNeighbors = getUnvisitedNeighbors(maze, current);
//
//            if (!unvisitedNeighbors.isEmpty()) {
//                stack.push(current); // Push current back to backtrack later
//
//                // Choose a random unvisited neighbor
//                Position next = unvisitedNeighbors.get(random.nextInt(unvisitedNeighbors.size()));
//
//                // Carve a path to the neighbor
//                carvePath(maze, current, next);
//
//                maze.setCell(next.getRowIndex(), next.getColumnIndex(), 0); // Mark neighbor as visited
//                stack.push(next);
//            }
//        }
//
//        // Choose a random end position on the border, different from the start
//        Position goal;
//        do {
//            goal = getRandomBorderPosition(rows, columns);
//        } while (goal.equals(start));
//        maze.setGoalPosition(goal);
//        maze.setCell(goal.getRowIndex(), goal.getColumnIndex(), 0); // Ensure end is open
//
//        // Post-processing to ensure the goal is reachable (might be needed if the random border placement isolates it)
//        ensureGoalIsReachable(maze, goal);
//
//        return maze;
//    }
//
//    private Position getRandomBorderPosition(int rows, int columns) {
//        int side = random.nextInt(4); // 0: top, 1: bottom, 2: left, 3: right
//        int row, col;
//
//        switch (side) {
//            case 0: // Top row
//                row = 0;
//                col = random.nextInt(columns);
//                break;
//            case 1: // Bottom row
//                row = rows - 1;
//                col = random.nextInt(columns);
//                break;
//            case 2: // Left column
//                row = random.nextInt(rows);
//                col = 0;
//                break;
//            case 3: // Right column
//                row = random.nextInt(rows);
//                col = columns - 1;
//                break;
//            default:
//                row = 0;
//                col = 0; // Should not happen
//                break;
//        }
//        return new Position(row, col);
//    }
//
//    private List<Position> getUnvisitedNeighbors(Maze maze, Position pos) {
//        List<Position> neighbors = new ArrayList<>();
//        int row = pos.getRowIndex();
//        int col = pos.getColumnIndex();
//        int rows = maze.getRows();
//        int columns = maze.getColumns();
//
//        // Check neighbors with a gap of 2
//        if (row > 1 && maze.getCell(row - 2, col) == 1) neighbors.add(new Position(row - 2, col));
//        if (row < rows - 2 && maze.getCell(row + 2, col) == 1) neighbors.add(new Position(row + 2, col));
//        if (col > 1 && maze.getCell(row, col - 2) == 1) neighbors.add(new Position(row, col - 2));
//        if (col < columns - 2 && maze.getCell(row, col + 2) == 1) neighbors.add(new Position(row, col + 2));
//
//        java.util.Collections.shuffle(neighbors, random);
//        return neighbors;
//    }
//
//    private void carvePath(Maze maze, Position from, Position to) {
//        int rowDiff = to.getRowIndex() - from.getRowIndex();
//        int colDiff = to.getColumnIndex() - from.getColumnIndex();
//
//        if (rowDiff == 2) maze.setCell(from.getRowIndex() + 1, from.getColumnIndex(), 0);
//        else if (rowDiff == -2) maze.setCell(from.getRowIndex() - 1, from.getColumnIndex(), 0);
//        else if (colDiff == 2) maze.setCell(from.getRowIndex(), from.getColumnIndex() + 1, 0);
//        else if (colDiff == -2) maze.setCell(from.getRowIndex(), from.getColumnIndex() - 1, 0);
//    }
//
//    private void ensureGoalIsReachable(Maze maze, Position goal) {
//        int goalRow = goal.getRowIndex();
//        int goalCol = goal.getColumnIndex();
//        int rows = maze.getRows();
//        int columns = maze.getColumns();
//
//        if (maze.getCell(goalRow, goalCol) == 1) {
//            // Find an adjacent empty cell and carve a path to the goal
//            List<Position> adjacentEmpty = new ArrayList<>();
//            if (goalRow > 0 && maze.getCell(goalRow - 1, goalCol) == 0) adjacentEmpty.add(new Position(goalRow - 1, goalCol));
//            if (goalRow < rows - 1 && maze.getCell(goalRow + 1, goalCol) == 0) adjacentEmpty.add(new Position(goalRow + 1, goalCol));
//            if (goalCol > 0 && maze.getCell(goalRow, goalCol - 1) == 0) adjacentEmpty.add(new Position(goalRow, goalCol - 1));
//            if (goalCol < columns - 1 && maze.getCell(goalRow, goalCol + 1) == 0) adjacentEmpty.add(new Position(goalRow, goalCol + 1));
//
//            if (!adjacentEmpty.isEmpty()) {
//                Position neighbor = adjacentEmpty.get(random.nextInt(adjacentEmpty.size()));
//                maze.setCell(goalRow, goalCol, 0); // Make the goal an empty cell
//                carvePath(maze, neighbor, goal); // Carve a path to the goal (might be redundant if already adjacent)
//            } else {
//                // If no adjacent empty cell, try to carve from a nearby wall
//                List<Position> adjacentWalls = new ArrayList<>();
//                if (goalRow > 0 && maze.getCell(goalRow - 1, goalCol) == 1) adjacentWalls.add(new Position(goalRow - 1, goalCol));
//                if (goalRow < rows - 1 && maze.getCell(goalRow + 1, goalCol) == 1) adjacentWalls.add(new Position(goalRow + 1, goalCol));
//                if (goalCol > 0 && maze.getCell(goalRow, goalCol - 1) == 1) adjacentWalls.add(new Position(goalRow, goalCol - 1));
//                if (goalCol < columns - 1 && maze.getCell(goalRow, goalCol + 1) == 1) adjacentWalls.add(new Position(goalRow, goalCol + 1));
//
//                if (!adjacentWalls.isEmpty()) {
//                    Position wallNeighbor = adjacentWalls.get(random.nextInt(adjacentWalls.size()));
//                    maze.setCell(goalRow, goalCol, 0);
//                    maze.setCell(wallNeighbor.getRowIndex(), wallNeighbor.getColumnIndex(), 0);
//                }
//            }
//        }
//    }
//}




//package algorithms.mazeGenerators;
//
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Queue;
//import java.util.Random;
//import java.util.Stack;
//
//public class MyMazeGenerator extends AMazeGenerator {
//    private static final Random random = new Random();
//
//    @Override
//    public Maze generate(int rows, int columns) {
//        if (rows <= 0 || columns <= 0) {
//            throw new IllegalArgumentException("Rows and columns must be positive integers.");
//        }
//
//        Maze maze = new Maze(rows, columns);
//        // Initialize the maze with all walls (1s)
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < columns; j++) {
//                maze.setCell(i, j, 1);
//            }
//        }
//
//        // Choose a random start position on the border
//        Position start = getRandomBorderPosition(rows, columns);
//        maze.setStartPosition(start);
//        maze.setCell(start.getRowIndex(), start.getColumnIndex(), 0); // Ensure start is open
//
//        Position current = new Position(start.getRowIndex(), start.getColumnIndex());
//        Stack<Position> stack = new Stack<>();
//        stack.push(current);
//
//        while (!stack.isEmpty()) {
//            current = stack.pop();
//            List<Position> unvisitedNeighbors = getUnvisitedNeighbors(maze, current);
//
//            if (!unvisitedNeighbors.isEmpty()) {
//                stack.push(current);
//                Position next = unvisitedNeighbors.get(random.nextInt(unvisitedNeighbors.size()));
//                carvePath(maze, current, next);
//                maze.setCell(next.getRowIndex(), next.getColumnIndex(), 0);
//                stack.push(next);
//            }
//        }
//
//        // Choose a random end position on the border, different from the start
//        Position goal;
//        do {
//            goal = getRandomBorderPosition(rows, columns);
//        } while (goal.equals(start));
//        maze.setGoalPosition(goal);
//        maze.setCell(goal.getRowIndex(), goal.getColumnIndex(), 0); // Ensure end is open
//
//        // Force a path between start and goal
//        forcePathBetween(maze, start, goal);
//
//        return maze;
//    }
//
//    private Position getRandomBorderPosition(int rows, int columns) {
//        int side = random.nextInt(4); // 0: top, 1: bottom, 2: left, 3: right
//        int row, col;
//
//        switch (side) {
//            case 0:
//                row = 0;
//                col = random.nextInt(columns);
//                break;
//            case 1:
//                row = rows - 1;
//                col = random.nextInt(columns);
//                break;
//            case 2:
//                row = random.nextInt(rows);
//                col = 0;
//                break;
//            case 3:
//                row = random.nextInt(rows);
//                col = columns - 1;
//                break;
//            default:
//                row = 0;
//                col = 0;
//                break;
//        }
//        return new Position(row, col);
//    }
//
//    private List<Position> getUnvisitedNeighbors(Maze maze, Position pos) {
//        List<Position> neighbors = new ArrayList<>();
//        int r = pos.getRowIndex();
//        int c = pos.getColumnIndex();
//        int rows = maze.getRows();
//        int cols = maze.getColumns();
//
//        if (r > 1 && maze.getCell(r - 2, c) == 1) neighbors.add(new Position(r - 2, c));
//        if (r < rows - 2 && maze.getCell(r + 2, c) == 1) neighbors.add(new Position(r + 2, c));
//        if (c > 1 && maze.getCell(r, c - 2) == 1) neighbors.add(new Position(r, c - 2));
//        if (c < cols - 2 && maze.getCell(r, c + 2) == 1) neighbors.add(new Position(r, c + 2));
//
//        java.util.Collections.shuffle(neighbors, random);
//        return neighbors;
//    }
//
//    private void carvePath(Maze maze, Position from, Position to) {
//        int rowDiff = to.getRowIndex() - from.getRowIndex();
//        int colDiff = to.getColumnIndex() - from.getColumnIndex();
//
//        if (rowDiff == 2) maze.setCell(from.getRowIndex() + 1, from.getColumnIndex(), 0);
//        else if (rowDiff == -2) maze.setCell(from.getRowIndex() - 1, from.getColumnIndex(), 0);
//        else if (colDiff == 2) maze.setCell(from.getRowIndex(), from.getColumnIndex() + 1, 0);
//        else if (colDiff == -2) maze.setCell(from.getRowIndex(), from.getColumnIndex() - 1, 0);
//    }
//
//    private void forcePathBetween(Maze maze, Position start, Position end) {
//        int rows = maze.getRows();
//        int cols = maze.getColumns();
//        Queue<Position> queue = new LinkedList<>();
//        queue.offer(start);
//        boolean[][] visited = new boolean[rows][cols];
//        Position[][] parent = new Position[rows][cols];
//        visited[start.getRowIndex()][start.getColumnIndex()] = true;
//
//        while (!queue.isEmpty()) {
//            Position current = queue.poll();
//            if (current.equals(end)) {
//                break; // Path found
//            }
//
//            int r = current.getRowIndex();
//            int c = current.getColumnIndex();
//            int[] dr = {-1, 1, 0, 0};
//            int[] dc = {0, 0, -1, 1};
//
//            for (int i = 0; i < 4; i++) {
//                int nr = r + dr[i];
//                int nc = c + dc[i];
//
//                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !visited[nr][nc] && maze.getCell(nr, nc) == 0) {
//                    visited[nr][nc] = true;
//                    parent[nr][nc] = current;
//                    queue.offer(new Position(nr, nc));
//                }
//            }
//        }
//
//        // Backtrack to carve the path if found
//        Position current = end;
//        while (parent[current.getRowIndex()][current.getColumnIndex()] != null) {
//            Position prev = parent[current.getRowIndex()][current.getColumnIndex()];
//            carvePath(maze, prev, current); // Ensure the path is open
//            maze.setCell(current.getRowIndex(), current.getColumnIndex(), 0);
//            current = prev;
//        }
//    }
//}

package algorithms.mazeGenerators;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MyMazeGenerator extends AMazeGenerator {
    //private static final Random random = new Random();

//    @Override
//    public Maze generate(int rows, int columns) {
//        if (rows <= 0 || columns <= 0) {
//            throw new IllegalArgumentException("Rows and columns must be positive integers.");
//        }
//
//        Maze maze = new Maze(rows, columns);
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < columns; j++) {
//                maze.setCell(i, j, 1);
//            }
//        }
//
//        Position start = getRandomBorderPosition(rows, columns);
//        Position goal;
//        do {
//            goal = getRandomBorderPosition(rows, columns);
//        } while (goal.equals(start));
//        maze.setStartPosition(start);
//        maze.setGoalPosition(goal);
//
//        Stack<Position> stack = new Stack<>();
//        stack.push(start);
//        maze.setCell(start.getRowIndex(), start.getColumnIndex(), 0);
//
//        while (!stack.isEmpty()) {
//            Position current = stack.peek();
//            List<Position> unvisitedNeighbors = getUnvisitedNeighborsWithGoalBias(maze, current, goal);
//
//            if (!unvisitedNeighbors.isEmpty()) {
//                Position next = unvisitedNeighbors.get(random.nextInt(unvisitedNeighbors.size()));
//                carvePath(maze, current, next);
//                maze.setCell(next.getRowIndex(), next.getColumnIndex(), 0);
//                stack.push(next);
//            } else {
//                stack.pop();
//            }
//        }
//
//        maze.setCell(goal.getRowIndex(), goal.getColumnIndex(), 0); // Ensure goal is open
//
//        return maze;
//    }
@Override
public Maze generate(int rows, int columns) {
    if (rows <= 0 || columns <= 0)
        throw new IllegalArgumentException("Rows and columns must be positive.");

    /* ------------------------------------------------------------------
     * 1. Decide the working grid: always odd so the DFS never gets stuck
     * ------------------------------------------------------------------ */
    int workRows = (rows % 2 == 0) ? rows - 1 : rows;
    int workCols = (columns % 2 == 0) ? columns - 1 : columns;

    /* ------------------------------------------------------------------
     * 2. Build the odd‑sized maze with your existing algorithm
     *    (moved into a private helper so we can call it recursively)
     * ------------------------------------------------------------------ */
    Maze work = generateOddMaze(workRows, workCols);   // *see helper below*

    /* ------------------------------------------------------------------
     * 3. Copy the odd maze into the final maze of the requested size
     * ------------------------------------------------------------------ */
    Maze maze = new Maze(rows, columns);
    for (int r = 0; r < workRows; r++) {
        for (int c = 0; c < workCols; c++) {
            maze.setCell(r, c, work.getCell(r, c));
        }
    }

    java.util.Random rnd = new java.util.Random();

    /* ------------------------------------------------------------------
     * 4. If we had to add an extra bottom row, randomise it and connect it
     * ------------------------------------------------------------------ */
    if (rows % 2 == 0) {
        int lastRow = rows - 1;

        // 4a. random 0 / 1 across the whole bottom row
        for (int c = 0; c < columns; c++) {
            maze.setCell(lastRow, c, rnd.nextInt(2));
        }
        // 4b. punch a single doorway so the new row is reachable
        int doorCol = rnd.nextInt(workCols / 2) * 2 + 1; // choose an odd index
        maze.setCell(lastRow, doorCol, 0);
        maze.setCell(lastRow - 1, doorCol, 0);
    }

    /* ------------------------------------------------------------------
     * 5. If we had to add an extra right‑most column, randomise & connect
     * ------------------------------------------------------------------ */
    if (columns % 2 == 0) {
        int lastCol = columns - 1;

        for (int r = 0; r < rows; r++) {
            maze.setCell(r, lastCol, rnd.nextInt(2));
        }
        int doorRow = rnd.nextInt(workRows / 2) * 2 + 1; // choose an odd index
        maze.setCell(doorRow, lastCol, 0);
        maze.setCell(doorRow, lastCol - 1, 0);
    }

    /* ------------------------------------------------------------------
     * 6. Copy the start / goal (they were generated on the working grid)
     * ------------------------------------------------------------------ */
    maze.setStartPosition(work.getStartPosition());
    maze.setGoalPosition(work.getGoalPosition());

    return maze;
}

    /* ----------------------------------------------------------------------
     * Helper: exactly your original algorithm, but it *always* receives
     * odd dimensions so it never produces a solid border.
     * -------------------------------------------------------------------- */
    private Maze generateOddMaze(int rows, int columns) {
        Maze maze = new Maze(rows, columns);

    /* ----- everything from your original generate(…) method,
            except the first two if (rows <= 0 …) lines and
            the final return, stays here unchanged ------------------ */

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                maze.setCell(i, j, 1);

        Position start = getRandomBorderPosition(rows, columns);
        Position goal;
        do {
            goal = getRandomBorderPosition(rows, columns);
        } while (goal.equals(start));
        maze.setStartPosition(start);
        maze.setGoalPosition(goal);

        Stack<Position> stack = new Stack<>();
        stack.push(start);
        maze.setCell(start.getRowIndex(), start.getColumnIndex(), 0);

        while (!stack.isEmpty()) {
            Position current = stack.peek();
            List<Position> unvisited =
                    getUnvisitedNeighborsWithGoalBias(maze, current, goal);

            if (!unvisited.isEmpty()) {
                Position next = unvisited.get(random.nextInt(unvisited.size()));
                carvePath(maze, current, next);
                maze.setCell(next.getRowIndex(), next.getColumnIndex(), 0);
                stack.push(next);
            } else {
                stack.pop();
            }
        }
        maze.setCell(goal.getRowIndex(), goal.getColumnIndex(), 0);
        return maze;
    }

    private List<Position> getUnvisitedNeighbors(Maze maze, Position pos) {
        List<Position> neighbors = new ArrayList<>();
        int r = pos.getRowIndex();
        int c = pos.getColumnIndex();
        int rows = maze.getRows();
        int cols = maze.getColumns();

        if (r > 1 && maze.getCell(r - 2, c) == 1) neighbors.add(new Position(r - 2, c));
        if (r < rows - 2 && maze.getCell(r + 2, c) == 1) neighbors.add(new Position(r + 2, c));
        if (c > 1 && maze.getCell(r, c - 2) == 1) neighbors.add(new Position(r, c - 2));
        if (c < cols - 2 && maze.getCell(r, c + 2) == 1) neighbors.add(new Position(r, c + 2));

        return neighbors;
    }

    private List<Position> getUnvisitedNeighborsWithGoalBias(Maze maze, Position current, Position goal) {
        List<Position> neighbors = getUnvisitedNeighbors(maze, current);
        List<Position> biasedNeighbors = new ArrayList<>();

        for (Position neighbor : neighbors) {
            // Introduce a slight bias towards neighbors that reduce the Manhattan distance to the goal
            int currentDistance = Math.abs(current.getRowIndex() - goal.getRowIndex()) + Math.abs(current.getColumnIndex() - goal.getColumnIndex());
            int neighborDistance = Math.abs(neighbor.getRowIndex() - goal.getRowIndex()) + Math.abs(neighbor.getColumnIndex() - goal.getColumnIndex());

            if (neighborDistance < currentDistance || random.nextDouble() < 0.5) { // Adjust probability
                biasedNeighbors.add(neighbor);
            } else {
                biasedNeighbors.add(neighbor); // Still allow other directions to maintain complexity
            }
        }
        java.util.Collections.shuffle(biasedNeighbors, random);
        return biasedNeighbors;
    }

    private void carvePath(Maze maze, Position from, Position to) {
        int rowDiff = to.getRowIndex() - from.getRowIndex();
        int colDiff = to.getColumnIndex() - from.getColumnIndex();

        if (rowDiff == 2) maze.setCell(from.getRowIndex() + 1, from.getColumnIndex(), 0);
        else if (rowDiff == -2) maze.setCell(from.getRowIndex() - 1, from.getColumnIndex(), 0);
        else if (colDiff == 2) maze.setCell(from.getRowIndex(), from.getColumnIndex() + 1, 0);
        else if (colDiff == -2) maze.setCell(from.getRowIndex(), from.getColumnIndex() - 1, 0);
    }
}