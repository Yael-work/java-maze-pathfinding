package algorithms.search;
import java.util.*;

public class BestFirstSearch extends ASearchingAlgorithm {
    @Override public Solution solve(ISearchable domain) {
        PriorityQueue<AState> open = new PriorityQueue<>();
        Map<AState, Double> gScore = new HashMap<>();
        AState start = domain.getStartState();
        AState goal = domain.getGoalState();
        gScore.put(start, 0.0);
        open.add(start);
        Set<AState> closed = new HashSet<>();
        while (!open.isEmpty()) {
            AState cur = open.poll();
            evaluated++;
            if (cur.equals(goal)) return Solution.reconstruct(cur);
            closed.add(cur);
            for (AState n : domain.getPossibleStates(cur)) {
                if (closed.contains(n)) continue;
                double tentative = cur.getCost() + 1; // regular + diag handled in adapter
                if (tentative < gScore.getOrDefault(n, Double.POSITIVE_INFINITY)) {
                    n.setCameFrom(cur);
                    gScore.put(n, tentative);
                    open.remove(n);
                    open.add(n);
                }
            }
        }
        return new Solution(new ArrayList<>());
    }

    @Override public String getName() { return "BestFirstSearch"; }
}