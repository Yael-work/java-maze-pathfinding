package algorithms.search;

import java.io.Serial;
import java.io.Serializable;

/**
 * this is the base class for any state in a search problem.
 * it keeps track of cost, where it came from, and its unique identifier.
 * used by search algorithms to build paths and track progress.
 */

public abstract class AState implements Comparable<AState> , Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    protected double cost;          // g(n): cost from start
    private AState cameFrom;        // back pointer to previous state
    protected boolean visited;      // used optionally to mark expansion

    protected AState(double initialCost) {
        this.cost = initialCost;
    }

    //getters / setters
    public void   setCost(double cost) { this.cost = cost; }
    public double getCost() { return cost; }
    public AState getCameFrom()                  { return cameFrom; }
    public void   setCameFrom(AState cameFrom)   { this.cameFrom = cameFrom; }


    public void reset() { // clear search data so state can be reused
        cameFrom = null;
        cost     = Double.POSITIVE_INFINITY;
        visited  = false;
    }


    @Override
    public int compareTo(AState other) {
        return Double.compare(this.cost, other.cost); // compare by g(n)
    }

    //(must be implemented by subclasses)
    @Override public abstract boolean equals(Object obj); // defines state identity
    @Override public abstract int hashCode();             // must match equals
}
