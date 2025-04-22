package algorithms.search;

import java.util.List;

/** This interface defines the contract for searchable domains.
 */
public interface ISearchable {
    AState getStartState();
    AState getGoalState();

    List<AState> getPossibleStates(AState state); // returns neighbor sates of (state)
}
