/**
 * Thrown when there isn't enough food in stock to feed an animal.
 */
public class NotEnoughFoodException extends ZooException {
	public NotEnoughFoodException(String foodType) {
		super("Error: Not enough " + foodType);
	}
}