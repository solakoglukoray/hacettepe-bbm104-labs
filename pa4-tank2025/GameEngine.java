import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.util.*;

/**
 * Core class managing the game's lifecycle, objects, interactions, and UI.
 */
public class GameEngine {
	private Pane root;
	private Scene scene;
	private AnimationTimer gameLoop;

	// Game state
	private GameState gameState;
	private int score;

	// Game objects
	private PlayerTank player;
	private List<EnemyTank> enemies;
	private List<Bullet> bullets;
	private List<Wall> walls;
	private List<Explosion> explosions;

	// UI elements
	private Text scoreText;
	private Text livesText;
	private Text gameOverText;
	private Text pauseText;

	// Controls and timers
	private Set<KeyCode> pressedKeys;
	private int enemySpawnTimer;
	private Random random;

	/**
	 * Constructor for GameEngine.
	 * 
	 * @param root  JavaFX root pane
	 * @param scene JavaFX scene
	 */
	public GameEngine(Pane root, Scene scene) {
		this.root = root;
		this.scene = scene;
		this.random = new Random();
		this.pressedKeys = new HashSet<>();

		initializeGameVariables();
		initializeGame();
		setupControls();
	}

	private void initializeGameVariables() {
		this.gameState = GameState.PLAYING;
		this.score = 0;
		this.enemies = new ArrayList<>();
		this.bullets = new ArrayList<>();
		this.walls = new ArrayList<>();
		this.explosions = new ArrayList<>();
		this.enemySpawnTimer = Constants.ENEMY_SPAWN_INTERVAL;
	}

	/**
	 * Starts the main game loop.
	 */
	public void start() {
		gameLoop = new AnimationTimer() {
			@Override
			public void handle(long now) {
				update();
			}
		};
		gameLoop.start();
	}

	/**
	 * Sets up the initial state of the game for a new game or restart.
	 */
	private void initializeGame() {
		root.getChildren().clear();

		createWalls();
		for (Wall wall : walls) {
			if (wall.getSprite() != null) {
				root.getChildren().add(wall.getSprite());
			}
		}

		player = new PlayerTank(Constants.PLAYER_START_X, Constants.PLAYER_START_Y, root);
		if (player.getSprite() != null) {
			root.getChildren().add(player.getSprite());
		}

		createUI();
		root.getChildren().addAll(scoreText, livesText, gameOverText, pauseText);
	}

	/**
	 * Creates the wall layout for the game map.
	 */
	private void createWalls() {
		walls.clear();
		// Outer boundary walls
		for (int x = 0; x < Constants.WINDOW_WIDTH; x += Constants.WALL_SIZE) {
			walls.add(new Wall(x, 0));
			walls.add(new Wall(x, Constants.WINDOW_HEIGHT - Constants.WALL_SIZE));
		}
		for (int y = Constants.WALL_SIZE; y < Constants.WINDOW_HEIGHT - Constants.WALL_SIZE; y += Constants.WALL_SIZE) {
			walls.add(new Wall(0, y));
			walls.add(new Wall(Constants.WINDOW_WIDTH - Constants.WALL_SIZE, y));
		}

		// Inner walls
		walls.add(new Wall(150, 150));
		walls.add(new Wall(180, 150));
		walls.add(new Wall(150, 180));

		walls.add(new Wall(500, 200));
		walls.add(new Wall(530, 200));
		walls.add(new Wall(500, 230));

		walls.add(new Wall(300, 350));
		walls.add(new Wall(330, 350));
		walls.add(new Wall(360, 350));
	}

	/**
	 * Creates the UI text elements.
	 */
	private void createUI() {
		scoreText = new Text(50, 50, "Score: 0");
		scoreText.setFont(Font.font(16));
		scoreText.setFill(Color.WHITE);

		livesText = new Text(50, 80, "Lives: " + Constants.PLAYER_LIVES);
		livesText.setFont(Font.font(16));
		livesText.setFill(Color.WHITE);

		gameOverText = new Text(Constants.WINDOW_WIDTH / 2.0 - 150, Constants.WINDOW_HEIGHT / 2.0 - 50,
				"GAME OVER\nScore: 0\nPress R to restart\nPress ESC to exit");
		gameOverText.setFont(Font.font(24));
		gameOverText.setFill(Color.RED);
		gameOverText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
		gameOverText.setVisible(false);

		pauseText = new Text(Constants.WINDOW_WIDTH / 2.0 - 150, Constants.WINDOW_HEIGHT / 2.0 - 50,
				"PAUSED\nPress P to resume\nPress R to restart\nPress ESC to exit");
		pauseText.setFont(Font.font(24));
		pauseText.setFill(Color.YELLOW);
		pauseText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
		pauseText.setVisible(false);
	}

