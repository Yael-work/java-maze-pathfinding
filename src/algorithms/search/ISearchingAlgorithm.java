package algorithms.search;

/**
 * Interface for search algorithm implementations.
 * <p>
 * Provides a standard contract for solving {@link ISearchable} domains
 * and reporting algorithm-specific information such as name and node count.
 * </p>
 *
 * Implementing classes include {@link BreadthFirstSearch}, {@link DepthFirstSearch},
 * and {@link BestFirstSearch}.
 */

public interface ISearchingAlgorithm {
    Solution solve(ISearchable domain);
    String getName();
    int getNumberOfNodesEvaluated(); // return number of nodes evaluated
}
