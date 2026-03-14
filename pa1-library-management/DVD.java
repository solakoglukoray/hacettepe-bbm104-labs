/**
 * Represents a DVD, inheriting from LibraryItem.
 * Adds specific properties like director, category, and runtime.
 */
class DVD extends LibraryItem {
    // Specific fields for DVD
    private final String director;
    private final String category;
    private final int runtime; // Duration in minutes

    /**
     * Constructor for creating a DVD object.
     * Calls the parent (LibraryItem) constructor first.
     *
     * @param id           Unique ID.
     * @param name         Title of the DVD.
     * @param director     Director's name.
     * @param category     Category of the DVD.
     * @param runtime      Runtime in minutes.
     * @param physicalType Borrowing type ("normal", "rare", "limited", etc.).
     */
    public DVD(String id, String name, String director, String category, int runtime, String physicalType) {
        // Call the LibraryItem constructor
        super(id, name, "DVD", physicalType);
        // Set the fields specific to DVD
        this.director = director;
        this.category = category;
        this.runtime = runtime;
    }

    // Getter methods for DVD-specific fields
    public String getDirector() { return director; }
    public String getCategory() { return category; }
    public int getRuntime() { return runtime; }
}