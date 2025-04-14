package algorithms.mazeGenerators;
import java.util.Random;

public abstract class AMazeGenerator implements IMazeGenerator {
    protected static final Random random = new Random();

    @Override
    public long measureAlgorithmTimeMillis(int rows, int columns) {
        long start = System.currentTimeMillis();
        generate(rows, columns);
        return System.currentTimeMillis() - start;
    }

    protected Position getRandomBorderPosition(int rows, int columns) {

        int side = random.nextInt(4);
        int row, col;
        switch (side) {
            case 0: row = 0; col = random.nextInt(columns); break;
            case 1: row = rows - 1; col = random.nextInt(columns); break;
            case 2: row = random.nextInt(rows); col = 0; break;
            case 3: row = random.nextInt(rows); col = columns - 1; break;
            default: row = 0; col = 0; break;
        }
        return new Position(row, col);
    }

}
