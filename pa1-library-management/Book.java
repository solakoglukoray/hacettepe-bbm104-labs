/**
 * Represents a Book, inheriting from LibraryItem.
 * Adds specific properties like author and genre.
 */
class Book extends LibraryItem {
    // Specific fields for Book
    private final String author;
    private final String genre;

    /**
     * Constructor for creating a Book object.
     * Calls the parent (LibraryItem) constructor first.
     *
     * @param id           Unique ID.
     * @param name         Title of the book.
     * @param author       Author's name.
     * @param genre        Genre of the book.
     * @param physicalType Borrowing type ("normal", "reference", etc.).
     */
    public Book(String id, String name, String author, String genre, String physicalType) {
        // Call the LibraryItem constructor to set common fields
        super(id, name, "Book", physicalType);
        // Set the fields specific to Book
        this.author = author;
        this.genre = genre;
    }

    // Getter methods for book-specific fields
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
}