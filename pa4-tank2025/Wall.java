import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represents an indestructible wall in the game. Blocks tanks and bullets.
 */
public class Wall {
	private double x, y;
	private ImageView sprite;

	/**
	 * Constructor for Wall.
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	public Wall(double x, double y) {
		this.x = x;
		this.y = y;

		try {
			Image wallImage = new Image(getClass().getResourceAsStream(Constants.WALL_IMAGE_PATH));
			sprite = new ImageView(wallImage);
		} catch (NullPointerException e) {
			System.err.println("Wall image could not be loaded: " + Constants.WALL_IMAGE_PATH);
			sprite = new ImageView(); // Use an empty ImageView if loading fails
		}

		sprite.setFitWidth(Constants.WALL_SIZE);
		sprite.setFitHeight(Constants.WALL_SIZE);
		sprite.setX(x);
		sprite.setY(y);
	}

	// Getter methods
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public ImageView getSprite() {
		return sprite;
	}
}