	/**
	 * Sets up keyboard event handlers.
	 */
	private void setupControls() {
		scene.setOnKeyPressed(event -> {
			pressedKeys.add(event.getCode());

			if (gameState == GameState.GAME_OVER) {
				if (event.getCode() == KeyCode.R)
					restartGame();
				else if (event.getCode() == KeyCode.ESCAPE)
					System.exit(0);
				return;
			}
			if (gameState == GameState.PAUSED) {
				if (event.getCode() == KeyCode.P)
					togglePause();
				else if (event.getCode() == KeyCode.R)
					restartGame();
				else if (event.getCode() == KeyCode.ESCAPE)
					System.exit(0);
				return;
			}

			// Key presses for PLAYING state (pause, restart, exit)
			if (event.getCode() == KeyCode.P)
				togglePause();
			else if (event.getCode() == KeyCode.R)
				restartGame();
			else if (event.getCode() == KeyCode.ESCAPE)
				System.exit(0);

			if (event.getCode() == KeyCode.X) {
				if (player.isAlive() && !player.isRespawning()) {
					Bullet bullet = player.shoot();
					if (bullet != null && bullet.getSprite() != null) {
						bullets.add(bullet);
						root.getChildren().add(bullet.getSprite());
					}
				}
			}
		});

		scene.setOnKeyReleased(event -> {
			pressedKeys.remove(event.getCode());
		});
	}

	/**
	 * Main game update method, called every frame.
	 */
	private void update() {
		if (gameState == GameState.PAUSED || gameState == GameState.GAME_OVER)
			return;

		updatePlayer();
		updateEnemies();
		updateBullets();
		updateExplosions();
		checkCollisions();
		updateUI();
		updateEnemySpawn();
		cleanupInactiveObjects();
	}

	/**
	 * Updates the player's state and handles movement.
	 */
	private void updatePlayer() {
		if (!player.isAlive() && !player.isRespawning()) {
			gameState = GameState.GAME_OVER;
			showGameOver();
			return;
		}
		player.updateRespawn();
		if (!player.isAlive() || player.isRespawning())
			return;

		// Player movement based on currently pressed keys
		if (pressedKeys.contains(KeyCode.UP))
			player.move(Direction.UP, walls, enemies, null);
		else if (pressedKeys.contains(KeyCode.DOWN))
			player.move(Direction.DOWN, walls, enemies, null);
		else if (pressedKeys.contains(KeyCode.LEFT))
			player.move(Direction.LEFT, walls, enemies, null);
		else if (pressedKeys.contains(KeyCode.RIGHT))
			player.move(Direction.RIGHT, walls, enemies, null);
	}

	/**
	 * Updates all active enemy tanks.
	 */
	private void updateEnemies() {
		for (EnemyTank enemy : enemies) {
			if (enemy.isAlive()) {
				enemy.updateAI(walls, bullets, player, enemies);
			}
		}
	}

	/**
	 * Updates all active bullets and adds new ones to the scene.
	 */
	private void updateBullets() {
		Iterator<Bullet> bulletIt = bullets.iterator();
		while (bulletIt.hasNext()) {
			Bullet bullet = bulletIt.next();
			if (bullet.isActive()) {
				bullet.update();
				if (bullet.getSprite() != null && !root.getChildren().contains(bullet.getSprite())) {
					root.getChildren().add(bullet.getSprite()); // Ensures new bullets are visible
				}
			}
		}
	}

	/**
	 * Updates active explosion effects, ensuring they are added to the scene.
	 */
	private void updateExplosions() {
		Iterator<Explosion> expIt = explosions.iterator();
		while (expIt.hasNext()) {
			Explosion explosion = expIt.next();
			if (explosion.isFinished()) {
				if (explosion.getSprite() != null) {
					root.getChildren().remove(explosion.getSprite());
				}
				expIt.remove();
			} else {
				if (explosion.getSprite() != null && !root.getChildren().contains(explosion.getSprite())) {
					root.getChildren().add(explosion.getSprite());
					explosion.play();
				}
			}
		}
	}

	/**
	 * Checks for and handles collisions between game objects.
	 */
	private void checkCollisions() {
		// Bullet-Wall collisions
		for (Bullet bullet : bullets) {
			if (!bullet.isActive())
				continue;
			for (Wall wall : walls) {
				if (bullet.getSprite() != null && wall.getSprite() != null
						&& bullet.getSprite().getBoundsInParent().intersects(wall.getSprite().getBoundsInParent())) {
					bullet.destroy(true);
					break;
				}
			}
		}

		// Bullet-Tank collisions
		for (Bullet bullet : bullets) {
			if (!bullet.isActive())
				continue;
			if (bullet.isPlayerBullet()) {
				for (EnemyTank enemy : enemies) {
					if (enemy.isAlive() && bullet.getSprite() != null && enemy.getSprite() != null && bullet.getSprite()
							.getBoundsInParent().intersects(enemy.getSprite().getBoundsInParent())) {
						bullet.destroy(false);
						enemy.destroy();
						score += 100;
						break;
					}
				}
			} else {
				if (player.isAlive() && !player.isRespawning() && bullet.getSprite() != null
						&& player.getSprite() != null
						&& bullet.getSprite().getBoundsInParent().intersects(player.getSprite().getBoundsInParent())) {
					bullet.destroy(false);
					player.hit();
				}
			}
		}

		// Player Tank - Enemy Tank collision
		if (player.isAlive() && !player.isRespawning()) {
			for (EnemyTank enemy : enemies) {
				if (enemy.isAlive() && player.getSprite() != null && enemy.getSprite() != null
						&& player.getSprite().getBoundsInParent().intersects(enemy.getSprite().getBoundsInParent())) {
					player.hit();
					enemy.destroy();
					break;
				}
			}
		}
	}

