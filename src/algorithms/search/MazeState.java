package algorithms.search;
 import algorithms.mazeGenerators.Position;

public class MazeState  extends AState {
    private final Position position;

    public MazeState(Position pos , double cost) {
        super(cost);
        this.position = pos;
    }

    public Position getPosition() {
        return this.position;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MazeState)) return false;
        return position.equals(((MazeState) o).position);
    }
    @Override
    public int hashCode() {
        return position.hashCode();
    }
    @Override
    public String toString() {
        return position.toString();
    }
}
