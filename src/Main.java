import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Entry point for the JavaFX application. Launches the game.
 */
public class Main extends Application {
    /**
     * The main entry point for the JavaFX application.
     */
    @Override
    public void start(Stage primaryStage) {
        Game game = new Game();
        game.start(primaryStage);
    }

    /**
     * The main method that launches the application.
     */

    public static void main(String[] args) {
        launch(args);
    }
}