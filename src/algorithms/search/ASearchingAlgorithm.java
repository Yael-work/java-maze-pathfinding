package algorithms.search;

public abstract class ASearchingAlgorithm implements ISearchingAlgorithm {
    protected int evaluated = 0;
    @Override
    public int getNumberOfNodesEvaluated() {
        return evaluated;
    }
}
