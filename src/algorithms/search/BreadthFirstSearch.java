package algorithms.search;

import java.util.*;
 /**
  * Implements the Breadth-First Search (BFS) algorithm for searching a solution in a given domain.
  * This algorithm explores all possible states level by level, ensuring the shortest path is found.
  * It uses a queue to manage the states to be explored and a set to track visited states.
  * should be more efficient than BestFirstSearch.
  */
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
        start.setCost(0); // sets the cost to 0
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
