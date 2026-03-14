import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a general library user.
 * This is an abstract class for different user types (Student, Academic, Guest).
 */
abstract class User {
    // --- Fields ---
    private final String id;
    private final String name;
    private final String phoneNumber;
    private double penalty; // Current penalty amount, starts at 0
    // List to keep track of items currently borrowed by this user
    // Declared as List, implemented as ArrayList for flexibility
    private final List<LibraryItem> borrowedItems;

    // --- Constants (shared rules/values) ---
    // Protected means they can be accessed by User and its subclasses (Student, etc.)
    protected static final double PENALTY_THRESHOLD = 6.0; // Cannot borrow if penalty is this amount or more
    protected static final double OVERDUE_PENALTY = 2.0;   // Penalty added for each overdue item

    /**
     * Constructor for creating a User object.
     * Initializes common user properties.
     *
     * @param id          Unique user ID.
     * @param name        User's name.
     * @param phoneNumber User's phone number.
     */
    public User(String id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.penalty = 0.0; // Start with no penalty
        this.borrowedItems = new ArrayList<>(); // Create an empty list to hold borrowed items
    }

    // --- Getters ---
    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public double getPenalty() { return penalty; }

    /**
     * Gets the list of borrowed items.
     * Returns an "unmodifiable" list, meaning the code outside this class
     * cannot directly add or remove items from the returned list, ensuring safety.
     */
    public List<LibraryItem> getBorrowedItems() {
        return Collections.unmodifiableList(borrowedItems);
    }

    /**
     * Internal method (protected) to get the actual list for modification
     * ONLY by trusted classes (like the Library class for handling returns).
     */
    protected List<LibraryItem> getModifiableBorrowedItemsInternal() {
        return borrowedItems;
    }


    // --- Abstract Methods (Must be implemented by subclasses) ---
    /** Returns the maximum number of items this user type can borrow. */
    public abstract int getMaxItems();
    /** Returns the number of days this user type can keep an item before it's overdue. */
    public abstract int getOverdueDays();
    /** Checks if this user type is restricted from borrowing a specific item. */
    public abstract boolean isRestricted(LibraryItem item);


    // --- Common User Methods ---
    /** Checks if the user's penalty is high enough to block borrowing. */
    public boolean hasPenaltyBlock() {
        return this.penalty >= PENALTY_THRESHOLD;
    }

    /** Checks if the user has already borrowed the maximum allowed number of items. */
    public boolean hasReachedLimit() {
        // Compare current number of borrowed items to the max allowed for this user type
        return this.borrowedItems.size() >= getMaxItems();
    }

    /** Adds an item to the user's internal list of borrowed items. */
    public void addBorrowedItem(LibraryItem item) {
        // Avoid adding duplicates if item somehow already exists
        if (!borrowedItems.contains(item)) {
            borrowedItems.add(item);
        }
    }

    /** Removes an item from the user's internal list of borrowed items. */
    public boolean removeBorrowedItem(LibraryItem item) {
       // List.remove returns true if the item was found and removed, false otherwise
       return borrowedItems.remove(item);
    }

    /** Adds the specified amount to the user's penalty total. */
    public void addPenalty(double amount) {
        this.penalty += amount;
    }

    /** Resets the user's penalty to zero. */
    public void payPenalty() {
        this.penalty = 0.0;
    }

    // --- Abstract Display Method (Must be implemented by subclasses) ---
    /** Generates the string representation for displaying user info in the output file. */
    public abstract String getDisplayString();

    // --- Standard Java Object Methods ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id); // Users are equal if IDs match
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Use ID for hashing
    }
}