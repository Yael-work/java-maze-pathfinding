package algorithms.mazeGenerators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class MyMazeGenerator extends AMazeGenerator {
    @Override
    public Maze generate(int rows, int columns) {
        if (rows <= 0 || columns <= 0)
            throw new IllegalArgumentException("Rows and columns must be positive.");

        int workRows = (rows % 2 == 0) ? rows - 1 : rows;
        int workCols = (columns % 2 == 0) ? columns - 1 : columns;

        Maze work = generateOddMaze(workRows, workCols);

        Maze maze = new Maze(rows, columns);
        for (int r = 0; r < Math.min(rows, workRows); r++) {
            for (int c = 0; c < Math.min(columns, workCols); c++) {
                maze.setCell(r, c, work.getCell(r, c));
            }
        }

        Random rnd = new Random();

        if (rows % 2 == 0) {
            int lastRow = rows - 1;
            for (int c = 0; c < columns; c++) maze.setCell(lastRow, c, rnd.nextInt(2));
            int doorCol = rnd.nextInt(workCols / 2) * 2 + 1;
            if (doorCol < columns) {
                maze.setCell(lastRow, doorCol, 0);
                if (lastRow > 0 && doorCol < workCols) maze.setCell(lastRow - 1, doorCol, 0);
            } else if (columns > 0) {
                maze.setCell(lastRow, columns - 1, 0);
                if (lastRow > 0 && columns - 1 < workCols) maze.setCell(lastRow - 1, columns - 1, 0);
            }
        }
        if (columns % 2 == 0) {
            int lastCol = columns - 1;
            for (int r = 0; r < rows; r++) maze.setCell(r, lastCol, rnd.nextInt(2));
            int doorRow = rnd.nextInt(workRows / 2) * 2 + 1;
            if (doorRow < rows) {
                maze.setCell(doorRow, lastCol, 0);
                if (lastCol > 0 && doorRow < workRows) maze.setCell(doorRow, lastCol - 1, 0);
            } else if (rows > 0) {
                maze.setCell(rows - 1, lastCol, 0);
                if (lastCol > 0 && rows - 1 < workCols) maze.setCell(rows - 1, lastCol - 1, 0);
            }
        }

        Position start, goal;
        if (rows == 1) {
            start = new Position(0, 0);
            goal = new Position(0, columns - 1);
            maze.setCell(0, 0, 0);
            maze.setCell(0, columns - 1, 0);
        } else if (columns == 1) {
            start = new Position(0, 0);
            goal = new Position(rows - 1, 0);
            maze.setCell(0, 0, 0);
            maze.setCell(rows - 1, 0, 0);
        } else {
            List<Position> borderCells = new ArrayList<>();
            for (int c = 0; c < columns; c++) {
                borderCells.add(new Position(0, c));
                if (rows > 1) borderCells.add(new Position(rows - 1, c));
            }
            for (int r = 1; r < rows - 1; r++) {
                borderCells.add(new Position(r, 0));
                if (columns > 1) borderCells.add(new Position(r, columns - 1));
            }

            List<Position> connectedBorderCells = new ArrayList<>();
            for (Position borderCell : borderCells) {
                int r = borderCell.getRowIndex();
                int c = borderCell.getColumnIndex();
                if (r < workRows && c < workCols && maze.getCell(r, c) == 0) {
                    connectedBorderCells.add(borderCell);
                } else if (rows % 2 == 0 && r == rows - 1 && c < workCols && maze.getCell(r - 1, c) == 0) {
                    connectedBorderCells.add(borderCell);
                } else if (columns % 2 == 0 && c == columns - 1 && r < workRows && maze.getCell(r, c - 1) == 0) {
                    connectedBorderCells.add(borderCell);
                } else if (rows % 2 == 0 && r == rows - 1 && columns % 2 == 0 && c == columns - 1 &&
                        (maze.getCell(r - 1, c) == 0 || maze.getCell(r, c - 1) == 0)) {
                    connectedBorderCells.add(borderCell);
                }
            }

            if (!connectedBorderCells.isEmpty()) {
                int startIndex = rnd.nextInt(connectedBorderCells.size());
                start = connectedBorderCells.get(startIndex);
                int goalIndex;
                do {
                    goalIndex = rnd.nextInt(connectedBorderCells.size());
                } while (goalIndex == startIndex);
                goal = connectedBorderCells.get(goalIndex);
            } else {
                // Fallback to the original way of getting start and goal from the work maze
                start = work.getStartPosition();
                goal = work.getGoalPosition();
                // Ensure they are within the bounds of the final maze
                start = new Position(Math.min(start.getRowIndex(), rows - 1), Math.min(start.getColumnIndex(), columns - 1));
                goal = new Position(Math.min(goal.getRowIndex(), rows - 1), Math.min(goal.getColumnIndex(), columns - 1));
            }
            // Ensure the chosen start and goal are on the border of the final maze
            if (rows > 1 && columns > 1) {
                if (!isBorder(start, rows, columns)) {
                    start = findNearestBorder(maze, start, rows, columns);
                }
                if (!isBorder(goal, rows, columns) || goal.equals(start)) {
                    goal = findNearestBorder(maze, goal, rows, columns, start);
                }
            } else if (rows == 1) {
                start = new Position(0, 0);
                goal = new Position(0, columns - 1);
            } else if (columns == 1) {
                start = new Position(0, 0);
                goal = new Position(rows - 1, 0);
            }
            // Ensure the start and goal cells themselves are open
            maze.setCell(start.getRowIndex(), start.getColumnIndex(), 0);
            maze.setCell(goal.getRowIndex(), goal.getColumnIndex(), 0);
        }

        maze.setStartPosition(start);
        maze.setGoalPosition(goal);

        return maze;
    }

    private boolean isBorder(Position p, int rows, int columns) {
        return p.getRowIndex() == 0 || p.getRowIndex() == rows - 1 || p.getColumnIndex() == 0 || p.getColumnIndex() == columns - 1;
    }

    private Position findNearestBorder(Maze maze, Position current, int rows, int cols) {
        return findNearestBorder(maze, current, rows, cols, null);
    }

    private Position findNearestBorder(Maze maze, Position current, int rows, int cols, Position avoid) {
        // Simple approach: move towards the nearest border
        int r = current.getRowIndex();
        int c = current.getColumnIndex();
        Position best = current;
        int minDist = Integer.MAX_VALUE;

        List<Position> potentialBorders = new ArrayList<>();
        potentialBorders.add(new Position(0, c));
        potentialBorders.add(new Position(rows - 1, c));
        potentialBorders.add(new Position(r, 0));
        potentialBorders.add(new Position(r, cols - 1));

        for (Position border : potentialBorders) {
            if (avoid != null && border.equals(avoid)) continue;
            int dist = Math.abs(r - border.getRowIndex()) + Math.abs(c - border.getColumnIndex());
            if (dist < minDist) {
                minDist = dist;
                best = border;
            }
        }
        return best;
    }


    private Maze generateOddMaze(int rows, int columns) {
        Maze maze = new Maze(rows, columns);
        for (int i = 0; i < rows; i++) for (int j = 0; j < columns; j++) maze.setCell(i, j, 1);
        Position start = getRandomBorderPosition(rows, columns);
        Position goal;
        do { goal = getRandomBorderPosition(rows, columns); } while (goal.equals(start));
        maze.setStartPosition(start);
        maze.setGoalPosition(goal);
        Stack<Position> stack = new Stack<>();
        stack.push(start);
        maze.setCell(start.getRowIndex(), start.getColumnIndex(), 0);
        while (!stack.isEmpty()) {
            Position current = stack.peek();
            List<Position> unvisited = getUnvisitedNeighborsWithGoalBias(maze, current, goal);
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
            int currentDistance = Math.abs(current.getRowIndex() - goal.getRowIndex()) + Math.abs(current.getColumnIndex() - goal.getColumnIndex());
            int neighborDistance = Math.abs(neighbor.getRowIndex() - goal.getRowIndex()) + Math.abs(neighbor.getColumnIndex() - goal.getColumnIndex());
            if (neighborDistance < currentDistance || random.nextDouble() < 0.5) biasedNeighbors.add(neighbor);
            else biasedNeighbors.add(neighbor);
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