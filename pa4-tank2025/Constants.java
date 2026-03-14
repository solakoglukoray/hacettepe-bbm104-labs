/**
 * Defines constant values used throughout the game.
 */
public class Constants {
	// Window dimensions
	public static final int WINDOW_WIDTH = 800;
	public static final int WINDOW_HEIGHT = 600;

	// Tank properties
	public static final int TANK_SIZE = 30; // Size of the tank sprite
	public static final int TANK_SPEED = 1; // Movement speed of tanks (pixels per frame)
	public static final int TANK_ANIMATION_FRAMES = 10; // Frames between tank animation sprite changes

	// Bullet properties
	public static final int BULLET_SIZE = 10; // Size of the bullet sprite
	public static final int BULLET_SPEED = 3; // Movement speed of bullets (pixels per frame)
	public static final double PLAYER_BULLET_COOLDOWN = 0.5; // Cooldown time for player shooting in seconds
	public static final int ENEMY_MIN_SHOOT_COOLDOWN = 90; // Minimum frames enemy waits before shooting again

	// Wall properties
	public static final int WALL_SIZE = 30; // Size of the wall sprite

	// Game settings
	public static final int PLAYER_LIVES = 3; // Initial number of player lives
	public static final int ENEMY_SPAWN_INTERVAL = 180; // Base interval for spawning new enemies (frames)
	public static final int ENEMY_SHOOT_INTERVAL = 120; // Base interval for enemy shooting (frames, max random range)
	public static final int RESPAWN_TIME = 120; // Time player is invulnerable and invisible after being hit (frames)

	// Starting positions
	public static final int PLAYER_START_X = 400;
	public static final int PLAYER_START_Y = 500;

	// Image file paths
	public static final String PLAYER_TANK_1_IMAGE_PATH = "/yellowTank1.png";
	public static final String PLAYER_TANK_2_IMAGE_PATH = "/yellowTank2.png";
	public static final String ENEMY_TANK_1_IMAGE_PATH = "/whiteTank1.png";
	public static final String ENEMY_TANK_2_IMAGE_PATH = "/whiteTank2.png";
	public static final String BULLET_IMAGE_PATH = "/bullet.png";
	public static final String WALL_IMAGE_PATH = "/wall.png";
	public static final String EXPLOSION_IMAGE_PATH = "/explosion.png"; // Large explosion
	public static final String SMALL_EXPLOSION_IMAGE_PATH = "/smallExplosion.png"; // Small explosion
																							// (bullet-wall)
	// Explosion sizes
	public static final int EXPLOSION_SIZE = 60;
	public static final int SMALL_EXPLOSION_SIZE = 30;
}