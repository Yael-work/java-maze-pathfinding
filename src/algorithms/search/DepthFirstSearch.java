package algorithms.search;

import java.util.*;

/**
 * This class implements the Depth First Search (DFS) algorithm for solving search problems.
 * It extends the ASearchingAlgorithm class and provides a specific implementation
 * for the solve method.
 */
public class DepthFirstSearch extends ASearchingAlgorithm {

    @Override
    public Solution solve(ISearchable domain) {
        if (domain instanceof SearchableMaze) {
            ((SearchableMaze) domain).resetStates(); // reset state data
        }

        Deque<AState> stack = new ArrayDeque<>(); // use stack for dfs (lifo)
        Set<AState> visited = new HashSet<>(); // track visited nodes
        AState start = domain.getStartState();
        AState goal  = domain.getGoalState();

        stack.push(start);    // start dfs from here
        visited.add(start);   // mark start as visited

        while (!stack.isEmpty()) {
            AState cur = stack.pop(); // get last added state
            evaluated++;

            if (cur.equals(goal)) return Solution.reconstruct(cur); // goal found

            for (AState n : domain.getPossibleStates(cur)) {
                if (visited.add(n)) {           // add if not visited
                    n.setCameFrom(cur);         // track previous state
                    stack.push(n);              // push for further search
                }
            }
        }

        return new Solution(new ArrayList<>()); // no path found
    }

    @Override
    public String getName() {
        return "DepthFirstSearch";
    }
}
