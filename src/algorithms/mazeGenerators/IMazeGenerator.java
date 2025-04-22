package algorithms.mazeGenerators;

public interface IMazeGenerator {
    /**
     * This function generates a maze with the given dimensions.
     * @param rows    The number of rows in the maze.
     * @param columns The number of columns in the maze.
     * @return A new Maze object with the specified dimensions.
     */
    Maze generate(int rows, int columns);

    /**
     * This function measures the time it takes to generate a maze with the given dimensions.
     * @param rows    The number of rows in the maze.
     * @param columns The number of columns in the maze.
     * @return The time taken to generate the maze in milliseconds.
     */
    long measureAlgorithmTimeMillis(int rows, int columns);
}
