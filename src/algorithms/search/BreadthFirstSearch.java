package algorithms.search;
import java.util.*;

public class BreadthFirstSearch extends ASearchingAlgorithm{
    @Override public Solution solve(ISearchable domain) {
        Queue<AState> q = new ArrayDeque<>();
        Set<AState> visited = new HashSet<>();
        AState start = domain.getStartState();
        AState goal = domain.getGoalState();
        q.add(start);
        visited.add(start);
        while (!q.isEmpty()) {
            AState cur = q.poll();
            evaluated++;
            if (cur.equals(goal)) return Solution.reconstruct(cur);
            for (AState n : domain.getPossibleStates(cur)) {
                if (visited.add(n)) {
                    n.setCameFrom(cur);
                    q.add(n);
                }
            }
        }
        return new Solution(new ArrayList<>()); // no path
    }

    @Override public String getName() { return "BreadthFirstSearch"; }
}
