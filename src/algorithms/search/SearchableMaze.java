package algorithms.search;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;

import java.util.ArrayList;
import java.util.List;

/** Adapter turning a Maze into an ISearchable domain. */
public class SearchableMaze implements ISearchable {
    private final Maze maze;
    private final MazeState start;
    private final MazeState goal;

    private static final int[][] directional_Arr = { // {Row , Col ,Cost}
            { 1, 0, 10 }, { -1, 0, 10 }, { 0, 1, 10 }, { 0, -1, 10 }, // straight
            { 1, 1, 15 }, { 1, -1, 15 }, { -1, 1, 15 }, { -1, -1, 15 } // diagonal
    };

    public SearchableMaze(Maze maze) {
        this.maze = maze;
        this.start = new MazeState(maze.getStartPosition(), 0);
        this.goal = new MazeState(maze.getGoalPosition(), 0);
    }

    @Override public AState getStartState() { return start; }
    @Override public AState getGoalState() { return goal; }

    @Override public List<AState> getPossibleStates(AState state) {
        MazeState ms = (MazeState) state; // cast to MazeState
        Position p = ms.getPosition(); // get position
        int r = p.getRowIndex();
        int c = p.getColumnIndex();
        int rows = maze.getRows();
        int cols = maze.getColumns();
        List<AState> list = new ArrayList<>(8); // preallocate for 8 directions

        for ( int i = 0 ; i < directional_Arr.length ; i ++){// iterate over directions
            int[] d = directional_Arr[i];
            int nr = r + d[0]; // new row
            int nc = c + d[1]; // new column
            if (nr < 0 || nc < 0 || nr >= rows || nc >= cols) continue; // out of bounds
            if (maze.getCell(nr, nc) == 1) continue; // wall

            // diagonal legality: both orthogonal neighbors must be free
            if (Math.abs(d[0]) + Math.abs(d[1]) == 2) { // check if move id diagonal
                if (maze.getCell(r, nc) == 1 || maze.getCell(nr, c) == 1) continue; // prevent diagonal move if one of the orthogonal neighbors is a wall
            }
            // found a valid neighbor at (nr, nc)
            MazeState neighbor = new MazeState(new Position(nr, nc), ms.getCost() + d[2]);
            list.add(neighbor);
        }
        return list;
    }

}