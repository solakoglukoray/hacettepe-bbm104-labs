import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Represents a bullet fired by a tank. Handles its movement and destruction.
 */
public class Bullet {
	private double x, y;
	private Direction direction;
	private ImageView sprite;
	private boolean active;
	private boolean isPlayerBullet;
	private Pane rootPane;

	/**
	 * Constructor for Bullet.
	 * 
	 * @param x              initial x-coordinate
	 * @param y              initial y-coordinate
	 * @param direction      movement direction
	 * @param isPlayerBullet true if fired by player, false otherwise
	 * @param rootPane       the main game pane
	 */
	public Bullet(double x, double y, Direction direction, boolean isPlayerBullet, Pane rootPane) {
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.active = true;
		this.isPlayerBullet = isPlayerBullet;
		this.rootPane = rootPane;

		try {
			Image bulletImage = new Image(getClass().getResourceAsStream(Constants.BULLET_IMAGE_PATH));
			sprite = new ImageView(bulletImage);
		} catch (NullPointerException e) {
			System.err.println("Bullet image could not be loaded: " + Constants.BULLET_IMAGE_PATH);
			sprite = new ImageView();
		}

		sprite.setFitWidth(Constants.BULLET_SIZE);
		sprite.setFitHeight(Constants.BULLET_SIZE);
		sprite.setX(x);
		sprite.setY(y);
		sprite.setRotate(direction.getRotation());
	}

	/**
	 * Updates the bullet's position. Deactivates if it goes off-screen.
	 */
	public void update() {
		if (!active)
			return;

		// Move bullet according to its speed and direction
		x += direction.getDx() * Constants.BULLET_SPEED;
		y += direction.getDy() * Constants.BULLET_SPEED;

		if (sprite != null) {
			sprite.setX(x);
			sprite.setY(y);
		}

		// Check for out-of-bounds
		if (x + Constants.BULLET_SIZE < 0 || x > Constants.WINDOW_WIDTH || y + Constants.BULLET_SIZE < 0
				|| y > Constants.WINDOW_HEIGHT) {
			destroy(false);
		}
	}

	/**
	 * Deactivates the bullet and optionally creates a small explosion if it hit a
	 * wall.
	 * 
	 * @param hitWall true if the bullet hit a wall, false otherwise
	 */
	public void destroy(boolean hitWall) {
		if (active && rootPane != null && hitWall) {
			new Explosion(x + Constants.BULLET_SIZE / 2.0, y + Constants.BULLET_SIZE / 2.0, rootPane, true);
		}
		active = false;
		if (sprite != null) {
			sprite.setVisible(false);
		}
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

	public boolean isActive() {
		return active;
	}

	public boolean isPlayerBullet() {
		return isPlayerBullet;
	}
}