	/**
	 * Updates the UI text elements.
	 */
	private void updateUI() {
		scoreText.setText("Score: " + score);
		if (player != null) {
			livesText.setText("Lives: " + player.getLives());
		}
	}

	/**
	 * Handles the spawning of new enemy tanks.
	 */
	private void updateEnemySpawn() {
		if (enemies.size() >= 5)
			return;

		enemySpawnTimer--;
		if (enemySpawnTimer <= 0) {
			spawnEnemy();
			enemySpawnTimer = Constants.ENEMY_SPAWN_INTERVAL + random.nextInt(60);
		}
	}

	/**
	 * Creates a new enemy tank at a random valid position.
	 */
	private void spawnEnemy() {
		double x, y;
		boolean validPosition;
		int attempts = 0;

		do { // Tries to find a clear spawning spot
			validPosition = true;
			x = Constants.WALL_SIZE
					+ random.nextInt(Constants.WINDOW_WIDTH - 2 * Constants.WALL_SIZE - Constants.TANK_SIZE);
			y = Constants.WALL_SIZE + random.nextInt(100);

			javafx.scene.shape.Rectangle testRect = new javafx.scene.shape.Rectangle(x, y, Constants.TANK_SIZE,
					Constants.TANK_SIZE);

			for (Wall wall : walls) {
				if (wall.getSprite() != null
						&& testRect.getBoundsInParent().intersects(wall.getSprite().getBoundsInParent())) {
					validPosition = false;
					break;
				}
			}
			if (validPosition) {
				for (EnemyTank existingEnemy : enemies) {
					if (existingEnemy.getSprite() != null
							&& testRect.getBoundsInParent().intersects(existingEnemy.getSprite().getBoundsInParent())) {
						validPosition = false;
						break;
					}
				}
			}
			if (validPosition && player.getSprite() != null) {
				if (testRect.getBoundsInParent().intersects(player.getSprite().getBoundsInParent())) {
					validPosition = false;
				}
			}
			attempts++;
		} while (!validPosition && attempts < 20);

		if (validPosition) {
			EnemyTank enemy = new EnemyTank(x, y, root);
			if (enemy.getSprite() != null) {
				enemies.add(enemy);
				root.getChildren().add(enemy.getSprite());
			}
		}
	}

	/**
	 * Removes inactive (destroyed) game objects from lists and the scene.
	 */
	private void cleanupInactiveObjects() {
		bullets.removeIf(bullet -> {
			if (!bullet.isActive() && bullet.getSprite() != null) {
				root.getChildren().remove(bullet.getSprite());
				return true;
			}
			return false;
		});

		enemies.removeIf(enemy -> {
			if (!enemy.isAlive() && enemy.getSprite() != null) {
				root.getChildren().remove(enemy.getSprite());
				return true;
			}
			return false;
		});

		explosions.removeIf(Explosion::isFinished);
	}

	/**
	 * Toggles the game state between PLAYING and PAUSED.
	 */
	private void togglePause() {
		if (gameState == GameState.PLAYING) {
			gameState = GameState.PAUSED;
			pauseText.setVisible(true);
		} else if (gameState == GameState.PAUSED) {
			gameState = GameState.PLAYING;
			pauseText.setVisible(false);
		}
	}

	/**
	 * Restarts the game by re-initializing game variables and objects.
	 */
	private void restartGame() {
		bullets.forEach(b -> {
			if (b.getSprite() != null)
				root.getChildren().remove(b.getSprite());
		});
		bullets.clear();
		enemies.forEach(e -> {
			if (e.getSprite() != null)
				root.getChildren().remove(e.getSprite());
		});
		enemies.clear();
		explosions.forEach(ex -> {
			if (ex.getSprite() != null)
				root.getChildren().remove(ex.getSprite());
		});
		explosions.clear();
		if (player != null && player.getSprite() != null)
			root.getChildren().remove(player.getSprite());

		initializeGameVariables();
		initializeGame();

		gameOverText.setVisible(false);
		pauseText.setVisible(false);
	}

	/**
	 * Displays the "GAME OVER" message.
	 */
	private void showGameOver() {
		gameOverText.setText("GAME OVER\nScore: " + score + "\nPress R to restart\nPress ESC to exit");
		gameOverText.setVisible(true);
	}
}