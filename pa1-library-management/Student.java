import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
// No need for List, ArrayList imports if only using methods inherited from User

/**
 * Represents a Student user, inheriting from User.
 */
class Student extends User {
    // --- Fields specific to Student ---
    private final String department;
    private final String faculty;
    private final int grade;

    // --- Constants for Student rules ---
    private static final int MAX_ITEMS = 5;        // Max items a student can borrow
    private static final int OVERDUE_DAYS = 30;    // Days before an item is overdue for a student
    // Set containing the physical types students CANNOT borrow
    private static final Set<String> RESTRICTED_PHYSICAL_TYPES = new HashSet<>(Collections.singletonList("reference"));

    /**
     * Constructor for Student.
     */
    public Student(String id, String name, String phoneNumber, String department, String faculty, int grade) {
        super(id, name, phoneNumber); // Call User constructor
        this.department = department;
        this.faculty = faculty;
        this.grade = grade;
    }

    // --- Implementation of abstract methods from User ---
    @Override
    public int getMaxItems() { return MAX_ITEMS; }
    @Override
    public int getOverdueDays() { return OVERDUE_DAYS; }

    /**
     * Checks if a student is restricted from borrowing this item.
     * Students cannot borrow "reference" items.
     */
    @Override
    public boolean isRestricted(LibraryItem item) {
        // Check if the item's type (already lowercase) is in the restricted set
        return RESTRICTED_PHYSICAL_TYPES.contains(item.getPhysicalType());
    }

    // --- Getters for student-specific fields ---
    public String getDepartment() { return department; }
    public String getFaculty() { return faculty; }
    public int getGrade() { return grade; }

    /**
     * Helper method to get the grade suffix (e.g., "1st", "2nd", "3th").
     * Matches the specific format required in the output.
     */
    private String getGradeSuffix(int grade) {
        // Note: This matches the example output's specific (maybe grammatically odd) format.
        return grade + "th";
    }

    /**
     * Generates the display string for a Student according to the required format.
     */
    @Override
    public String getDisplayString() {
        // Uses String.format to build the multi-line string with specific placeholders
        // %s for strings, %d for integers, %n for newline
        return String.format("------ User Information for %s ------%n" + // Header line
                             "Name: %s Phone: %s%n" +                  // Name and phone
                             "Faculty: %s Department: %s Grade: %s%n", // Faculty, Dept, Grade (with suffix)
                             getId(),                           // Placeholder values
                             getName(),
                             getPhoneNumber(),
                             faculty,
                             department,
                             getGradeSuffix(grade));
    }
}