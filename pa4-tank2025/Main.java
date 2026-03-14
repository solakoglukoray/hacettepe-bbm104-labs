import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Main class for the Tank 2025 game. Initializes and starts the JavaFX
 * application and the game engine.
 */
public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Tank 2025");

		Pane root = new Pane();

		root.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

		Scene scene = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

		GameEngine gameEngine = new GameEngine(root, scene);

		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();

		gameEngine.start();
	}

	public static void main(String[] args) {
		launch(args);
	}
}