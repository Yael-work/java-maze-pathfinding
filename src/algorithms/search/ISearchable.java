package algorithms.search;

import java.util.List;

public interface ISearchable {
    AState getStartState();
    AState getGoalState();

    List<AState> getPossibleStates(AState state); // returns neighbor sates of (state)
}
