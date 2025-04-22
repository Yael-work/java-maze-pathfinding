package algorithms.search;

import algorithms.mazeGenerators.Position;
import java.util.*;

/**
 * BestFirstSearch is a search algorithm that uses a heuristic to guide the search.
 * It evaluates nodes based on their cost and heuristic value,
 * prioritizing those that are expected to lead to the goal fastest.
 */
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

        // Queue ordered by f(n) = g(n) + h(n)
        PriorityQueue<AState> open = new PriorityQueue<>(new StateComparator());
        Set<AState> closed = new HashSet<>(); // track visited states

        open.add(start);
        start.setCost(0); // initialize start cost

        while (!open.isEmpty()) {
            AState current = open.poll(); // get state with lowest f(n)
            evaluated++;

            if (current.equals(goal)) {
                return Solution.reconstruct(current); // goal reached, build path
            }

            closed.add(current); // mark as visited

            for (AState neighbor : domain.getPossibleStates(current)) {
                if (closed.contains(neighbor)) continue; // skip visited

                // Calculate new cost = current cost + step cost
                double stepCost = calculateStepCost(current, neighbor);
                double tentativeCost = current.getCost() + stepCost;

                // If new path is better
                if (tentativeCost < neighbor.getCost()) {
                    neighbor.setCameFrom(current); // track path
                    neighbor.setCost(tentativeCost); // update cost

                    // Add to open set if not already there
                    if (!open.contains(neighbor)) {
                        open.add(neighbor);
                    } else {
                        // Force priority queue to re-sort
                        open.remove(neighbor);
                        open.add(neighbor);
                    }
                }
            }
        }

        return new Solution(new ArrayList<>()); // return empty if no path
    }

    private double calculateStepCost(AState current, AState neighbor) {
        MazeState a = (MazeState) current;
        MazeState b = (MazeState) neighbor;

        int dr = Math.abs(a.getPosition().getRowIndex() - b.getPosition().getRowIndex());
        int dc = Math.abs(a.getPosition().getColumnIndex() - b.getPosition().getColumnIndex());

        // Diagonal movement costs 15, straight movement costs 10
        return (dr + dc == 2) ? 15.0 : 10.0;
    }

    // Custom comparator that considers both cost and heuristic
    private class StateComparator implements Comparator<AState> {
        @Override
        public int compare(AState a, AState b) {
            double fa = a.getCost() + heuristic(a);
            double fb = b.getCost() + heuristic(b);
            return Double.compare(fa, fb);
        }
    }

    private double heuristic(AState state) { // manhattan distance to goal
        Position goalPos = ((MazeState) currentDomain.getGoalState()).getPosition();
        Position curPos = ((MazeState) state).getPosition();

        return Math.abs(goalPos.getRowIndex() - curPos.getRowIndex()) +
                Math.abs(goalPos.getColumnIndex() - curPos.getColumnIndex());
    }

    private ISearchable currentDomain; // needed for heuristic access
}


