import java.io.IOException;
// No need for UnsupportedEncodingException if using standard UTF-8

/**
 * The main entry point for the Library Management System application.
 * Reads file paths from command line arguments and runs the library simulation.
 * Does NOT print progress messages to the console.
 */
public class Main {

    /**
     * The main method executed when the program starts.
     * @param args Command line arguments: items_file users_file commands_file output_file
     */
    public static void main(String[] args) {
        // Check command line arguments
        if (args.length < 4) {
            // Still print usage error to standard error if arguments are wrong
            System.err.println("Usage: java Main <items_file> <users_file> <commands_file> <output_file>");
            System.exit(1); // Exit with error
        }

        // Assign arguments
        String itemsFilePath = args[0];
        String usersFilePath = args[1];
        String commandsFilePath = args[2];
        String outputFilePath = args[3];

        Library library = null; // Declare library variable

        try {
            // 1. Create the Library (Opens output file)
            library = new Library(outputFilePath);

            // 2. Load data
            library.loadItems(itemsFilePath);
            library.loadUsers(usersFilePath);

            library.processCommands(commandsFilePath); // Runs the simulation


        } catch (IOException e) {
            // Print file errors to standard error
            System.err.println("Error during File I/O: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
             // Print other unexpected errors to standard error
             System.err.println("An unexpected error occurred: " + e.getMessage());
             e.printStackTrace();
             System.exit(1);
        } finally {
            // Ensure the output file is closed
            if (library != null) {
                library.close();
            }
        }
        System.exit(0);
    }
}