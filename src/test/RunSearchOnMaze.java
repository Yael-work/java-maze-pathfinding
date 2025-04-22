package test;
import algorithms.mazeGenerators.*;
import algorithms.search.*;
import java.util.ArrayList;
public class RunSearchOnMaze {
    /**
     * Demonstrates running various search algorithms on a randomly generated maze.
     * <p>
     * This class generates a 30x30 maze using {@link MyMazeGenerator}, converts it into
     * a {@link SearchableMaze}, and solves it using three search strategies:
     * {@link BreadthFirstSearch}, {@link DepthFirstSearch}, and {@link BestFirstSearch}.
     * </p>
     *
     * Intended for testing and comparing search algorithms based on pathfinding
     * performance and number of evaluated nodes.
     */

    public static void main(String[] args) {
        IMazeGenerator mg = new MyMazeGenerator();
        Maze maze = mg.generate(30, 30);
        SearchableMaze searchableMaze = new SearchableMaze(maze);
        solveProblem(searchableMaze, new BreadthFirstSearch());
        solveProblem(searchableMaze, new DepthFirstSearch());
        solveProblem(searchableMaze, new BestFirstSearch());
    }
    private static void solveProblem(ISearchable domain, ISearchingAlgorithm
            searcher) {
//Solve a searching problem with a searcher
        Solution solution = searcher.solve(domain);
        System.out.println(String.format("'%s' algorithm - nodes evaluated: %s", searcher.getName(), searcher.getNumberOfNodesEvaluated()));
//Printing Solution Path
                System.out.println("Solution path:");
        ArrayList<AState> solutionPath = solution.getSolutionPath();
        for (int i = 0; i < solutionPath.size(); i++) {
                System.out.println(String.format("%s. %s", i, solutionPath.get(i)));
        }
    }
}