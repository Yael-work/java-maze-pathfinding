package algorithms.search;

import algorithms.mazeGenerators.Position;
import java.util.*;

public class BestFirstSearch extends ASearchingAlgorithm {

    @Override
    public String getName() {
        return "BestFirstSearch";
    }

    @Override
    public Solution solve(ISearchable domain) {
        this.currentDomain = domain; // store reference for heuristic use

        if (domain instanceof SearchableMaze) {
            ((SearchableMaze) domain).resetStates(); // reset visited/cost for reuse
        }

        AState start = domain.getStartState();
        AState goal  = domain.getGoalState();

        PriorityQueue<AState> open = new PriorityQueue<>(Comparator.comparingDouble(this::heuristic)); // queue by h(n)
        Set<AState> closed = new HashSet<>(); // track visited states

        open.add(start);
        start.setCost(0); // optional init, not used in heuristic-only

        while (!open.isEmpty()) {
            AState current = open.poll(); // get state with lowest h(n)
            evaluated++;

            if (current.equals(goal)) {
                return Solution.reconstruct(current); // goal reached, build path
            }

            closed.add(current); // mark as visited

            for (AState neighbor : domain.getPossibleStates(current)) {
                if (closed.contains(neighbor)) continue; // skip visited

                if (!open.contains(neighbor)) {
                    neighbor.setCameFrom(current); // track path
                    open.add(neighbor); // enqueue by heuristic
                }
            }
        }

        return new Solution(new ArrayList<>()); // return empty if no path
    }

    private double heuristic(AState state) { // manhattan distance to goal
        Position goalPos = ((MazeState) currentDomain.getGoalState()).getPosition();
        Position curPos = ((MazeState) state).getPosition();

        return Math.abs(goalPos.getRowIndex() - curPos.getRowIndex()) +
                Math.abs(goalPos.getColumnIndex() - curPos.getColumnIndex());
    }

    private ISearchable currentDomain; // needed for heuristic access
}
