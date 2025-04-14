package algorithms.mazeGenerators;

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