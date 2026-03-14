import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
// Import necessary classes explicitly
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Manages the library's items, users, and operations like borrowing and returning.
 * Reads commands from a file and writes output.
 */
class Library {
    // --- Fields ---
    // Use Map for efficient lookup by ID
    private final Map<String, User> users; // Stores users, key is userID, value is User object
    private final Map<String, LibraryItem> items; // Stores items, key is itemID, value is LibraryItem object
    // Used to write output to the specified file
    private final PrintWriter outputWriter;
    // Shared date formatter
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Constructor for the Library.
     * Initializes the user/item maps and sets up the output file writer.
     * @param outputFilePath Path to the file where results will be written.
     * @throws IOException If the output file cannot be opened/created.
     */
    public Library(String outputFilePath) throws IOException {
        this.users = new HashMap<>();
        this.items = new HashMap<>();
        // Prepare the output file
        File outFile = new File(outputFilePath);
        // Create parent directories if they don't exist
        if (outFile.getParentFile() != null) {
            outFile.getParentFile().mkdirs();
        }
        // Create a PrintWriter to write to the file, using UTF-8 encoding for compatibility
        // Set autoFlush to true, so we don't need to manually flush after every write
        this.outputWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"), true);
    }

    // --- File Loading Methods ---

    /**
     * Reads item data from the specified file path and populates the items map.
     * Handles basic parsing and object creation. Silently ignores errors in file format.
     * @param filePath Path to the items input file (e.g., "items.txt").
     */
    public void loadItems(String filePath) {
        // Use try-with-resources to ensure the reader is closed automatically
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"))) {
            String line;
            // Read the file line by line
            while ((line = br.readLine()) != null) {
                line = line.trim(); // Remove leading/trailing whitespace
                // Skip empty lines or lines starting with # (comments)
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                // Split the line by commas
                String[] parts = line.split(",");
                // Trim whitespace from each part
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].trim();
                }
                // Basic check for minimum expected parts
                if (parts.length < 5) {
                    continue; // Skip malformed lines silently
                }

