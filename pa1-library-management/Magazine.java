/**
 * Represents a Magazine, inheriting from LibraryItem.
 * Adds specific properties like publisher and category.
 */
class Magazine extends LibraryItem {
    // Specific fields for Magazine
    private final String publisher;
    private final String category;

    /**
     * Constructor for creating a Magazine object.
     * Calls the parent (LibraryItem) constructor first.
     *
     * @param id           Unique ID.
     * @param name         Title of the magazine.
     * @param publisher    Publisher's name.
     * @param category     Category of the magazine.
     * @param physicalType Borrowing type ("normal", "reference", etc.).
     */
    public Magazine(String id, String name, String publisher, String category, String physicalType) {
        // Call the LibraryItem constructor
        super(id, name, "Magazine", physicalType);
        // Set the fields specific to Magazine
        this.publisher = publisher;
        this.category = category;
    }

    // Getter methods for magazine-specific fields
    public String getPublisher() { return publisher; }
    public String getCategory() { return category; }
}