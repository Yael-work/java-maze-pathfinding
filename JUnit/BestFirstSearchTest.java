import algorithms.mazeGenerators.*;
import algorithms.search.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class BestFirstSearchTest {
    private static final List<IMazeGenerator> GENERATORS = Arrays.asList(
            new MyMazeGenerator(),
            new SimpleMazeGenerator(),
            new EmptyMazeGenerator()
    );

    private IMazeGenerator currentGenerator;

    @BeforeEach
    public void setup(TestInfo testInfo) {
        System.out.println("Running test: " + testInfo.getDisplayName());
    }

    @TestFactory
    @DisplayName("Run all tests on all maze generators")
    public List<DynamicTest> runTestsOnAllGenerators() {
        List<DynamicTest> allTests = new ArrayList<>();

        for (IMazeGenerator generator : GENERATORS) {
            this.currentGenerator = generator;

            allTests.add(DynamicTest.dynamicTest("Null domain throws Null pointer exception", this::nullDomain));
            allTests.add(DynamicTest.dynamicTest("1x1 maze trivial path", this::trivialMaze));
            allTests.add(DynamicTest.dynamicTest("Path begins at start and ends at goal", this::testEndpoints));
            allTests.add(DynamicTest.dynamicTest("No duplicate states in path", this::testNoDuplicates));
            allTests.add(DynamicTest.dynamicTest("Blocked maze returns empty path", this::testNoRoute));
            allTests.add(DynamicTest.dynamicTest("Thin corridor performance", this::testThinCorridor));
            allTests.add(DynamicTest.dynamicTest("Repeated solves are consistent", this::testRepeatedSolves));
            allTests.add(DynamicTest.dynamicTest("Diagonal preferred over two straights", this::testDiagonalPreferred));
            allTests.add(DynamicTest.dynamicTest("Corner-blocked diagonal disallowed", this::testDiagonalBlockedByWalls));
            allTests.add(DynamicTest.dynamicTest("Straight-line cost test", this::testSymmetricRoutesCost));
            allTests.add(DynamicTest.dynamicTest("Monotone cost check", this::testMonotoneCost));
            allTests.add(DynamicTest.dynamicTest("Large maze performance (simple check)", this::testLargeMazePerformance));
        }

        // Additional robustness tests
        allTests.add(DynamicTest.dynamicTest("Negative maze dimensions", this::testNegativeMazeDimensions));
        allTests.add(DynamicTest.dynamicTest("Null start or goal position", this::testNullStartOrGoalPosition));
        allTests.add(DynamicTest.dynamicTest("Invalid maze cell access", this::testInvalidCellAccess));
        allTests.add(DynamicTest.dynamicTest("Maze.setCell out of bounds", this::testMazeSetCellOutOfBounds));
        allTests.add(DynamicTest.dynamicTest("Maze.getCell out of bounds", this::testMazeGetCellOutOfBounds));
        allTests.add(DynamicTest.dynamicTest("Maze with null grid", this::testMazeWithNullGrid));
        allTests.add(DynamicTest.dynamicTest("Null MazeState position", this::testNullMazeStatePosition));
        allTests.add(DynamicTest.dynamicTest("SearchableMaze with null", this::testSearchableMazeWithNull));
        allTests.add(DynamicTest.dynamicTest("SearchableMaze start/goal on wall", this::testSearchableMazeStartGoalOnWall));
        allTests.add(DynamicTest.dynamicTest("Unreachable goal returns empty path", this::testUnreachableGoal));
        allTests.add(DynamicTest.dynamicTest("Reconstruct with null throws", this::testReconstructNull));

        return allTests;
    }

    public void nullDomain() {
        BestFirstSearch bfs = new BestFirstSearch();
        assertThrows(NullPointerException.class, () -> bfs.solve(null));
    }

    public void trivialMaze() {
        Maze maze = new Maze(1, 1);
        maze.setStartPosition(new Position(0, 0));
        maze.setGoalPosition(new Position(0, 0));
        SearchableMaze sm = new SearchableMaze(maze);
        sm.resetStates();
        BestFirstSearch bfs = new BestFirstSearch();
        Solution sol = bfs.solve(sm);
        List<AState> path = sol.getSolutionPath();
        assertEquals(1, path.size());
        assertEquals(new Position(0, 0), ((MazeState) path.get(0)).getPosition());
    }

    public void testEndpoints() {
        Maze m = currentGenerator.generate(20, 20);
        SearchableMaze sm = new SearchableMaze(m);
        sm.resetStates();
        BestFirstSearch bfs = new BestFirstSearch();
        List<AState> path = bfs.solve(sm).getSolutionPath();
        assertFalse(path.isEmpty());
        assertEquals(m.getStartPosition(), ((MazeState) path.get(0)).getPosition());
        assertEquals(m.getGoalPosition(), ((MazeState) path.get(path.size() - 1)).getPosition());
    }

    public void testNoDuplicates() {
        Maze m = currentGenerator.generate(30, 30);
        SearchableMaze sm = new SearchableMaze(m);
        sm.resetStates();
        BestFirstSearch bfs = new BestFirstSearch();
        List<AState> path = bfs.solve(sm).getSolutionPath();
        Set<Position> seen = new HashSet<>();
        for (AState s : path) {
            Position p = ((MazeState) s).getPosition();
            assertTrue(seen.add(p));
        }
    }

    public void testNoRoute() {
        int size = 10;
        int[][] grid = new int[size][size];
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                grid[r][c] = 1;
        grid[0][0] = 0;
        grid[size - 1][size - 1] = 0;
        Maze m = new Maze(grid, new Position(0, 0), new Position(size - 1, size - 1));
        SearchableMaze sm = new SearchableMaze(m);
        sm.resetStates();
        BestFirstSearch bfs = new BestFirstSearch();
        assertTrue(bfs.solve(sm).getSolutionPath().isEmpty());
    }

    public void testThinCorridor() {
        int len = 100;
        Maze m = new Maze(1, len);
        for (int i = 0; i < len; i++) m.setCell(0, i, 0);
        m.setStartPosition(new Position(0, 0));
        m.setGoalPosition(new Position(0, len - 1));

        SearchableMaze sm = new SearchableMaze(m);
        sm.resetStates();

        BestFirstSearch bfs = new BestFirstSearch();
        long start = System.currentTimeMillis();
        List<AState> path = bfs.solve(sm).getSolutionPath();
        long duration = System.currentTimeMillis() - start;

        assertTrue(duration < 1000);
        assertEquals(len, path.size());
    }

    public void testRepeatedSolves() {
        Maze m = currentGenerator.generate(25, 25);
        SearchableMaze sm = new SearchableMaze(m);
        sm.resetStates();
        BestFirstSearch bfs = new BestFirstSearch();
        List<AState> p1 = bfs.solve(sm).getSolutionPath();
        sm.resetStates();
        List<AState> p2 = bfs.solve(sm).getSolutionPath();
        assertEquals(p1.size(), p2.size());
        for (int i = 0; i < p1.size(); i++)
            assertEquals(((MazeState) p1.get(i)).getPosition(), ((MazeState) p2.get(i)).getPosition());
    }

    public void testDiagonalPreferred() {
        Maze m = new Maze(3, 3);
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                m.setCell(r, c, 0);
        m.setStartPosition(new Position(0, 0));
        m.setGoalPosition(new Position(2, 2));

        SearchableMaze sm = new SearchableMaze(m);
        sm.resetStates();
        BestFirstSearch bfs = new BestFirstSearch();
        Solution sol = bfs.solve(sm);

        MazeState goal = (MazeState) sm.getGoalState();
        assertEquals(30.0, goal.getCost());
        assertEquals(3, sol.getSolutionPath().size());
    }

    public void testDiagonalBlockedByWalls() {
        Maze m = new Maze(3, 3);
        m.setCell(0, 1, 1);
        m.setCell(1, 0, 1);
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                if (m.getCell(r, c) != 1) m.setCell(r, c, 0);
        m.setStartPosition(new Position(0, 0));
        m.setGoalPosition(new Position(1, 1));
        SearchableMaze sm = new SearchableMaze(m);
        sm.resetStates();
        BestFirstSearch bfs = new BestFirstSearch();
        assertTrue(bfs.solve(sm).getSolutionPath().isEmpty());
    }

    public void testSymmetricRoutesCost() {
        Maze m = new Maze(1, 5);
        for (int i = 0; i < 5; i++) m.setCell(0, i, 0);
        m.setStartPosition(new Position(0, 0));
        m.setGoalPosition(new Position(0, 4));

        SearchableMaze sm = new SearchableMaze(m);
        sm.resetStates();
        BestFirstSearch bfs = new BestFirstSearch();
        Solution sol = bfs.solve(sm);

        MazeState goal = (MazeState) sm.getGoalState();
        assertEquals(40.0, goal.getCost());
        assertEquals(5, sol.getSolutionPath().size());
    }

    public void testMonotoneCost() {
        Maze m = currentGenerator.generate(20, 20);
        SearchableMaze sm = new SearchableMaze(m);
        sm.resetStates();
        BestFirstSearch bfs = new BestFirstSearch();
        List<AState> path = bfs.solve(sm).getSolutionPath();
        double prev = -Double.MAX_VALUE;
        for (AState s : path) {
            assertTrue(s.getCost() >= prev);
            prev = s.getCost();
        }
    }

    public void testLargeMazePerformance() {
        Maze m = currentGenerator.generate(1000, 1000);
        SearchableMaze sm = new SearchableMaze(m);
        sm.resetStates();
        BestFirstSearch bfs = new BestFirstSearch();
        long start = System.currentTimeMillis();
        bfs.solve(sm);
        long duration = System.currentTimeMillis() - start;
        assertTrue(duration < 60000);
    }

    public void testNegativeMazeDimensions() {
        assertThrows(IllegalArgumentException.class, () -> new Maze(-5, 5));
        assertThrows(IllegalArgumentException.class, () -> new Maze(5, -5));
        assertThrows(IllegalArgumentException.class, () -> new Maze(-1, -1));
    }

    public void testNullStartOrGoalPosition() {
        int[][] grid = {{0, 0}, {0, 0}};
        assertThrows(NullPointerException.class, () -> new Maze(grid, null, new Position(1, 1)));
        assertThrows(NullPointerException.class, () -> new Maze(grid, new Position(0, 0), null));
    }

    public void testInvalidCellAccess() {
        Maze m = new Maze(5, 5);
        assertThrows(IndexOutOfBoundsException.class, () -> m.setCell(-1, 0, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> m.setCell(0, -1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> m.setCell(5, 0, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> m.setCell(0, 5, 0));
    }
    public void testMazeSetCellOutOfBounds() {
        Maze maze = new Maze(5, 5);
        assertThrows(IndexOutOfBoundsException.class, () -> maze.setCell(5, 0, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> maze.setCell(-1, 0, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> maze.setCell(0, 5, 1));
    }

    public void testMazeGetCellOutOfBounds() {
        Maze maze = new Maze(5, 5);
        assertThrows(IndexOutOfBoundsException.class, () -> maze.getCell(10, 10));
    }

    public void testMazeWithNullGrid() {
        assertThrows(NullPointerException.class, () -> new Maze(null, new Position(0, 0), new Position(1, 1)));
    }

    public void testNullMazeStatePosition() {

        assertThrows(NullPointerException.class, () -> new MazeState(null));
    }


    public void testSearchableMazeWithNull() {
        assertThrows(NullPointerException.class, () -> new SearchableMaze(null));
    }


    public void testSearchableMazeStartGoalOnWall() {
        Maze m = new Maze(3, 3);
        m.setCell(0, 0, 1);  // Make start a wall
        m.setStartPosition(new Position(0, 0));
        m.setGoalPosition(new Position(2, 2));
        assertDoesNotThrow(() -> new SearchableMaze(m));
    }


    public void testUnreachableGoal() {
        Maze m = new Maze(3, 3);
        for (int r = 0; r < 3; r++) for (int c = 0; c < 3; c++) m.setCell(r, c, 1);
        m.setCell(0, 0, 0);
        m.setStartPosition(new Position(0, 0));
        m.setGoalPosition(new Position(2, 2));
        SearchableMaze sm = new SearchableMaze(m);
        BestFirstSearch bfs = new BestFirstSearch();
        assertTrue(bfs.solve(sm).getSolutionPath().isEmpty());
    }

    public void testReconstructNull() {
        assertThrows(NullPointerException.class, () -> Solution.reconstruct(null));
    }

}

