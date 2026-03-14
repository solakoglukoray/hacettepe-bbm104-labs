/**
 * Thrown when a person with the specified ID cannot be found.
 */
public class PersonNotFoundException extends ZooException {
	public PersonNotFoundException(int personId) {
		super("Error: There are no visitors or personnel with the id " + personId);
	}
}