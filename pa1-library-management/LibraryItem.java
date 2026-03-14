import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents a general item in the library.
 * This is an abstract class, meaning you can't create a direct LibraryItem object.
 * Instead, you create specific items like Book, Magazine, or DVD which inherit from this.
 */
abstract class LibraryItem {
    // --- Class Variables (Fields) ---

    // Final means these cannot be changed after the object is created
    private final String id;           // Unique identifier (e.g., "1001")
    private final String name;         // Title of the item (e.g., "The Great Gatsby")
    private final String itemType;     // General type ("Book", "Magazine", "DVD")
    private final String physicalType; // Borrowing type ("normal", "reference", "rare", "limited")

    // These can change based on borrowing actions
    private boolean isBorrowed;     // Is the item currently checked out?
    private String borrowerId;      // ID of the user who borrowed it (null if not borrowed)
    private LocalDate borrowDate;   // Date it was borrowed (null if not borrowed)
    private LocalDate dueDate;      // Date it is due back (null if not borrowed)

    // A shared tool to format dates like "dd/MM/yyyy"
    protected static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Constructor: Used to create a new LibraryItem object.
     * It initializes the basic properties.
     *
     * @param id           The unique ID.
     * @param name         The title.
     * @param itemType     The general type ("Book", "Magazine", "DVD").
     * @param physicalType The borrowing type ("normal", etc.).
     */
    public LibraryItem(String id, String name, String itemType, String physicalType) {
        this.id = id;
        this.name = name;
        this.itemType = itemType;
        // Convert physical type to lowercase and remove extra spaces for consistency
        this.physicalType = physicalType.toLowerCase().trim();
        this.isBorrowed = false; // Starts as not borrowed
        // Other fields start as null (or default for boolean)
    }

    // --- Getter Methods ---
    // These methods allow other parts of the code to safely get the values of the private fields.

    public String getId() { return id; }
    public String getName() { return name; }
    public String getItemType() { return itemType; }
    public String getPhysicalType() { return physicalType; }
    public boolean isBorrowed() { return isBorrowed; }
    public String getBorrowerId() { return borrowerId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }

    // --- Methods to Change State ---

    /**
     * Marks the item as borrowed by a specific user.
     * Calculates the due date based on the user's allowed borrowing period.
     *
     * @param userId      The ID of the borrowing user.
     * @param borrowDate  The date the item is borrowed.
     * @param overdueDays The number of days the user type can keep the item.
     */
    public void borrow(String userId, LocalDate borrowDate, int overdueDays) {
        this.isBorrowed = true;
        this.borrowerId = userId;
        this.borrowDate = borrowDate;
        this.dueDate = borrowDate.plusDays(overdueDays); // Calculate due date
    }

    /**
     * Marks the item as returned to the library.
     * Resets borrowing information.
     */
    public void returned() {
        this.isBorrowed = false;
        this.borrowerId = null;
        this.borrowDate = null;
        this.dueDate = null;
    }

    // --- Standard Java Object Methods ---

    /**
     * Checks if two LibraryItem objects are considered equal (based on their ID).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Same object in memory
        if (o == null || getClass() != o.getClass()) return false; // Different type or null
        LibraryItem otherItem = (LibraryItem) o; // Cast to LibraryItem
        return Objects.equals(this.id, otherItem.id); // Compare IDs
    }

    /**
     * Generates a hash code for the item, used in data structures like HashMap.
     * Based on the unique ID.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id); // Use ID for hashing
    }

    // Note: The specific getDisplayString() method is removed from the item classes.
    // Display formatting is now handled entirely within the Library class's display methods.
}