                try {
                    // Extract common data
                    String itemClass = parts[0]; // B, M, or D
                    String id = parts[1];
                    String title = parts[2];
                    String physicalType = parts[parts.length - 1]; // Last part is always the type

                    // Skip if item with this ID already exists
                    if (items.containsKey(id)) {
                        continue;
                    }

                    LibraryItem item = null; // Variable to hold the created item
                    // Create specific item type based on the first part
                    if ("B".equals(itemClass) && parts.length >= 6) {
                        item = new Book(id, title, parts[3], parts[4], physicalType);
                    } else if ("M".equals(itemClass) && parts.length >= 6) {
                        item = new Magazine(id, title, parts[3], parts[4], physicalType);
                    } else if ("D".equals(itemClass) && parts.length >= 7) {
                        // Parse runtime, removing " min" if present
                        String runtimeStr = parts[5].toLowerCase().replace("min", "").trim();
                        int runtime = Integer.parseInt(runtimeStr);
                        item = new DVD(id, title, parts[3], parts[4], runtime, physicalType);
                    }
                    // If an item was successfully created, add it to the map
                    if (item != null) {
                        items.put(id, item);
                    }
                } catch (Exception e) {
                    // Silently ignore errors during parsing or object creation for a specific line
                }
            }
        } catch (IOException e) {
            // Print error to console if the file cannot be read
            System.err.println("Error reading items file: " + filePath + " - " + e.getMessage());
        }
    }

     /**
     * Reads user data from the specified file path and populates the users map.
     * Handles basic parsing and object creation. Silently ignores errors in file format.
     * @param filePath Path to the users input file (e.g., "users.txt").
     */
    public void loadUsers(String filePath) {
        // Use try-with-resources for automatic closing
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"))) {
            String line;
            // Read line by line
            while ((line = br.readLine()) != null) {
                 line = line.trim();
                 // Skip empty lines or comments
                 if (line.isEmpty() || line.startsWith("#")) {
                     continue;
                 }
                 // Split by comma
                 String[] parts = line.split(",");
                 // Trim parts
                 for(int i=0; i<parts.length; i++) parts[i] = parts[i].trim();
                 // Basic check for minimum parts
                 if (parts.length < 4) {
                     continue; // Skip malformed lines silently
                 }

                 try {
                    // Extract common data
                    String userClass = parts[0]; // S, A, or G
                    String name = parts[1];
                    String id = parts[2];
                    String phone = parts[3];

                    // Skip if user with this ID already exists
                    if (users.containsKey(id)) {
                        continue;
                    }

                    User user = null; // Variable to hold created user
                    // Create specific user type
                    if ("S".equals(userClass) && parts.length >= 7) {
                        user = new Student(id, name, phone, parts[4], parts[5], Integer.parseInt(parts[6]));
                    } else if ("A".equals(userClass) && parts.length >= 7) {
                        user = new Academic(id, name, phone, parts[4], parts[5], parts[6]);
                    } else if ("G".equals(userClass) && parts.length >= 5) {
                        user = new Guest(id, name, phone, parts[4]);
                    }
                    // Add successfully created user to map
                    if (user != null) {
                        users.put(id, user);
                    }
                 } catch (Exception e) {
                    // Silently ignore errors for specific lines
                 }
            }
        } catch (IOException e) {
            // Print error to console if file reading fails
            System.err.println("Error reading users file: " + filePath + " - " + e.getMessage());
        }
    }


    // --- Command Processing ---

    /**
     * Reads and processes commands from the specified file path.
     * Executes actions like borrow, return, pay, and display.
     * Performs overdue checks AFTER each command based on the current system date.
     * @param filePath Path to the commands input file (e.g., "commands.txt").
     */
    public void processCommands(String filePath) {
        // Use try-with-resources for automatic closing
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"))) {
            String line;
            // Read commands line by line
            while ((line = br.readLine()) != null) {
                line = line.trim();
                // Skip empty lines or comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                // Split command line by comma
                String[] parts = line.split(",");
                // Trim each part
                for(int i=0; i<parts.length; i++) parts[i] = parts[i].trim();
                // Basic check for command existence
                if (parts.length == 0 || parts[0].isEmpty()) {
                    continue;
                }

                // Get command name (lowercase)
                String command = parts[0].toLowerCase();
                // Get user ID if present
                String userId = (parts.length > 1) ? parts[1] : null;
                // Find the corresponding User object (might be null if ID is invalid)
                User user = (userId != null) ? users.get(userId) : null;

                // Use a try-catch block for potential errors during command execution
                try {
                    // --- Execute the command based on its name ---
                    if ("borrow".equals(command)) {
                        // Check for correct number of parts and valid user
                        if (parts.length >= 4 && user != null) {
                            try {
                                // Parse the borrow date from the command
                                LocalDate borrowAttemptDate = LocalDate.parse(parts[3], DATE_FORMATTER);
                                // Call the borrow handler method
                                handleBorrow(user, parts[2], borrowAttemptDate);
                            } catch (DateTimeParseException e) {
                                // Silently ignore commands with invalid date format
                            }
                        }
                    } else if ("return".equals(command)) {
                        // Check for correct parts and valid user
                        if (parts.length >= 3 && user != null) {
                            handleReturn(user, parts[2]);
                        }
                    } else if ("pay".equals(command)) {
                        // Check for correct parts and valid user
                        if (parts.length >= 2 && user != null) {
                            handlePayPenalty(user);
                        }
                    } else if ("displayusers".equals(command)) {
                        handleDisplayUsers();
                    } else if ("displayitems".equals(command)) {
                        handleDisplayItems();
                    }
                    // Unknown commands are silently ignored

                    // --- IMPORTANT: Overdue Check AFTER every command ---
                    // Checks all users and items based on the CURRENT system date (LocalDate.now())
                    // Updates penalties and item statuses silently if anything is overdue.
                    checkAllOverdueItemsGloballyUsingNow();

                } catch (Exception e) {
                    // Silently ignore any unexpected errors during the processing of a single command
                    // In a real application, these should be logged.
                }
            } // End of while loop (reading commands)
        } catch (IOException e) {
            // Print error to console if command file cannot be read
            System.err.println("Error reading commands file: " + filePath + " - " + e.getMessage());
        } finally {
            // Ensure the output writer is closed even if errors occurred
            close();
        }
    }

    // --- Overdue Item Check Method ---

    /**
     * Checks ALL currently borrowed items for ALL users against the CURRENT system date.
     * If an item is overdue, it's marked as returned, removed from the user's list,
     * and a penalty is added to the user's account. This operates SILENTLY (no output).
     */
    private void checkAllOverdueItemsGloballyUsingNow() {
        LocalDate today = LocalDate.now(); // Get the current date

        // Loop through every user in the system
        for (User user : users.values()) {
            // Get the list of items borrowed by this specific user
            // We need the modifiable list to remove items directly
            List<LibraryItem> userBorrowedItems = user.getModifiableBorrowedItemsInternal();

            // Use an Iterator to safely remove items while looping through the list
            Iterator<LibraryItem> iterator = userBorrowedItems.iterator();
            while (iterator.hasNext()) {
                LibraryItem item = iterator.next();
                // Check if the item has a due date and if today's date is AFTER the due date
                if (item.getDueDate() != null && today.isAfter(item.getDueDate())) {
                    // Item is overdue!
                    iterator.remove();         // Remove the item from the user's list using the iterator
                    item.returned();           // Mark the item itself as returned (available)
                    user.addPenalty(User.OVERDUE_PENALTY); // Add the standard penalty to the user
                }
            }
        }
        // This method produces NO output to the file.
    }

    // --- Command Handler Methods ---

    /**
     * Handles the logic for a borrow request.
     * Assumes the user object is valid. Checks item validity and borrowing rules.
     * Prints success or failure messages to the output file.
     *
     * @param user       The User attempting to borrow.
     * @param itemId     The ID of the item to borrow.
     * @param borrowDate The date the borrow attempt is made (from the command).
     */
    private void handleBorrow(User user, String itemId, LocalDate borrowDate) {
        LibraryItem item = items.get(itemId); // Find the item object
        // If item doesn't exist, do nothing and exit silently
        if (item == null) {
            return;
        }

        // --- Check Borrowing Rules in Order ---

        // 1. Check for penalty block (penalty >= $6)
        if (user.hasPenaltyBlock()) {
            // Use printf for formatted output. %.0f formats double with no decimal places. %s is for strings. %n is newline.
            outputWriter.printf("%s cannot borrow %s, you must first pay the penalty amount! %.0f$%n",
                                user.getName(), item.getName(), user.getPenalty());
            return; // Stop processing if penalty block exists
        }
        // 2. Check if the item is already borrowed by someone else
        if (item.isBorrowed()) {
            outputWriter.printf("%s cannot borrow %s, it is not available!%n",
                                user.getName(), item.getName());
            return; // Stop if item is not available
        }
        // 3. Check if the user type is restricted from borrowing this item type
        if (user.isRestricted(item)) {
            // Only print the specific message for "reference" items, as required.
            if ("reference".equalsIgnoreCase(item.getPhysicalType())) {
                outputWriter.printf("%s cannot borrow reference item!%n", user.getName());
            }
            // Whether a message was printed or not, stop processing if restricted.
            return;
        }
        // 4. Check if the user has reached their borrowing limit
        if (user.hasReachedLimit()) {
            outputWriter.printf("%s cannot borrow %s, since the borrow limit has been reached!%n",
                                user.getName(), item.getName());
            return; // Stop if limit reached
        }

        // --- If all checks passed: Borrowing is successful ---
        item.borrow(user.getId(), borrowDate, user.getOverdueDays()); // Update item state
        user.addBorrowedItem(item); // Add item to user's list
        outputWriter.printf("%s successfully borrowed! %s%n", // Print success message
                            user.getName(), item.getName());
    }

    /**
     * Handles the logic for an explicit return request from the command file.
     * Prints the success message regardless of whether the item was auto-returned just before.
     *
     * @param user   The User attempting to return.
     * @param itemId The ID of the item to return.
     */
    private void handleReturn(User user, String itemId) {
        LibraryItem item = items.get(itemId); // Find the item
        // If item or user is invalid, exit silently
        if (item == null) {
            return;
        }

        // Attempt to remove the item from the user's list.
        // This might fail if it was already auto-removed, but that's okay.
        user.removeBorrowedItem(item);

        // Ensure the item's state is set to 'returned', even if it was already auto-returned.
        // Check if the last known borrower was indeed this user before resetting, just in case.
        if (item.getBorrowerId() != null && item.getBorrowerId().equals(user.getId())) {
             item.returned();
        } else if (!item.isBorrowed()) {
            // If it's already not borrowed, we might still call returned to be safe,
            // or just trust the state. Let's call it to ensure clean state.
             item.returned();
        }


        // Print the success message for the explicit 'return' command.
        outputWriter.printf("%s successfully returned %s%n", user.getName(), item.getName());
    }

    /**
     * Handles the logic for a pay penalty request.
     * Resets the user's penalty to 0 if they have one and prints a confirmation.
     *
     * @param user The User paying the penalty.
     */
    private void handlePayPenalty(User user) {
        // Check if the user actually has a penalty > 0
        if (user.getPenalty() > 0) {
            user.payPenalty(); // Reset penalty to 0
            outputWriter.printf("%s has paid penalty%n", user.getName()); // Print confirmation
        }
        // If penalty is 0, do nothing and print nothing.
    }

    // --- Display Methods (Simplified Sorting) ---

    /**
     * Displays information about all users, sorted by ID.
     */
    private void handleDisplayUsers() {
        if (users.isEmpty()) {
            // Optional: print a message if no users exist
            // outputWriter.println("\n\nNo users in the system.\n");
            return; // Exit if no users
        }

        // Get all User objects from the map
        List<User> sortedUserList = new ArrayList<>(users.values());

        // Sort the list using Collections.sort and a Comparator
        // This compares users based on their IDs (String comparison)
        Collections.sort(sortedUserList, new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return u1.getId().compareTo(u2.getId());
            }
        });
        // Alternative using lambda (slightly more modern but still basic):
        // Collections.sort(sortedUserList, (u1, u2) -> u1.getId().compareTo(u2.getId()));

        // Print the sorted list with required formatting
        outputWriter.println(); // Blank line before the first user
        for (int i = 0; i < sortedUserList.size(); i++) {
             User currentUser = sortedUserList.get(i);
             outputWriter.print(currentUser.getDisplayString()); // User's display string handles formatting
             // Add a blank line between users, but not after the last one
             if (i < sortedUserList.size() - 1) {
                 outputWriter.println();
             }
        }
        outputWriter.println(); // Blank line after the last user
    }

    /**
     * Displays information about all library items, sorted by ID.
     */
    private void handleDisplayItems() {
         if (items.isEmpty()) {
            // Optional: print message if no items
            // outputWriter.println("\n\nNo items in the system.\n");
             return; // Exit if no items
         }

        // Get all LibraryItem objects
        List<LibraryItem> sortedItemList = new ArrayList<>(items.values());

        // Sort the list by item ID using Collections.sort
        Collections.sort(sortedItemList, new Comparator<LibraryItem>() {
            @Override
            public int compare(LibraryItem i1, LibraryItem i2) {
                return i1.getId().compareTo(i2.getId());
            }
        });
        // Alternative lambda:
        // Collections.sort(sortedItemList, (i1, i2) -> i1.getId().compareTo(i2.getId()));


        // Print the sorted list with required formatting
        outputWriter.println(); // Blank line before the first item
        for (int i = 0; i < sortedItemList.size(); i++) {
            LibraryItem currentItem = sortedItemList.get(i);
            // Print Header
            outputWriter.printf("------ Item Information for %s ------%n", currentItem.getId());
            // Print ID, Name, Status line
            outputWriter.printf("ID: %s Name: %s Status: ", currentItem.getId(), currentItem.getName());

            // Determine status string
            if (currentItem.isBorrowed()) {
                // Find the borrower's name for the status message
                User borrower = users.get(currentItem.getBorrowerId());
                String borrowerName;
                // Use if-else instead of ternary operator for simplicity
                if (borrower != null) {
                    borrowerName = borrower.getName();
                } else {
                    borrowerName = "Unknown"; // Should not happen if data is consistent
                }
                // Format borrow date if available
                String borrowDateStr = "N/A";
                if(currentItem.getBorrowDate() != null) {
                    borrowDateStr = currentItem.getBorrowDate().format(DATE_FORMATTER);
                }
                outputWriter.printf("Borrowed Borrowed Date: %s Borrowed by: %s%n",
                                   borrowDateStr, borrowerName);
            } else {
                outputWriter.printf("Available%n");
            }

            // Print item-specific details using instanceof checks
            if (currentItem instanceof Book) {
                Book book = (Book) currentItem; // Cast to Book
                outputWriter.printf("Author: %s Genre: %s%n", book.getAuthor(), book.getGenre());
            } else if (currentItem instanceof Magazine) {
                Magazine mag = (Magazine) currentItem; // Cast to Magazine
                outputWriter.printf("Publisher: %s Category: %s%n", mag.getPublisher(), mag.getCategory());
            } else if (currentItem instanceof DVD) {
                DVD dvd = (DVD) currentItem; // Cast to DVD
                outputWriter.printf("Director: %s Category: %s Runtime: %d min%n",
                                    dvd.getDirector(), dvd.getCategory(), dvd.getRuntime());
            }

            // Add blank line between items, but not after the last one
             if (i < sortedItemList.size() - 1) {
                 outputWriter.println();
             }
        }
         outputWriter.println(); // Blank line after the last item
    }

    // --- Cleanup ---
    /**
     * Closes the output file writer to ensure all data is saved.
     * Should be called when the program finishes.
     */
    public void close() {
        if (outputWriter != null) {
            outputWriter.flush(); // Write any remaining buffered data
            outputWriter.close(); // Close the file
        }
    }
}