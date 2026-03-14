import javafx.scene.layout.Pane; // Import for Pane

/**
 * Represents the tank controlled by the player. Handles player-specific logic
 * like lives and respawning.
 */
public class PlayerTank extends Tank {
	private int lives;
	private int respawnTimer;
	private boolean respawning;
	private long lastShotTimeNano;

	/**
	 * Constructor for PlayerTank.
	 * 
	 * @param x        initial x-coordinate
	 * @param y        initial y-coordinate
	 * @param rootPane the main game pane
	 */
	public PlayerTank(double x, double y, Pane rootPane) {
		super(x, y, Constants.PLAYER_TANK_1_IMAGE_PATH, Constants.PLAYER_TANK_2_IMAGE_PATH, rootPane);
		this.lives = Constants.PLAYER_LIVES;
		this.respawnTimer = 0;
		this.respawning = false;
		this.lastShotTimeNano = 0;
	}

	/**
	 * Called when the player tank is hit by an enemy bullet. Decreases lives and
	 * initiates respawn or game over.
	 */
	public void hit() {
		if (!alive || respawning)
			return; // Do nothing if already dead or currently respawning

		lives--; // Decrease life count
		destroy();

		if (lives > 0) {
			respawning = true;
			respawnTimer = Constants.RESPAWN_TIME;
		} else {
			// No lives remaining, game over
		}
	}

	/**
	 * Updates the respawn process timer. If respawn timer finishes, makes the
	 * player alive and visible again at start position.
	 */
	public void updateRespawn() {
		if (!respawning)
			return;

		respawnTimer--;
		if (respawnTimer <= 0) {
			// Respawn completed
			respawning = false;
			alive = true;
			this.x = Constants.PLAYER_START_X;
			this.y = Constants.PLAYER_START_Y;
			if (sprite != null) {
				sprite.setX(this.x);
				sprite.setY(this.y);
				sprite.setVisible(true);
			}
			this.direction = Direction.UP;
			updateSpriteDirection();
		}
	}

	/**
	 * Overrides Tank's shoot method to implement a firing cooldown for the player.
	 * 
	 * @return a new Bullet object if cooldown allows, otherwise null
	 */
	@Override
	public Bullet shoot() {
		long currentTimeNano = System.nanoTime();
		// Check if enough time has passed since the last shot
		if (currentTimeNano - lastShotTimeNano < Constants.PLAYER_BULLET_COOLDOWN * 1_000_000_000L) {
			return null;
		}
		Bullet bullet = super.shoot();
		if (bullet != null) {
			lastShotTimeNano = currentTimeNano;
		}
		return bullet;
	}

	// Getter methods
	public int getLives() {
		return lives;
	}

	public boolean isRespawning() {
		return respawning;
	}
}