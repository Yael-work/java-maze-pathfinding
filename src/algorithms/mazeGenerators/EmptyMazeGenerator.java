package algorithms.mazeGenerators;
/**
 * This class generates an empty maze with a start and goal position.
 * The maze is represented as a grid of cells, where each cell is either a wall or a path.
 * In this case, the maze is empty, meaning all cells are paths.
 */
public class EmptyMazeGenerator extends AMazeGenerator {
    @Override
    public Maze generate(int rows, int columns){
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException("Rows and columns must be positive integers.");
        }
        Maze maze = new Maze(rows,columns);

        Position start = getRandomBorderPosition(rows, columns);
        Position goal;
        do {
            goal = getRandomBorderPosition(rows, columns);
        } while (goal.equals(start));
        maze.setStartPosition(start);
        maze.setGoalPosition(goal);

        return maze;
    }


}