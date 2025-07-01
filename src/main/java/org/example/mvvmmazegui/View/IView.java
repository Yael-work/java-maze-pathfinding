package org.example.mvvmmazegui.View;

public interface IView {
    void displayMaze();
    void displaySolution();
    void showAlert(String message);
    void toggleMusic();
    void onRestartClick();
}