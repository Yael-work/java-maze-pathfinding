package org.example.mvvmmazegui.View;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import org.example.mvvmmazegui.Model.Configurations;
import org.example.mvvmmazegui.ViewModel.MyViewModel;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.IO.MyCompressorOutputStream;
import org.example.IO.MyDecompressorInputStream;
import org.example.algorithms.mazeGenerators.Maze;
import org.example.algorithms.mazeGenerators.Position;
import org.example.algorithms.search.AState;
import org.example.algorithms.search.MazeState;
import org.example.algorithms.search.Solution;

import java.util.List;
import java.util.Optional;

import static org.example.mvvmmazegui.Model.ServerManager.*;


/**
 * controller for the view in the mvvm architecture; handles all gui logic, user interaction, and triggers viewmodel actions.
 */
public class MyViewController implements IView {
    /** displays the maze and player on the canvas */
    public MazeDisplayer mazeDisplayer;
    /** label showing the number of steps taken by the player */
    public javafx.scene.control.Label stepCounterLabel;
    /** label showing the elapsed time in seconds */
    public javafx.scene.control.Label timerLabel;
    /** reference to the viewmodel for mvvm communication */
    private MyViewModel viewModel;
    /** mediaplayer for background music playback */
    private MediaPlayer mediaPlayer;
    /** flag indicating if music is currently playing */
    private boolean isMusicOn = true;
    /** flag toggling whether maze animation is enabled */
    private boolean animateMaze = true; // toggle animation

    /** text field for user to input number of rows for maze generation */
    @FXML
    private TextField rowsField;

    /** text field for user to input number of columns for maze generation */
    @FXML
    private TextField colsField;

    private MediaPlayer victoryPlayer; // media player for victory sound

    /**
     * sets the viewmodel instance for this controller to enable mvvm data flow.
     * @param viewModel the viewmodel to connect to this view
     */
    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * creates a new maze based on user input for size.
     * prompts user for rows and columns, validates input, and triggers maze generation.
     * also restarts client-server communication for maze generation.
     * handles invalid input with alerts.
     * client-server communication: triggers maze server start and maze generation on server.
     */
    @FXML
    public void newMaze() {
        viewModel.clearTemporarySolution();
        TextInputDialog dialog = new TextInputDialog("50x50");
        dialog.setTitle("Maze Size Input");
        dialog.setHeaderText("enter maze size as rows x columns (example: 50x50):");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(input -> {
            String[] parts = input.toLowerCase().replace(" ", "").split("x");
            if (parts.length == 2) {
                try {
                    int rows = Integer.parseInt(parts[0]);
                    int cols = Integer.parseInt(parts[1]);

                    // client-server communication: restart servers and request maze generation
                    stopServers();
                    startServers();
                    viewModel.generateMaze(rows, cols);
                    displayMaze();
                    mazeDisplayer.requestFocus();
                } catch (NumberFormatException e) {
                    showAlert("invalid input format. please use numbers like 50x50.");
                }
            } else {
                showAlert("invalid input. please enter in format rows x cols (e.g., 50x50).");
            }
        });
    }

    /**
     * solves the current maze using the selected algorithm.
     * requests the viewmodel to solve the maze and displays the solution.
     * handles exceptions and informs the user if errors occur.
     * client-server communication: triggers maze solving on the server.
     */
    @FXML
    public void solveMaze() {
        try {
            viewModel.solveMaze();
            // call displaySolution explicitly as a fallback
            displaySolution();
        } catch (Exception e) {
            showAlert("error solving maze: " + e.getMessage());
        }
    }

    /**
     * exits the application immediately.
     */
    @FXML
    public void exitApp() {
        System.exit(0);
    }

