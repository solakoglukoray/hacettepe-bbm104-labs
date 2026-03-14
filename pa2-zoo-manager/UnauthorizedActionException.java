/**
 * Thrown when a person attempts an action they are not permitted to do (e.g.,
 * Visitor feeding).
 */
public class UnauthorizedActionException extends ZooException {
	public UnauthorizedActionException(String reason) {
		super("Error: " + reason);
	}
}