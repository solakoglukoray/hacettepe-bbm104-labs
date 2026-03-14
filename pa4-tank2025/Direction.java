/**
 * Represents the possible movement directions for tanks and bullets, including
 * their delta X/Y values and rotation angle for sprites.
 */
public enum Direction {
	UP(0, -1, 270), // Moves up
	DOWN(0, 1, 90), // Moves down
	LEFT(-1, 0, 180), // Moves left
	RIGHT(1, 0, 0); // Moves right

	private final int dx; // Change in x-coordinate
	private final int dy; // Change in y-coordinate
	private final int rotation; // Rotation angle in degrees for the sprite

	Direction(int dx, int dy, int rotation) {
		this.dx = dx;
		this.dy = dy;
		this.rotation = rotation;
	}

	public int getDx() {
		return dx;
	}

	public int getDy() {
		return dy;
	}

	public int getRotation() {
		return rotation;
	}
}