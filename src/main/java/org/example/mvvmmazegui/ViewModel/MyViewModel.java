package org.example.mvvmmazegui.ViewModel;


// this class acts as the viewmodel in the mvvm architecture
// it connects the view to the model and handles user input logic
// it forwards operations like maze generation, solving, movement, etc., to the model
// it also observes model changes and updates UI-bound properties accordingly

import org.example.mvvmmazegui.Model.IModel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.example.algorithms.mazeGenerators.Maze;
import org.example.algorithms.search.Solution;

import java.util.function.Consumer;

public class MyViewModel {
    // reference to the model, which handles all core logic and server communication
    private final IModel model;

    // javafx property for player row index, used for data binding with the view
    public IntegerProperty playerRow = new SimpleIntegerProperty();

    // javafx property for player column index, used for data binding with the view
    public IntegerProperty playerCol = new SimpleIntegerProperty();

    // callback listener used to notify the view about model state changes
    private Consumer<String> onModelChange;

    // flag used to track whether the solution should be visibly displayed or not
    private boolean solutionExplicit = false;

    // constructor initializes viewmodel with model reference and sets up a change listener
    public MyViewModel(IModel model) {
        this.model = model;
        model.setChangeListener(this::handleModelUpdate);
    }

    // requests the model to generate a new maze with given dimensions
    // this may trigger server-side generation if configured
    public void generateMaze(int rows, int cols) {
        model.generateMaze(rows, cols);
    }

    // returns the current maze from the model
    public Maze getMaze() {
        return model.getMaze();
    }

    // toggles the visibility of the maze solution
    // when called for the first time, it sets the flag and requests the model to solve the maze
    // this invokes a server-based algorithm depending on configuration
    public void solveMaze() {
        if (solutionExplicit == false){
        solutionExplicit = true;
        }
        else {
            clearTemporarySolution();
        }
        model.solveMaze();
    }


    // returns whether the solution was explicitly requested by the user
    public boolean isSolutionExplicitlyRequested() {
        return solutionExplicit;
    }

    // returns the computed solution from the model
    public Solution getSolution() {
        return model.getSolution();
    }

    // tells the model to move the player in the given direction (0–7)
    public void movePlayer(int direction) {
        model.movePlayer(direction);
    }

    // called whenever the model state changes
    // updates bound player position properties and propagates change event to the view
    private void handleModelUpdate(String event) {
        if (event.equals("mazeGenerated") || event.equals("playerMoved")) {
            playerRow.set(model.getPlayerRow());
            playerCol.set(model.getPlayerCol());
        }
        if (onModelChange != null) {
            onModelChange.accept(event);
        }
    }

    // sets the listener to be notified on model state changes (e.g. maze updated, player moved)
    public void setOnModelChange(Consumer<String> callback) {
        this.onModelChange = callback;
    }

    // returns the current player row from the model
    public int getPlayerRow() {
        return model.getPlayerRow();
    }

    // returns the current player column from the model
    public int getPlayerCol() {
        return model.getPlayerCol();
    }
    // sets a new maze into the model (e.g. from file) and updates player position bindings
    public void setLoadedMaze(Maze maze) {
        model.setMaze(maze);
        playerRow.set(model.getPlayerRow());
        playerCol.set(model.getPlayerCol());
    }
    // returns the number of steps taken by the player
    public int getStepCount() {
        return model.getStepCount();
    }

    // requests the model to undo the last player movement
    // also updates the bound player position
    public void undoMove() {
        model.undoMove();
        playerRow.set(model.getPlayerRow());
        playerCol.set(model.getPlayerCol());
    }

    // requests the model to redo the last undone movement
    // also updates the bound player position
    public void redoMove() {
        model.redoMove();
        playerRow.set(model.getPlayerRow());
        playerCol.set(model.getPlayerCol());
    }

    // returns the total time elapsed since the maze started
    public long getElapsedTimeSeconds() {
        return model.getElapsedTimeSeconds();
    }
    // restarts the maze in the model and updates player position bindings
    public void restartMaze() {
        model.restartMaze();
        playerRow.set(model.getPlayerRow());
        playerCol.set(model.getPlayerCol());
    }
    // clears the current solution from the model and resets the visibility flag
    public void clearTemporarySolution() {
        model.clearSolution();
        solutionExplicit = false;
    }
}