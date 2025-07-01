package org.example.mvvmmazegui.Model;

import org.example.algorithms.mazeGenerators.Maze;
import org.example.algorithms.search.Solution;

import java.util.function.Consumer;

public interface IModel {
    void generateMaze(int rows, int cols);
    Maze getMaze();
    void solveMaze();
    Solution getSolution();
    void movePlayer(int direction);
    int getPlayerRow();
    int getPlayerCol();
    void setChangeListener(Consumer<String> callback);
    void setMaze(Maze maze);
    int getStepCount();
    long getElapsedTimeSeconds();
    void undoMove();
    void redoMove();
    void restartMaze();
    void clearSolution();

}