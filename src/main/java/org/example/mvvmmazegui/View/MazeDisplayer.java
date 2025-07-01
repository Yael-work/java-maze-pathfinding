// this class handles the drawing and redrawing of the maze on a javafx canvas
// it displays the maze grid, the player, start/goal positions, hint highlights, and the solution path
// the canvas gets redrawn every time the maze, player position, or hint changes

package org.example.mvvmmazegui.View;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import org.example.algorithms.mazeGenerators.Maze;
import org.example.algorithms.mazeGenerators.Position;
import org.example.algorithms.search.AState;
import org.example.algorithms.search.MazeState;
import org.example.algorithms.search.Solution;



public class MazeDisplayer extends Canvas {
    private Position hintPosition = null;
    private Maze lastMaze;
    private int lastPlayerRow;
    private int lastPlayerCol;
    private Image playerImage;
    //private Image backgroundImage;

    // this method draws the maze grid on the canvas
    // it colors walls black and paths white, then overlays the start and goal positions
    // it also displays the hint (if set) and the player
    public void displayMaze(Maze maze, int playerRow, int playerCol, boolean animate) {
        this.lastMaze = maze;
        this.lastPlayerRow = playerRow;
        this.lastPlayerCol = playerCol;


        if (maze == null || maze.getRows() == 0 || maze.getColumns() == 0) return;

        double cellWidth = getWidth() / maze.getColumns();
        double cellHeight = getHeight() / maze.getRows();
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        for (int i = 0; i < maze.getRows(); i++) {
            for (int j = 0; j < maze.getColumns(); j++) {
                gc.setFill(maze.getCell(i, j) == 1 ? Color.BLACK : Color.WHITE);
                gc.fillRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                gc.setStroke(Color.GRAY);
                gc.strokeRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);
            }
        }

        drawSpecialCell(maze.getStartPosition(), Color.LIMEGREEN, cellWidth, cellHeight, gc);
        drawSpecialCell(maze.getGoalPosition(), Color.RED, cellWidth, cellHeight, gc);
        //gc.drawImage(backgroundImage, 0, 0, getWidth(), getHeight());

        if (hintPosition != null) {
            gc.setFill(Color.GOLD.deriveColor(1, 1, 1, 0.6));
            gc.fillRect(hintPosition.getColumnIndex() * cellWidth,
                        hintPosition.getRowIndex() * cellHeight,
                        cellWidth, cellHeight);
        }

        displayPlayer(playerRow, playerCol, cellWidth, cellHeight, gc);
    }

    // constructor loads the image for the player
    public MazeDisplayer() {
        try {
            playerImage = new Image(getClass().getResourceAsStream("/images/player.png"));
            //backgroundImage = new Image(getClass().getResourceAsStream("/images/background.png"));
        } catch (Exception e) {
            System.out.println("Failed to load image: " + e.getMessage());
        }
    }

    // helper to color a specific cell at the given position
    private void drawSpecialCell(Position pos, Color color, double w, double h, GraphicsContext gc) {
        gc.setFill(color);
        gc.fillRect(pos.getColumnIndex() * w, pos.getRowIndex() * h, w, h);
    }

    // draws the player image at the specified row and column
    // if player image is missing, draws a blue circle instead
    public void displayPlayer(int row, int col, double cellWidth, double cellHeight, GraphicsContext gc) {
        if (cellWidth <= 0 || cellHeight <= 0) return;
        if (playerImage != null) {
            gc.drawImage(playerImage, col * cellWidth, row * cellHeight, cellWidth, cellHeight);
        }
        else { // if image corrupted
            gc.setFill(Color.DODGERBLUE);
            gc.fillOval(col * cellWidth + cellWidth / 4, row * cellHeight + cellHeight / 4,
                    cellWidth / 2, cellHeight / 2);
        }
    }

    // draws the solution path using a semi-transparent blue color
    // this also redraws the maze first and then overlays the solution and player
    public void displaySolution(Maze maze, Solution solution) {
        if (maze == null || solution == null || maze.getColumns() == 0 || maze.getRows() == 0) return;

        //  redraw the maze to ensure itis visible
        displayMaze(maze, lastPlayerRow, lastPlayerCol, false);

        double cellWidth = getWidth() / maze.getColumns();
        double cellHeight = getHeight() / maze.getRows();
        GraphicsContext gc = getGraphicsContext2D();

        // show solution path on top of the maze
        gc.setFill(Color.CORNFLOWERBLUE.deriveColor(1, 1, 1, 0.5));
        for (AState state : solution.getSolutionPath()) {
            if (!(state instanceof MazeState)) continue;
            Position pos = ((MazeState) state).getPosition();

            if (!pos.equals(maze.getStartPosition()) && !pos.equals(maze.getGoalPosition())) {
                gc.fillRect(pos.getColumnIndex() * cellWidth, pos.getRowIndex() * cellHeight, cellWidth, cellHeight);
            }
        }

        // redraw special cells and player to ensure they are on top of maze
        drawSpecialCell(maze.getStartPosition(), Color.LIMEGREEN, cellWidth, cellHeight, gc);
        drawSpecialCell(maze.getGoalPosition(), Color.RED, cellWidth, cellHeight, gc);
        displayPlayer(lastPlayerRow, lastPlayerCol, cellWidth, cellHeight, gc);
    }

    // sets the position of a single hint and triggers a redraw
    public void setHintPosition(Position pos) {
        this.hintPosition = pos;
        redraw();  //full redraw including the hint
    }

    // re-displays the most recently drawn maze and player
    public void redraw() {
        if (lastMaze != null) {
            displayMaze(lastMaze, lastPlayerRow, lastPlayerCol, false);
        }
    }
}
