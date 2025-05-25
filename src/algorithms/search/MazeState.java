package algorithms.search;

import algorithms.mazeGenerators.Position;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class represents a state in a maze search problem.
 * It extends AState to include position information.
 * The position is immutable and cannot be changed after creation.
 */
public class MazeState extends AState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Position position; // position in the maze grid

    public MazeState(Position pos) {
        super(Double.POSITIVE_INFINITY); // initial cost is infinite

        if (pos == null)
            throw new NullPointerException("Position cannot be null");
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
        return this.position.equals(other.position); // same position means equal
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
