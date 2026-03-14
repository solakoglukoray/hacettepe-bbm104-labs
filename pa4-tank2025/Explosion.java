import javafx.animation.FadeTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Represents a visual explosion effect. Uses a FadeTransition to make the
 * explosion image fade out.
 */
public class Explosion {
	private ImageView sprite;
	private FadeTransition fadeTransition;
	private boolean finished = false;

	/**
	 * Constructor for Explosion.
	 * 
	 * @param x       center x-coordinate of the explosion
	 * @param y       center y-coordinate of the explosion
	 * @param root    the root JavaFX Pane to add the explosion to
	 * @param isSmall true for a small explosion (bullet-wall), false for a large
	 *                one (tank destruction)
	 */
	public Explosion(double x, double y, Pane root, boolean isSmall) {
		String imagePath = isSmall ? Constants.SMALL_EXPLOSION_IMAGE_PATH : Constants.EXPLOSION_IMAGE_PATH;
		int size = isSmall ? Constants.SMALL_EXPLOSION_SIZE : Constants.EXPLOSION_SIZE;

		try {
			Image explosionImage = new Image(getClass().getResourceAsStream(imagePath));
			sprite = new ImageView(explosionImage);
		} catch (NullPointerException e) {
			System.err.println("Explosion image could not be loaded: " + imagePath);
			finished = true;
			return;
		}

		sprite.setFitWidth(size);
		sprite.setFitHeight(size);
		sprite.setX(x - size / 2.0);
		sprite.setY(y - size / 2.0);
		sprite.setOpacity(1.0);

		// Create a fade-out animation
		fadeTransition = new FadeTransition(Duration.seconds(isSmall ? 0.3 : 0.6), sprite);
		fadeTransition.setFromValue(1.0);
		fadeTransition.setToValue(0.0);
		fadeTransition.setOnFinished(e -> {
			if (root != null && sprite != null) {
				root.getChildren().remove(sprite);
			}
			finished = true;
		});

		if (root != null) {
			root.getChildren().add(sprite);
			fadeTransition.play();
		} else {
			finished = true;
		}
	}

	/**
	 * Checks if the explosion animation has finished.
	 * 
	 * @return true if finished, false otherwise
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * Gets the ImageView sprite for this explosion.
	 * 
	 * @return the ImageView of the explosion
	 */
	public ImageView getSprite() {
		return sprite;
	}

	/**
	 * Plays the fade transition if it hasn't finished. Useful if the explosion
	 * needs to be explicitly started after creation.
	 */
	public void play() {
		if (fadeTransition != null && !finished) {
			fadeTransition.play();
		}
	}
}