    /**
     * saves the current maze to a .maze file chosen by the user.
     * compresses the maze data and writes it to disk.
     * handles file i/o errors and notifies the user.
     */
    @FXML
    public void saveMaze() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("save maze");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("maze files", "*.maze"));
        java.io.File file = fileChooser.showSaveDialog(mazeDisplayer.getScene().getWindow());
        if (file != null) {
            try (java.io.OutputStream out = new MyCompressorOutputStream(new java.io.FileOutputStream(file))) {
                Maze maze = viewModel.getMaze();
                byte[] mazeBytes = maze.toByteArray();
                out.write(mazeBytes);
                out.flush();
            } catch (java.io.IOException e) {
                showAlert("failed to save maze: " + e.getMessage());
            }
        }
    }

    /**
     * loads a maze from a .maze file chosen by the user.
     * decompresses maze data and updates the viewmodel and display.
     * handles file i/o errors and notifies the user.
     * also restarts the maze state after loading.
     */
    @FXML
    public void loadMaze() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("load maze");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("maze files", "*.maze"));
        java.io.File file = fileChooser.showOpenDialog(mazeDisplayer.getScene().getWindow());
        if (file != null) {
            try (java.io.InputStream in = new MyDecompressorInputStream(new java.io.FileInputStream(file))) {
                byte[] buffer = new byte[100000]; // adjust if needed
                int bytesRead = in.read(buffer);
                Maze loadedMaze = new Maze(buffer);
                viewModel.setLoadedMaze(loadedMaze); // updates maze in viewmodel
                displayMaze();
                mazeDisplayer.requestFocus();
            } catch (java.io.IOException e) {
                showAlert("failed to load maze: " + e.getMessage());
            }
        }
        onRestartClick();
    }

    /**
     * initializes the view, setting up event handlers, focus, zoom, drag-and-drop, background music, fade-in animation,
     * and periodic updates of step/timer labels. also attaches listeners for resizing and sets initial label values.
     * handles cross-platform focus and input quirks.
     */
    @FXML
    public void initialize() {
        setupMazeDisplayerFocusAndZoom();
        setupKeyboardControls();
        setupMouseDragAndDrop();
        setupBackgroundMusic();
        setupVictorySound();
        setupFadeInAnimation();
        setupStepAndTimerUpdater();
        bindMazeDisplayerResizeListeners();
        resetStepAndTimerLabels();
    }
    // Focus and Zoom - helper
    private void setupMazeDisplayerFocusAndZoom() {
        mazeDisplayer.setFocusTraversable(true);
        mazeDisplayer.requestFocus();
        mazeDisplayer.setOnMouseClicked(e -> mazeDisplayer.requestFocus());

        mazeDisplayer.setOnScroll(event -> {
            if (event.isControlDown()) {
                double zoomFactor = event.getDeltaY() > 0 ? 1.1 : 0.9;
                mazeDisplayer.setScaleX(mazeDisplayer.getScaleX() * zoomFactor);
                mazeDisplayer.setScaleY(mazeDisplayer.getScaleY() * zoomFactor);
                event.consume();
            }
        });
    }
    //Keyboard Controls - helper
    private void setupKeyboardControls() {
        mazeDisplayer.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.Z) {
                viewModel.undoMove();
            } else if (event.isControlDown() && event.getCode() == KeyCode.Y) {
                viewModel.redoMove();
            } else {
                switch (event.getCode()) {
                    case NUMPAD8, DIGIT8, UP -> viewModel.movePlayer(0);
                    case NUMPAD6, DIGIT6, RIGHT -> viewModel.movePlayer(1);
                    case NUMPAD2, DIGIT2, DOWN -> viewModel.movePlayer(2);
                    case NUMPAD4, DIGIT4, LEFT -> viewModel.movePlayer(3);
                    case NUMPAD7, DIGIT7 -> viewModel.movePlayer(4);
                    case NUMPAD9, DIGIT9 -> viewModel.movePlayer(5);
                    case NUMPAD1, DIGIT1 -> viewModel.movePlayer(6);
                    case NUMPAD3, DIGIT3 -> viewModel.movePlayer(7);
                }
            }
            displayMaze();
            checkWinCondition();
        });
    }
    //Mouse Drag and Drop support - helper
    private void setupMouseDragAndDrop() {
        final double[] lastMousePosition = new double[2];

        mazeDisplayer.setOnMousePressed(event -> {
            lastMousePosition[0] = event.getSceneX();
            lastMousePosition[1] = event.getSceneY();
        });

        mazeDisplayer.setOnMouseDragged(event -> {
            double dx = event.getSceneX() - lastMousePosition[0];
            double dy = event.getSceneY() - lastMousePosition[1];
            mazeDisplayer.setTranslateX(mazeDisplayer.getTranslateX() + dx);
            mazeDisplayer.setTranslateY(mazeDisplayer.getTranslateY() + dy);
            lastMousePosition[0] = event.getSceneX();
            lastMousePosition[1] = event.getSceneY();
        });

        mazeDisplayer.setOnDragOver(event -> {
            if (event.getGestureSource() != mazeDisplayer &&
                    event.getDragboard().hasFiles() &&
                    event.getDragboard().getFiles().get(0).getName().endsWith(".maze")) {
                event.acceptTransferModes(javafx.scene.input.TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        mazeDisplayer.setOnDragDropped(event -> {
            var db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                java.io.File file = db.getFiles().get(0);
                try (var in = new MyDecompressorInputStream(new java.io.FileInputStream(file))) {
                    byte[] buffer = new byte[100000];
                    int bytesRead = in.read(buffer);
                    Maze loadedMaze = new Maze(buffer);
                    viewModel.setLoadedMaze(loadedMaze);
                    displayMaze();
                    showAlert("Maze loaded from drag-and-drop.");
                    success = true;
                } catch (Exception e) {
                    showAlert("Failed to load dropped maze file: " + e.getMessage());
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    //background Music - helper
    private void setupBackgroundMusic() {
        try {
            java.net.URL musicUrl = getClass().getResource("/audio/background.mp3");
            if (musicUrl == null) {
                musicUrl = getClass().getClassLoader().getResource("audio/background.mp3");
            }
            if (musicUrl == null) {
                musicUrl = new java.io.File("src/main/resources/audio/background.mp3").toURI().toURL();
            }

            if (musicUrl != null) {
                String musicPath = musicUrl.toExternalForm();
                mediaPlayer = new MediaPlayer(new Media(musicPath));
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.setVolume(0.4);
                mediaPlayer.play();
            }
        } catch (Exception e) {
            System.out.println("Background music failed to load: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //victory sound - helper
    private void setupVictorySound() {
        try {
            java.net.URL victoryUrl = getClass().getResource("/audio/victory.mp3");
            if (victoryUrl == null) {
                victoryUrl = getClass().getClassLoader().getResource("audio/victory.mp3");
            }
            if (victoryUrl == null) {
                victoryUrl = new java.io.File("src/main/resources/audio/victory.mp3").toURI().toURL();
            }

            if (victoryUrl != null) {
                victoryPlayer = new MediaPlayer(new Media(victoryUrl.toExternalForm()));
                victoryPlayer.setVolume(0.6);
            }
        } catch (Exception e) {
            System.out.println("Failed to load victory sound: " + e.getMessage());
        }
    }

    //fade in animation - helper
    private void setupFadeInAnimation() {
        FadeTransition ft = new FadeTransition(Duration.seconds(1.5), mazeDisplayer);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    //step and timer Update - heleper
    private void setupStepAndTimerUpdater() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    if (stepCounterLabel != null)
                        stepCounterLabel.setText("Steps: " + viewModel.getStepCount());
                    if (timerLabel != null)
                        timerLabel.setText("Time: " + viewModel.getElapsedTimeSeconds() + "s");
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    //Resize Listeners - helper
    private void bindMazeDisplayerResizeListeners() {
        mazeDisplayer.widthProperty().addListener((obs, oldVal, newVal) -> displayMaze());
        mazeDisplayer.heightProperty().addListener((obs, oldVal, newVal) -> displayMaze());
    }

    //Initial Labeling - helper
    private void resetStepAndTimerLabels() {
        if (stepCounterLabel != null) {
            stepCounterLabel.setText("Steps: 0");
        }
        if (timerLabel != null) {
            timerLabel.setText("Time: 0s");
        }
    }

    /**
     * opens dialogs for the user to select maze generation and solving algorithms.
     * updates the configuration singleton with the selected algorithms.
     */
    @FXML
    public void openAlgorithmConfig() {
        List<String> generationOptions = List.of("MyMazeGenerator", "SimpleMazeGenerator", "EmptyMazeGenerator");
        List<String> solvingOptions = List.of("BreadthFirstSearch", "DepthFirstSearch", "BestFirstSearch");

        String currentGen = Configurations.getInstance().getMazeGeneratingAlgorithm();
        String currentSearch = Configurations.getInstance().getMazeSearchingAlgorithm();

        ChoiceDialog<String> genDialog = new ChoiceDialog<>(currentGen, generationOptions);
        genDialog.setTitle("Maze Generator Configuration");
        genDialog.setHeaderText("Choose maze generation algorithm");

        Optional<String> genResult = genDialog.showAndWait();
        genResult.ifPresent(selectedGen ->
                Configurations.getInstance().setMazeGeneratingAlgorithm(selectedGen)
        );

        ChoiceDialog<String> searchDialog = new ChoiceDialog<>(currentSearch, solvingOptions);
        searchDialog.setTitle("Maze Solver Configuration");
        searchDialog.setHeaderText("Choose maze solving algorithm");

        Optional<String> searchResult = searchDialog.showAndWait();
        searchResult.ifPresent(selectedSearch ->
                Configurations.getInstance().setMazeSearchingAlgorithm(selectedSearch)
        );
    }

    /**
     * checks if the player has reached the maze goal.
     * if so, shows a congratulatory alert with statistics about the completion.
     */
    private void checkWinCondition() {
        Maze maze = viewModel.getMaze();
        if (maze == null) return;
        if (viewModel.getPlayerRow() == maze.getGoalPosition().getRowIndex()
                && viewModel.getPlayerCol() == maze.getGoalPosition().getColumnIndex()) {
            int steps = viewModel.getStepCount();
            long time = viewModel.getElapsedTimeSeconds();
            // int nodes = viewModel.getSolution() != null ? viewModel.getSolution().getSolutionPath().size() : 0;
            mediaPlayer.pause();
            if (victoryPlayer != null) {
                victoryPlayer.stop(); // stop if already playing
                victoryPlayer.play();
            }
            String message = String.format("""
                     Congratulations! You've solved the maze.

                     Steps taken: %d
                     Time elapsed: %d seconds
                    """, steps, time);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Maze Solved");
            alert.setHeaderText("Maze Completed Successfully");
            alert.setContentText(message);
            alert.setResizable(true);
            alert.getDialogPane().setPrefSize(450, 300);
            alert.showAndWait();

        }
    }

    /**
     * Handler toggles background music playback on or off.
     * updates the isMusicOn flag and notifies the user of the change.
     */
    @FXML
    public void toggleMusic() {
        if (mediaPlayer == null) return;

        if (isMusicOn) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.play();
        }

        isMusicOn = !isMusicOn;
        showAlert("Music " + (isMusicOn ? "resumed." : "paused."));
    }

    /**
     * Handelr undo the last move made by the player and updates the maze display.
     * also checks for win condition after undo.
     */
    @FXML
    public void onUndoClick() {
        viewModel.undoMove();
        displayMaze();
        checkWinCondition();
    }

    /**
     * Handelr redo a previously undone move and updates the maze display.
     * also checks for win condition after redo.
     */
    @FXML
    public void onRedoClick() {
        viewModel.redoMove();
        displayMaze();
        checkWinCondition();
    }

    /**
     * provides a hint for the next step towards the goal using the current solution.
     * highlights the next position and clears the temporary solution to prevent auto-display.
     * if no solution is available, informs the user. (!only if user is only the solve path!)
     */
    @FXML
    public void onHintClick() {
        if (viewModel.getSolution() == null) {
            viewModel.solveMaze(); // internal solve for hint only
        }

        Solution solution = viewModel.getSolution();
        if (solution == null || solution.getSolutionPath().isEmpty()) {
            showAlert("Unable to compute hint. Try solving again.");
            return;
        }

        int currentRow = viewModel.getPlayerRow();
        int currentCol = viewModel.getPlayerCol();

        List<AState> path = solution.getSolutionPath();
        for (int i = 0; i < path.size() - 1; i++) {
            MazeState current = (MazeState) path.get(i);
            Position pos = current.getPosition();
            if (pos.getRowIndex() == currentRow && pos.getColumnIndex() == currentCol) {
                MazeState next = (MazeState) path.get(i + 1);
                Position hintPos = next.getPosition();
                mazeDisplayer.setHintPosition(hintPos);
                mazeDisplayer.redraw(); // Hint only

                // Clear solution to avoid auto-display after redraw
                viewModel.clearTemporarySolution();

                return;
            }
        }

        showAlert("You may already be at the goal or the path isn't traceable from here.");
    }

    /**
     * displays the maze and player position using the maze displayer.
     * if a solution was explicitly requested, also displays the solution.
     * ensures the maze displayer has focus for keyboard input.
     */
    @Override
    public void displayMaze() {
        mazeDisplayer.displayMaze(
                viewModel.getMaze(),
                viewModel.getPlayerRow(),
                viewModel.getPlayerCol(),
                true
        );
        if (viewModel.isSolutionExplicitlyRequested()) {
            mazeDisplayer.displaySolution(viewModel.getMaze(), viewModel.getSolution());
        }
        mazeDisplayer.setFocusTraversable(true);
        Platform.runLater(() -> mazeDisplayer.requestFocus());
    }

    /**
     * Handelr displays the solution path for the current maze using the maze displayer.
     */
    @Override
    public void displaySolution() {
        mazeDisplayer.displaySolution(viewModel.getMaze(), viewModel.getSolution());
    }

    /**
     *  shows an informational alert dialog with the given message.
     * @param message the message to display in the alert
     */
    @Override
    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     *  Handler shows a help dialog describing controls, menu options, and the goal of the game.
     */
    @FXML
    public void showHelp() {
        String helpText = """
                Welcome to the Maze Game!
                
                Controls:
                - Use NUMPAD/Top-Bar number keys (1–9) to move the player.
                  8 = up, 2 = down, 4 = left, 6 = right
                  7 = up-left, 9 = up-right, 1 = down-left, 3 = down-right
                  
                - Alternative use arrows to move the player
                
                Menu:
                - File > New: Generate a new maze.
                - File > Solve: Solve the current maze using the selected algorithm.
                - File > Save/Load: Save to or load from a .maze file.
                - Help > How to Play: Show this help dialog.
                - Config: Set maze generation or search algorithm. ( will be set on program restart )
                
                Goal:
                Reach the red square (goal) starting from the green square (start).
                
                Good luck!
                """;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("How to Play");
        alert.setHeaderText("Maze Game Instructions");
        alert.setContentText(helpText);
        alert.setResizable(true);
        alert.getDialogPane().setPrefSize(500, 400);
        alert.showAndWait();
    }

    /**
     * HAndler shows an about dialog with project, author, and feature information.
     */
    @FXML
    public void showAbout() {
        String aboutText = """
                Maze Game Project - Part C
                Authors: Denis Kulman , Yael Durahly
             
                
                Features:
                - Maze generation and solving
                - Player navigation
                - GUI with JavaFX and MVVM architecture
                - Client-server communication
                """;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("About This Project");
        alert.setContentText(aboutText);
        alert.setResizable(true);
        alert.getDialogPane().setPrefSize(500, 300);
        alert.showAndWait();
    }

    /**
     * handler restarts the maze to its initial state.
     * if no maze is loaded, shows an alert to the user.
     */
    @FXML
    public void onRestartClick() {
        if (viewModel != null && viewModel.getMaze() != null) {
            viewModel.restartMaze();
            displayMaze();
        } else {
            showAlert("maze not available or viewmodel not initialized.");
        }
    }

    /**
     * handler resets the zoom level of the maze displayer to default (1.0x).
     * notifies the user that zoom was reset.
     */
    @FXML
    public void resetZoom() {
        mazeDisplayer.setScaleX(1.0);
        mazeDisplayer.setScaleY(1.0);
        showAlert("zoom level reset.");
    }

    /**
     * handler for toggles the animation setting for maze display.
     * notifies the user of the new animation state.
     */
    @FXML
    public void toggleAnimation() {
        animateMaze = !animateMaze;
        showAlert("maze animation " + (animateMaze ? "enabled" : "disabled") + ".");
    }

    /**
     * handler for the "Start Maze" button, reads row and column input fields, validates input,
     * triggers maze generation, and updates the window size and visibility of panels.
     * client-server communication: restarts servers and requests maze generation on the server.
     */
    @FXML
    public void startMazeWithInput() {
        if (rowsField == null || colsField == null) {
            showAlert("input fields for rows and columns not found.");
            return;
        }

        String rowText = rowsField.getText();
        String colText = colsField.getText();

        if (!rowText.matches("\\d+") || !colText.matches("\\d+")) {
            showAlert("please enter valid positive integers for rows and columns.");
            return;
        }

        int rows = Integer.parseInt(rowText);
        int cols = Integer.parseInt(colText);

        if (rows <= 0 || cols <= 0) {
            showAlert("rows and columns must be greater than 0.");
            return;
        }

        // client-server communication: restart servers and request maze generation
        stopServers();
        startServers();
        viewModel.generateMaze(rows, cols);
        displayMaze();
        mazeDisplayer.requestFocus();

        // resize window based on maze size
        Stage stage = (Stage) mazeDisplayer.getScene().getWindow();
        int minWidth = Math.max(800, cols * 15);
        int minHeight = Math.max(800, rows * 15);
        stage.setWidth(minWidth);
        stage.setHeight(minHeight);

        // hide prompt panel and show the maze
        javafx.scene.Node startPane = mazeDisplayer.getScene().lookup("#startPane");
        javafx.scene.Node mazeContainer = mazeDisplayer.getScene().lookup("#mazeContainer");
        if (startPane != null && mazeContainer != null) {
            startPane.setVisible(false);
            startPane.setManaged(false);
            mazeContainer.setVisible(true);
            mazeContainer.setManaged(true);
        }
    }
}