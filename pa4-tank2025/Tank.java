import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import java.util.List;

/**
 * Abstract base class for PlayerTank and EnemyTank. Handles common tank
 * functionalities like movement, shooting, and visuals.
 */
public abstract class Tank {
	protected double x, y;
	protected Direction direction;
	protected ImageView sprite;
	protected Image image1;
	protected Image image2;
	protected boolean alive;
	protected boolean currentImageIs1;
	protected int animationCounter;
	protected Pane rootPane;

	/**
	 * Constructor for Tank.
	 * 
	 * @param x          initial x-coordinate
	 * @param y          initial y-coordinate
	 * @param imagePath1 path to the first tank image
	 * @param imagePath2 path to the second tank image (for animation)
	 * @param rootPane   the main game pane
	 */
	public Tank(double x, double y, String imagePath1, String imagePath2, Pane rootPane) {
		this.x = x;
		this.y = y;
		this.direction = Direction.UP; // Default direction
		this.alive = true;
		this.currentImageIs1 = true;
		this.animationCounter = 0;
		this.rootPane = rootPane;

		try {
			// Load tank images from specified paths
			image1 = new Image(getClass().getResourceAsStream(imagePath1));
			image2 = new Image(getClass().getResourceAsStream(imagePath2));
			sprite = new ImageView(image1);
		} catch (NullPointerException e) {
			System.err.println("Tank image could not be loaded: " + imagePath1 + " or " + imagePath2);
			image1 = null;
			image2 = null;
			sprite = new ImageView();
		}

		sprite.setFitWidth(Constants.TANK_SIZE);
		sprite.setFitHeight(Constants.TANK_SIZE);
		sprite.setX(x);
		sprite.setY(y);
		updateSpriteDirection();
	}

	/**
	 * Moves the tank in the new direction if possible, considering collisions and
	 * boundaries.
	 * 
	 * @param newDirection the direction to move
	 * @param walls        list of walls for collision detection
	 * @param enemies      list of other enemy tanks (used by EnemyTank for not
	 *                     bumping into each other)
	 * @param player       the player tank (used by EnemyTank for not bumping into
	 *                     player)
	 * @return true if movement was successful, false otherwise
	 */
	public boolean move(Direction newDirection, List<Wall> walls, List<EnemyTank> enemies, PlayerTank player) {
		if (!alive)
			return false;

		boolean directionChanged = (this.direction != newDirection);
		this.direction = newDirection;
		if (directionChanged) {
			updateSpriteDirection();
		}

		double newX = x + newDirection.getDx() * Constants.TANK_SPEED;
		double newY = y + newDirection.getDy() * Constants.TANK_SPEED;

		// Check for out-of-bounds movement
		if (newX < 0 || newX + Constants.TANK_SIZE > Constants.WINDOW_WIDTH || newY < 0
				|| newY + Constants.TANK_SIZE > Constants.WINDOW_HEIGHT) {
			return false; // Movement blocked by map boundary
		}

		ImageView testSprite = new ImageView();
		testSprite.setX(newX);
		testSprite.setY(newY);
		testSprite.setFitWidth(Constants.TANK_SIZE);
		testSprite.setFitHeight(Constants.TANK_SIZE);

		// Check for collision with walls
		for (Wall wall : walls) {
			if (testSprite.getBoundsInParent().intersects(wall.getSprite().getBoundsInParent())) {
				return false; // Movement blocked by a wall
			}
		}

		// If movement is valid, update position
		x = newX;
		y = newY;
		sprite.setX(x);
		sprite.setY(y);

		animationCounter++;
		if (animationCounter >= Constants.TANK_ANIMATION_FRAMES) {
			currentImageIs1 = !currentImageIs1;
			sprite.setImage(currentImageIs1 ? image1 : image2);
			animationCounter = 0;
		}
		return true;
	}

	/**
	 * Updates the tank sprite's rotation to match its current direction.
	 */
	protected void updateSpriteDirection() {
		if (sprite != null) {
			sprite.setRotate(direction.getRotation());
		}
	}

	/**
	 * Creates and returns a new Bullet fired from this tank.
	 * 
	 * @return a new Bullet object, or null if the tank cannot shoot
	 */
	public Bullet shoot() {
		if (!alive || rootPane == null)
			return null;

		double bulletStartX = x + Constants.TANK_SIZE / 2.0 - Constants.BULLET_SIZE / 2.0;
		double bulletStartY = y + Constants.TANK_SIZE / 2.0 - Constants.BULLET_SIZE / 2.0;

		switch (direction) {
		case UP:
			bulletStartY = y - Constants.BULLET_SIZE;
			break;
		case DOWN:
			bulletStartY = y + Constants.TANK_SIZE;
			break;
		case LEFT:
			bulletStartX = x - Constants.BULLET_SIZE;
			break;
		case RIGHT:
			bulletStartX = x + Constants.TANK_SIZE;
			break;
		}

		bulletStartX += direction.getDx() * (Constants.TANK_SIZE / 2.0);
		bulletStartY += direction.getDy() * (Constants.TANK_SIZE / 2.0);

		return new Bullet(bulletStartX, bulletStartY, direction, this instanceof PlayerTank, rootPane);
	}

	/**
	 * Destroys the tank, sets it as not alive, and creates an explosion effect.
	 */
	public void destroy() {
		if (alive && rootPane != null) {
			new Explosion(x + Constants.TANK_SIZE / 2.0, y + Constants.TANK_SIZE / 2.0, rootPane, false);
		}
		alive = false;
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

	public Direction getDirection() {
		return direction;
	}

	public ImageView getSprite() {
		return sprite;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public Pane getRootPane() {
		return rootPane;
	}
}