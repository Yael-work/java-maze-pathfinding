package algorithms.search;

import java.util.ArrayList;
import java.util.Collections;
/**
 * holds the result of running a search algorithm.
 * stores the full path from start to goal as a list of states.
 * can be printed or used to analyze how the algorithm solved the problem.
 */

public class Solution {

    private final ArrayList<AState> path; // list of states forming the path

    public Solution(ArrayList<AState> path) {
        this.path = path; // store path
    }

    @Override
    public String toString() {
        return path.toString(); // stringify path for printing
    }

    public static Solution reconstruct(AState goal) {
        if (goal == null) return new Solution(new ArrayList<>()); // fail-safe
        ArrayList<AState> path = new ArrayList<>();
        for (AState cur = goal; cur != null; cur = cur.getCameFrom()) path.add(cur); // walk back via cameFrom
        Collections.reverse(path); // reverse to get start → goal
        return new Solution(path);
    }

    public ArrayList<AState> getSolutionPath() {
        return path; // alias for getSolution()
    }
}
