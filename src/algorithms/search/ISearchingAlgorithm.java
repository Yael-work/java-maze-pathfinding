package algorithms.search;

// This interface defines the contract for searching algorithms.
public interface ISearchingAlgorithm {
    Solution solve(ISearchable domain);
    String getName();
    int getNumberOfNodesEvaluated(); // return number of nodes evaluated
}
