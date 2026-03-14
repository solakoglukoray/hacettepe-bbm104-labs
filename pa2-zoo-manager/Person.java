import java.io.PrintWriter;

/**
 * Abstract base for people (Personnel and Visitors). Contains specific person
 * types as public static inner classes.
 */
public abstract class Person {
	protected String name;
	protected int id;

	public Person(String name, int id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public abstract void printAddedInfo(PrintWriter writer);

	// --- Inner classes for specific people ---
	public static class Personnel extends Person {
		public Personnel(String n, int i) {
			super(n, i);
		}

		@Override
		public void printAddedInfo(PrintWriter w) {
			w.println("Added new Personnel with id " + id + " and name " + name + ".");
		}
	}

	public static class Visitor extends Person {
		public Visitor(String n, int i) {
			super(n, i);
		}

		@Override
		public void printAddedInfo(PrintWriter w) {
			w.println("Added new Visitor with id " + id + " and name " + name + ".");
		}
	}
}