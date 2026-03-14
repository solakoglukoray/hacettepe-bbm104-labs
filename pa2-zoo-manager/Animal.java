import java.io.PrintWriter;
import java.text.DecimalFormat;

/**
 * Abstract base for animals, defining common features. Contains specific animal
 * types as public static inner classes.
 */
public abstract class Animal {
	protected String name;
	protected int age;
	protected static DecimalFormat df; // Shared number formatter

	public Animal(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public static void setDecimalFormat(DecimalFormat decimalFormat) {
		df = decimalFormat;
	}

	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}

	public abstract double calculateMealSize();

	public abstract String getFoodType();

	public abstract void cleanHabitat(PrintWriter writer);

	public abstract void printAddedInfo(PrintWriter writer);

	// --- Inner classes for specific animals ---
	public static class Lion extends Animal {
		private static final int BASE_AGE = 5;
		private static final double BASE_MEAL_KG = 5.0;
		private static final double AGE_FACTOR_KG = 0.050;

		public Lion(String n, int a) {
			super(n, a);
		}

		@Override
		public double calculateMealSize() {
			return Math.max(0.1, BASE_MEAL_KG + (age - BASE_AGE) * AGE_FACTOR_KG);
		}

		@Override
		public String getFoodType() {
			return "Meat";
		}

		@Override
		public void cleanHabitat(PrintWriter w) {
			w.println("Cleaning " + name + "'s habitat: Removing bones and refreshing sand.");
		}

		@Override
		public void printAddedInfo(PrintWriter w) {
			w.println("Added new Lion with name " + name + " aged " + age + ".");
		}
	}

	public static class Elephant extends Animal {
		private static final int BASE_AGE = 20;
		private static final double BASE_MEAL_KG = 10.0;
		private static final double AGE_FACTOR_KG = 0.015;

		public Elephant(String n, int a) {
			super(n, a);
		}

		@Override
		public double calculateMealSize() {
			return Math.max(0.1, BASE_MEAL_KG + (age - BASE_AGE) * AGE_FACTOR_KG);
		}

		@Override
		public String getFoodType() {
			return "Plant";
		}

		@Override
		public void cleanHabitat(PrintWriter w) {
			w.println("Cleaning " + name + "'s habitat: Washing the water area.");
		}

		@Override
		public void printAddedInfo(PrintWriter w) {
			w.println("Added new Elephant with name " + name + " aged " + age + ".");
		}
	}

	public static class Penguin extends Animal {
		private static final int BASE_AGE = 4;
		private static final double BASE_MEAL_KG = 3.0;
		private static final double AGE_FACTOR_KG = 0.040;

		public Penguin(String n, int a) {
			super(n, a);
		}

		@Override
		public double calculateMealSize() {
			return Math.max(0.1, BASE_MEAL_KG + (age - BASE_AGE) * AGE_FACTOR_KG);
		}

		@Override
		public String getFoodType() {
			return "Fish";
		}

		@Override
		public void cleanHabitat(PrintWriter w) {
			w.println("Cleaning " + name + "'s habitat: Replenishing ice and scrubbing walls.");
		}

		@Override
		public void printAddedInfo(PrintWriter w) {
			w.println("Added new Penguin with name " + name + " aged " + age + ".");
		}
	}

	public static class Chimpanzee extends Animal {
		private static final int BASE_AGE = 10;
		private static final double BASE_MEAL_KG_TOTAL = 6.0;
		private static final double AGE_FACTOR_TOTAL = 0.025;

		public Chimpanzee(String n, int a) {
			super(n, a);
		}

		@Override
		public double calculateMealSize() {
			return Math.max(0.2, BASE_MEAL_KG_TOTAL + (age - BASE_AGE) * AGE_FACTOR_TOTAL);
		}

		@Override
		public String getFoodType() {
			return "Mixed";
		}

		@Override
		public void cleanHabitat(PrintWriter w) {
			w.println("Cleaning " + name + "'s habitat: Sweeping the enclosure and replacing branches.");
		}

		@Override
		public void printAddedInfo(PrintWriter w) {
			w.println("Added new Chimpanzee with name " + name + " aged " + age + ".");
		}
	}
}