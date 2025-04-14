package algorithms.search;

import java.util.ArrayList;
import java.util.Collections;

public class Solution {
    private final ArrayList<AState> path;
    public Solution(ArrayList<AState> path) {
        this.path = path;
    }
     public ArrayList<AState> getSolution(){
        return path;
     }
    @Override
    public String toString() {
        return path.toString();
     }
     public static Solution reconstruct (AState goal){
         ArrayList<AState> path = new ArrayList<>();
         for (AState cur = goal; cur != null; cur = cur.getCameFrom()) path.add(cur);
         Collections.reverse(path);
         return new Solution(path);
     }
     public ArrayList<AState> getSolutionPath(){
        return path;
     }
}

