package algorithms.search;

/**
 * Abstract class for search algorithms.
 * <p>
 * This class implements the {@link ISearchingAlgorithm} interface and provides
 * a base for specific search algorithms. It maintains the number of nodes evaluated
 * and a reference to the current searchable domain.
 * </p>
 *
 * @see ISearchingAlgorithm
 */

public abstract class ASearchingAlgorithm implements ISearchingAlgorithm {

    protected int evaluated = 0;              // number of nodes evaluated
    protected ISearchable currentDomain;      // reference to current searchable domain

    @Override
    public int getNumberOfNodesEvaluated() {
        return evaluated;                     // return node evaluation count
    }
}
