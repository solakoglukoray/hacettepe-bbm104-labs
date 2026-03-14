/**
 * Thrown when an animal with the specified name cannot be found.
 */
public class AnimalNotFoundException extends ZooException {
	public AnimalNotFoundException(String animalName) {

		super("Error: There are no animals with the name " + animalName + ".");
	}
}