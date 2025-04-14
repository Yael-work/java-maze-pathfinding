package algorithms.search;

import java.util.*;

public class BreadthFirstSearch extends ASearchingAlgorithm {

    @Override
    public String getName() {
        return "BreadthFirstSearch";
    }

    @Override
    public Solution solve(ISearchable domain) {
        this.currentDomain = domain; // store domain reference

        if (domain instanceof SearchableMaze) {
            ((SearchableMaze) domain).resetStates(); // reset states before search
        }

        Queue<AState> open = new ArrayDeque<>(); // fifo queue for bfs
        Set<AState> visited = new HashSet<>(); // track visited nodes

        AState start = domain.getStartState();
        AState goal  = domain.getGoalState();

        open.add(start);     // enqueue start state
        visited.add(start);  // mark start as visited

        while (!open.isEmpty()) {
            AState cur = open.poll(); // dequeue front node
            evaluated++;

            if (cur.equals(goal)) {
                return Solution.reconstruct(cur); // goal found, build path
            }

            for (AState neighbor : domain.getPossibleStates(cur)) {
                if (visited.add(neighbor)) { // add if not visited yet
                    neighbor.setCameFrom(cur); // track where it came from
                    open.add(neighbor); // enqueue for further exploration
                }
            }
        }

        return new Solution(new ArrayList<>()); // no path found
    }
}
