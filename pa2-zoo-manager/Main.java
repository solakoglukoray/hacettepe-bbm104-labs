import java.io.*;
import java.nio.charset.StandardCharsets; // For UTF-8
import java.text.DecimalFormat; // For number formatting
import java.text.DecimalFormatSymbols; // For decimal point
import java.util.HashMap; // For data storage
import java.util.Locale; // For number formatting
import java.util.Map; // Type for storage

/**
 * Main simulation class for the Zoo Manager. Reads input, processes commands,
 * and writes formatted output.
 */
public class Main {

	private Map<String, Animal> animals = new HashMap<>();
	private Map<Integer, Person> people = new HashMap<>();
	private Map<String, Double> foodStock = new HashMap<>();

	private PrintWriter writer;
	private DecimalFormat df;
	private static final String SEPARATOR = "***********************************";

	public Main() {
		df = new DecimalFormat("0.000", new DecimalFormatSymbols(Locale.US));
	}

	/** Initializes the zoo state from files. */
	public void initialize(String animalsFile, String personsFile, String foodsFile, String outputFile)
			throws IOException {
		// Use OutputStreamWriter for UTF-8 writing
		this.writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8));
		Animal.setDecimalFormat(df); // Use static method on Animal class
		initializeAnimals(animalsFile);
		initializePersons(personsFile);
		initializeFoods(foodsFile);
	}

	/** Reads animal data. */
	private void initializeAnimals(String filename) throws IOException {
		writer.println(SEPARATOR);
		writer.println("***Initializing Animal information***");
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				try {
					String[] parts = line.split(",");
					if (parts.length == 3) {
						String type = parts[0].trim();
						String name = parts[1].trim();
						int age = Integer.parseInt(parts[2].trim());
						Animal animal = null;
						if (type.equals("Lion")) {
							animal = new Animal.Lion(name, age);
						} else if (type.equals("Elephant")) {
							animal = new Animal.Elephant(name, age);
						} else if (type.equals("Penguin")) {
							animal = new Animal.Penguin(name, age);
						} else if (type.equals("Chimpanzee")) {
							animal = new Animal.Chimpanzee(name, age);
						}
						if (animal != null) {
							animals.put(name, animal);
							animal.printAddedInfo(writer);
						}
					}
				} catch (Exception ignore) {
				}
			}
		}
	}

	/** Reads person data. */
	private void initializePersons(String filename) throws IOException {
		writer.println(SEPARATOR);
		writer.println("***Initializing Visitor and Personnel information***");
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				try {
					String[] parts = line.split(",");
					if (parts.length == 3) {
						String type = parts[0].trim();
						String name = parts[1].trim();
						int id = Integer.parseInt(parts[2].trim());
						Person person = null;
						if (type.equals("Visitor")) {
							person = new Person.Visitor(name, id);
						} else if (type.equals("Personnel")) {
							person = new Person.Personnel(name, id);
						}
						if (person != null && !people.containsKey(id)) {
							people.put(id, person);
							person.printAddedInfo(writer);
						}
					}
				} catch (Exception ignore) {
				}
			}
		}
	}

	/** Reads food data. */
	private void initializeFoods(String filename) throws IOException {
		writer.println(SEPARATOR);
		writer.println("***Initializing Food Stock***");
		foodStock.put("Meat", 0.0);
		foodStock.put("Plant", 0.0);
		foodStock.put("Fish", 0.0);
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				try {
					String[] parts = line.split(",");
					if (parts.length == 2) {
						String type = parts[0].trim();
						double amount = Double.parseDouble(parts[1].trim());
						if (foodStock.containsKey(type)) {
							foodStock.put(type, amount);
						}
					}
				} catch (Exception ignore) {
				}
			}
		}

		writer.println("There are " + df.format(foodStock.get("Meat")) + " kg of Meat in stock");
		writer.println("There are " + df.format(foodStock.get("Fish")) + " kg of Fish in stock");
		writer.println("There are " + df.format(foodStock.get("Plant")) + " kg of Plant in stock");
	}

	/** Processes commands from the command file. */
	public void processCommands(String filename) {
		String currentCommand = null;
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				currentCommand = line;
				writer.println(SEPARATOR);
				writer.println("***Processing new Command***");
				try {
					String[] parts = line.split(",");
					if (parts.length > 0 && !parts[0].trim().isEmpty()) {
						String commandType = parts[0].trim();
						if (commandType.equals("List Food Stock")) {
							handleListFoodStock();
						} else if (commandType.equals("Animal Visitation")) {
							if (parts.length == 3)
								handleVisitAnimal(parts);
							else
								throw new IllegalArgumentException("Invalid Animal Visitation command format.");
						} else if (commandType.equals("Feed Animal")) {
							if (parts.length == 4)
								handleFeedAnimal(parts);
							else
								throw new IllegalArgumentException("Invalid Feed Animal command format.");
						}
					}
				} catch (NumberFormatException e) {
					writer.println("Error processing command: " + currentCommand);
					String problematicInput = e.getMessage().substring(e.getMessage().indexOf('"') + 1,
							e.getMessage().lastIndexOf('"'));
					writer.println("Error:For input string: \"" + problematicInput + "\"");

				} catch (ZooException | IllegalArgumentException e) {
					writer.println(e.getMessage());
				} catch (Exception ignore) {
				}
			}
		} catch (IOException ignore) {
		} finally {
			closeWriter();
		}
	}

	// Handles the "List Food Stock" command.
	private void handleListFoodStock() {
		writer.println("Listing available Food Stock:");
		writer.println("Plant: " + df.format(foodStock.get("Plant")) + " kgs");
		writer.println("Fish: " + df.format(foodStock.get("Fish")) + " kgs");
		writer.println("Meat: " + df.format(foodStock.get("Meat")) + " kgs");
	}

	// handles the "Animal Visitation" command.
	private void handleVisitAnimal(String[] parts) throws ZooException, NumberFormatException {
		int personId = Integer.parseInt(parts[1].trim());
		String animalName = parts[2].trim();
		Person person = findPersonById(personId);

		if (person instanceof Person.Personnel) {
			writer.println(person.getName() + " attempts to clean " + animalName + "'s habitat.");
			Animal animal = findAnimalByName(animalName);
			writer.println(person.getName() + " started cleaning " + animal.getName() + "'s habitat.");
			animal.cleanHabitat(writer);
		} else if (person instanceof Person.Visitor) {
			Animal animal = findAnimalByName(animalName);
			writer.println(person.getName() + " tried  to register for a visit to " + animal.getName() + ".");
			writer.println(person.getName() + " successfully visited " + animal.getName() + ".");
		}
	}

	// Handles the "Feed Animal" command.
	private void handleFeedAnimal(String[] parts) throws ZooException, NumberFormatException {
		int personId = Integer.parseInt(parts[1].trim());
		String animalName = parts[2].trim();
		int numberOfMeals = Integer.parseInt(parts[3].trim());

		if (numberOfMeals <= 0)
			return;

		Person person = findPersonById(personId);
		Animal animal = findAnimalByName(animalName);

		if (person instanceof Person.Visitor) {
			writer.println(person.getName() + " tried to feed " + animal.getName());
			throw new UnauthorizedActionException("Visitors do not have the authority to feed animals.");
		}

		writer.println(person.getName() + " attempts to feed " + animal.getName() + ".");

		double mealSize = animal.calculateMealSize();
		double totalFoodExact = mealSize * numberOfMeals;

		if (animal instanceof Animal.Chimpanzee) {
			double meatExact = totalFoodExact / 2.0;
			double plantExact = totalFoodExact / 2.0;
			double meatRounded = Math.round(meatExact * 1000.0) / 1000.0;
			double plantRounded = Math.round(plantExact * 1000.0) / 1000.0;
			if (foodStock.get("Meat") < meatRounded)
				throw new NotEnoughFoodException("Meat");
			if (foodStock.get("Plant") < plantRounded)
				throw new NotEnoughFoodException("Plant");
			foodStock.put("Meat", foodStock.get("Meat") - meatRounded);
			foodStock.put("Plant", foodStock.get("Plant") - plantRounded);
			writer.println(animal.getName() + " has been given " + df.format(meatExact) + " kgs of meat and "
					+ df.format(plantExact) + " kgs of leaves");
		} else {
			String foodType = animal.getFoodType();
			double totalFoodRounded = Math.round(totalFoodExact * 1000.0) / 1000.0;
			if (foodStock.get(foodType) < totalFoodRounded)
				throw new NotEnoughFoodException(foodType);
			foodStock.put(foodType, foodStock.get(foodType) - totalFoodRounded);

			if (animal instanceof Animal.Elephant) {
				writer.println(animal.getName() + " has been given " + df.format(totalFoodExact)
						+ " kgs assorted fruits and hay");
			} else if (animal instanceof Animal.Penguin) {
				writer.println(animal.getName() + " has been given " + df.format(totalFoodExact)
						+ " kgs of various kinds of fish");
			} else {
				writer.println(animal.getName() + " has been given " + df.format(totalFoodExact) + " kgs of "
						+ foodType.toLowerCase());
			}
		}
	}

	/** Finds person by ID or throws. */
	private Person findPersonById(int id) throws PersonNotFoundException {
		Person p = people.get(id);
		if (p == null)
			throw new PersonNotFoundException(id);
		return p;
	}

	/** Finds animal by name or throws. */
	private Animal findAnimalByName(String name) throws AnimalNotFoundException {
		Animal a = animals.get(name);
		if (a == null)
			throw new AnimalNotFoundException(name);
		return a;
	}

	// --- closeWriter --- (No change needed)
	private void closeWriter() {
		if (writer != null) {
			writer.close();
			writer = null;
		}
	}

	// --- main --- (No change needed)
	public static void main(String[] args) {
		if (args.length != 5)
			return;
		String animalsFile = args[0], personsFile = args[1], foodsFile = args[2], commandsFile = args[3],
				outputFile = args[4];
		Main zooManager = new Main();
		try {
			zooManager.initialize(animalsFile, personsFile, foodsFile, outputFile);
			zooManager.processCommands(commandsFile);
		} catch (Exception e) {
			zooManager.closeWriter();
		}
	}
}