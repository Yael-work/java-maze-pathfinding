package org.example.mvvmmazegui;

import org.example.mvvmmazegui.Model.MyModel;
import org.example.mvvmmazegui.Model.ServerManager;
import org.example.mvvmmazegui.View.MyViewController;
import org.example.mvvmmazegui.ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        ServerManager.startServers();
        // Try multiple approaches to load the FXML file
        FXMLLoader fxmlLoader = null;
        Exception lastException = null;

        //because I am working using  mac i created number of approaches to fetch the path
        //as some worked for me and not on windows

        // Approach 1: Try with absolute path
        try {
            var resource = getClass().getResource("/org/example/mvvmmazegui/View/MyView.fxml");
            if (resource != null) {
                fxmlLoader = new FXMLLoader(resource);
            }
        } catch (Exception e) {
            lastException = e;
        }

        // Approach 2: Try with relative path
        if (fxmlLoader == null) {
            try {
                var resource = Main.class.getResource("View/MyView.fxml");
                if (resource != null) {
                    fxmlLoader = new FXMLLoader(resource);
                }
            } catch (Exception e) {
                lastException = e;
            }
        }

        // Approach 3: Try with class loader
        if (fxmlLoader == null) {
            try {
                var resource = Main.class.getClassLoader().getResource("org/example/mvvmmazegui/View/MyView.fxml");
                if (resource != null) {
                    fxmlLoader = new FXMLLoader(resource);
                }
            } catch (Exception e) {
                lastException = e;
            }
        }

        // Approach 4: Try with direct file path
        if (fxmlLoader == null) {
            try {
                var url = new java.io.File("src/main/java/org/example/mvvmmazegui/View/MyView.fxml").toURI().toURL();
                fxmlLoader = new FXMLLoader(url);
            } catch (Exception e) {
                lastException = e;
            }
        }

        // If all approaches failed, throw an exception
        if (fxmlLoader == null) {
            throw new RuntimeException("Failed to load FXML file", lastException);
        }

        Scene scene = new Scene(fxmlLoader.load());

        // try multiple approaches to load the CSS file
        String cssPath = "/styles/style.css";
        java.net.URL cssResource = null;

        // Approach 1: Try with absolute path
        try {
            cssResource = getClass().getResource(cssPath);
        } catch (Exception ignored) {}

        // Approach 2: Try with relative path
        if (cssResource == null) {
            try {
                cssResource = Main.class.getResource("../../../" + cssPath);
            } catch (Exception ignored) {}
        }

        // Approach 3: Try with class loader
        if (cssResource == null) {
            try {
                cssResource = Main.class.getClassLoader().getResource("styles/style.css");
            } catch (Exception ignored) {}
        }

        // Approach 4: Try with direct file path
        if (cssResource == null) {
            try {
                cssResource = new java.io.File("src/main/resources/styles/style.css").toURI().toURL();
            } catch (Exception ignored) {}
        }

        // Apply the CSS if found
        if (cssResource != null) {
            scene.getStylesheets().add(cssResource.toExternalForm());
        }

        MyModel model = new MyModel();
        MyViewModel viewModel = new MyViewModel(model);

        MyViewController controller = fxmlLoader.getController();
        controller.setViewModel(viewModel);

        viewModel.setOnModelChange(event -> {
            if (event.equals("mazeGenerated")) controller.displayMaze();
            if (event.equals("mazeSolved")) controller.displaySolution();
            if (event.equals("mazeRestarted")) controller.displayMaze();
            if (event.equals("playerMoved")) controller.displayMaze();
            if (event.equals("mazeLoaded")) controller.displayMaze();
        });

        primaryStage.setTitle("ATP Maze Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
