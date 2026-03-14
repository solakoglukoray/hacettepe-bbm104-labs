import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
// No need for List, ArrayList imports if only using methods inherited from User

/**
 * Represents a Guest user, inheriting from User.
 */
class Guest extends User {
    // --- Field specific to Guest ---
    private final String occupation;

    // --- Constants for Guest rules ---
    private static final int MAX_ITEMS = 1;        // Max items a guest can borrow
    private static final int OVERDUE_DAYS = 7;     // Days before an item is overdue for a guest
    // Set containing the physical types guests CANNOT borrow
    private static final Set<String> RESTRICTED_PHYSICAL_TYPES = new HashSet<>(Arrays.asList("rare", "limited"));

    /**
     * Constructor for Guest.
     */
    public Guest(String id, String name, String phoneNumber, String occupation) {
        super(id, name, phoneNumber); // Call User constructor
        this.occupation = occupation;
    }

    // --- Implementation of abstract methods from User ---
    @Override public int getMaxItems() { return MAX_ITEMS; }
    @Override public int getOverdueDays() { return OVERDUE_DAYS; }

    /**
     * Checks if a guest is restricted from borrowing this item.
     * Guests cannot borrow "rare" or "limited" items.
     */
     @Override
     public boolean isRestricted(LibraryItem item) {
        // Check if the item's type (already lowercase) is in the restricted set
        return RESTRICTED_PHYSICAL_TYPES.contains(item.getPhysicalType());
     }

    // --- Getter for guest-specific field ---
    public String getOccupation() { return occupation; }

    /**
     * Generates the display string for a Guest according to the required format.
     */
     @Override
    public String getDisplayString() {
        // Use String.format for the output
         return String.format("------ User Information for %s ------%n" + // Header
                              "Name: %s Phone: %s%n" +                  // Name and phone
                              "Occupation: %s%n",                       // Occupation
                              getId(),
                              getName(),
                              getPhoneNumber(),
                              occupation);
    }
}