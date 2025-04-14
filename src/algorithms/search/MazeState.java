package algorithms.search;

import algorithms.mazeGenerators.Position;

public class MazeState extends AState {

    private final Position position; // position in the maze grid

    public MazeState(Position pos) {
        super(Double.POSITIVE_INFINITY); // initial cost is infinite
        this.position = pos;
    }

    public Position getPosition() {
        return this.position; // expose position
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // same object
        if (!(o instanceof MazeState)) return false; // different type
        MazeState other = (MazeState) o;
        return position.equals(other.position); // same position means equal
    }

    @Override
    public int hashCode() {
        return position.hashCode(); // consistent with equals
    }

    @Override
    public String toString() {
        return position.toString(); // format like {row,col}
    }
}
