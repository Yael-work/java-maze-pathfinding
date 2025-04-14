package algorithms.search;

import java.util.*;

public class DepthFirstSearch extends ASearchingAlgorithm {
    @Override public Solution solve(ISearchable domain) {
        Deque<AState> stack = new ArrayDeque<>();
        Set<AState> visited = new HashSet<>();
        AState start = domain.getStartState();
        AState goal = domain.getGoalState();
        stack.push(start);
        visited.add(start);
        while (!stack.isEmpty()) {
            AState cur = stack.pop();
            evaluated++;
            if (cur.equals(goal)) return Solution.reconstruct(cur);
            for (AState n : domain.getPossibleStates(cur)) {
                if (visited.add(n)) {
                    n.setCameFrom(cur);
                    stack.push(n);
                }
            }
        }
        return new Solution(new ArrayList<>());
    }

    @Override public String getName() { return "DepthFirstSearch"; }
}
