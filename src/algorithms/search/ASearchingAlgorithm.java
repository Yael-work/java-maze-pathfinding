package algorithms.search;

public abstract class ASearchingAlgorithm implements ISearchingAlgorithm {

    protected int evaluated = 0;              // number of nodes evaluated
    protected ISearchable currentDomain;      // reference to current searchable domain

    @Override
    public int getNumberOfNodesEvaluated() {
        return evaluated;                     // return node evaluation count
    }
}
