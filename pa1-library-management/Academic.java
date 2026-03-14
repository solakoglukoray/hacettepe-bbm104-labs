// No specific imports needed if not using extra collections here

/**
 * Represents an Academic Member user, inheriting from User.
 */
class Academic extends User {
    // --- Fields specific to Academic ---
    private final String department;
    private final String faculty;
    private final String title; // e.g., "Professor", "Assistant Professor"

    // --- Constants for Academic rules ---
    private static final int MAX_ITEMS = 3;     // Max items an academic can borrow
    private static final int OVERDUE_DAYS = 15; // Days before an item is overdue for an academic

    /**
     * Constructor for Academic.
     */
    public Academic(String id, String name, String phoneNumber, String department, String faculty, String title) {
        super(id, name, phoneNumber); // Call User constructor
        this.department = department;
        this.faculty = faculty;
        this.title = title;
    }

    // --- Implementation of abstract methods from User ---
    @Override public int getMaxItems() { return MAX_ITEMS; }
    @Override public int getOverdueDays() { return OVERDUE_DAYS; }

    /**
     * Checks if an academic is restricted from borrowing this item.
     * Academics have no restrictions.
     */
    @Override public boolean isRestricted(LibraryItem item) {
        return false; // Always returns false (no restrictions)
    }

    // --- Getters for academic-specific fields ---
    public String getDepartment() { return department; }
    public String getFaculty() { return faculty; }
    public String getTitle() { return title; }

    /**
     * Generates the display string for an Academic according to the required format.
     * Prepends the title to the name.
     */
    @Override
    public String getDisplayString() {
        // Start with the name provided during creation
        String formattedName = getName();
        // If a title exists, prepend it (e.g., "Professor John Doe")
        if (title != null && !title.isEmpty()) {
            formattedName = title + " " + formattedName;
        }

        // Use String.format for the output
        return String.format("------ User Information for %s ------%n" + // Header
                             "Name: %s Phone: %s%n" +                  // Formatted Name and phone
                             "Faculty: %s Department: %s%n",          // Faculty and Dept
                             getId(),
                             formattedName, // Use the potentially modified name
                             getPhoneNumber(),
                             faculty,
                             department);
    }
}