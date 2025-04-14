package algorithms.search;

public abstract class AState implements Comparable<AState> {
    private final double cost;
    private AState cameFrom;

    protected AState(double cost){
        this.cost = cost;
    }
     public double getCost() {
        return this.cost;
     }
    public AState getCameFrom() {
        return this.cameFrom;
    }
    public void setCameFrom(AState cameFrom) {
        this.cameFrom = cameFrom;
    }

    @Override
    public abstract boolean equals(Object obj);
    @Override
    public abstract int hashCode();

    @Override
    public int compareTo(AState other) {
        return Double.compare(this.cost, other.cost);
    }
}
