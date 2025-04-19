package algorithms.mazeGenerators;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MyMazeGenerator extends AMazeGenerator {
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