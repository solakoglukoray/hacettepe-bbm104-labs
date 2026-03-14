import javafx.scene.layout.Pane;
import java.util.List;
import java.util.Random;

/**
 * Represents an AI-controlled enemy tank. Handles automatic movement and
 * shooting.
 */
public class EnemyTank extends Tank {
	private int directionTimer;
	private int shootTimer;
	private Random random;

	/**
	 * Constructor for EnemyTank.
	 * 
	 * @param x        initial x-coordinate
	 * @param y        initial y-coordinate
	 * @param rootPane the main game pane
	 */
	public EnemyTank(double x, double y, Pane rootPane) {
		super(x, y, Constants.ENEMY_TANK_1_IMAGE_PATH, Constants.ENEMY_TANK_2_IMAGE_PATH, rootPane);
		this.random = new Random();
		this.directionTimer = random.nextInt(60) + 30;
		// Initialize shoot timer, ensuring a minimum cooldown
		this.shootTimer = Constants.ENEMY_MIN_SHOOT_COOLDOWN + random.nextInt(Constants.ENEMY_SHOOT_INTERVAL);

		Direction[] directions = Direction.values();
		this.direction = directions[random.nextInt(directions.length)];
		updateSpriteDirection();
	}

	/**
	 * Updates the enemy tank's AI for movement and shooting.
	 * 
	 * @param walls        list of walls for collision detection
	 * @param bullets      list to add newly fired bullets to
	 * @param player       the player tank (can be used for more advanced AI, e.g.,
	 *                     targeting)
	 * @param otherEnemies list of other enemy tanks (can be used for collision
	 *                     avoidance)
	 */
	public void updateAI(List<Wall> walls, List<Bullet> bullets, PlayerTank player, List<EnemyTank> otherEnemies) {
		if (!alive) // Do nothing if the tank is not alive
			return;

		directionTimer--;
		if (directionTimer <= 0) {
			Direction[] Dirs = Direction.values();
			Direction newDirection = Dirs[random.nextInt(Dirs.length)];
			move(newDirection, walls, otherEnemies, player);
			directionTimer = random.nextInt(60) + 30;
		} else {
			if (!move(this.direction, walls, otherEnemies, player)) {
				directionTimer = 0;
			}
		}

		shootTimer--;
		if (shootTimer <= 0) {
			Bullet bullet = shoot();
			if (bullet != null) {
				bullets.add(bullet);
			}
			shootTimer = Constants.ENEMY_MIN_SHOOT_COOLDOWN + random.nextInt(Constants.ENEMY_SHOOT_INTERVAL);
		}
	}
}