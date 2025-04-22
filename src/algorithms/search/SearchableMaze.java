package algorithms.search;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * An adapter class that wraps a {@link Maze} object and exposes it as an
 * {@link ISearchable} domain, allowing search algorithms to operate on mazes.
 * <p>
 * Each maze cell is treated as a state in the search space, with allowed
 * transitions representing valid moves between cells.
 * </p>
 */

public class SearchableMaze implements ISearchable {

    private final Maze maze;                     // the underlying maze
    private final MazeState[][] states;          // grid of MazeState (null for walls)
    private final MazeState start;               // start state
    private final MazeState goal;                // goal state

    /* 8 directions with move costs: 10 for straight, 15 for diagonal */
    private static final int[][] directional_Arr = {
            { 1, 0, 10 }, { -1, 0, 10 }, { 0, 1, 10 }, { 0, -1, 10 },
            { 1, 1, 15 }, { 1, -1, 15 }, { -1, 1, 15 }, { -1, -1, 15 }
    };


    public SearchableMaze(Maze maze) {
        this.maze = maze;
        int rows  = maze.getRows();
        int cols  = maze.getColumns();

        states = new MazeState[rows][cols]; // create grid

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (maze.getCell(r, c) == 0)
                    states[r][c] = new MazeState(new Position(r, c)); // init non-wall
            }
        }

        start = states[maze.getStartPosition().getRowIndex()]
                [maze.getStartPosition().getColumnIndex()];
        goal  = states[maze.getGoalPosition().getRowIndex()]
                [maze.getGoalPosition().getColumnIndex()];
    }

    /* ---------- ISearchable ---------- */

    @Override
    public AState getStartState() { return start; }

    @Override
    public AState getGoalState()  { return goal; }

    @Override
    public List<AState> getPossibleStates(AState s) {
        MazeState ms = (MazeState) s;
        int r = ms.getPosition().getRowIndex();
        int c = ms.getPosition().getColumnIndex();

        List<AState> out = new ArrayList<>(8); // up to 8 neighbors

        for (int[] d : directional_Arr) {
            int nr = r + d[0], nc = c + d[1];
            if (nr < 0 || nc < 0 || nr >= states.length || nc >= states[0].length)
                continue; // skip out of bounds

            MazeState neigh = states[nr][nc];
            if (neigh == null) continue; // skip walls

            if (Math.abs(d[0]) + Math.abs(d[1]) == 2) { // diagonal check
                if (maze.getCell(r, nc) == 1 || maze.getCell(nr, c) == 1) continue;
            }

            out.add(neigh); // valid neighbor
        }

        return out;
    }

    /* ---------- reset before each new search ---------- */

    public void resetStates() {
        for (MazeState[] row : states)
            for (MazeState m : row)
                if (m != null) m.reset(); // clear cameFrom and cost
        start.setCost(0); // initialize start cost
    }
}
