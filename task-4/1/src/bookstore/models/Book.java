package bookstore.models;

import bookstore.enums.BookStatus;
import java.time.LocalDate;
import java.util.Objects;

public class Book {
    private final String id;
    private final String title;
    private final String author;
    private final String isbn;
    private final LocalDate publicationDate;
    private double price;
    private BookStatus status;
    private LocalDate arrivalDate;
    private final String description;

    public Book(String id, String title, String author, String isbn,
                LocalDate publicationDate, double price, BookStatus status,
                LocalDate arrivalDate, String description) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.author = Objects.requireNonNull(author, "Author cannot be null");
        this.isbn = Objects.requireNonNull(isbn, "ISBN cannot be null");
        this.publicationDate = Objects.requireNonNull(publicationDate, "Publication date cannot be null");
        this.price = validatePrice(price);
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.arrivalDate = Objects.requireNonNull(arrivalDate, "Arrival date cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
    }

    private double validatePrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        return price;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public LocalDate getPublicationDate() { return publicationDate; }
    public double getPrice() { return price; }
    public BookStatus getStatus() { return status; }
    public LocalDate getArrivalDate() { return arrivalDate; }
    public String getDescription() { return description; }

    public void setPrice(double price) {
        this.price = validatePrice(price);
    }

    public void setStatus(BookStatus status) {
        this.status = Objects.requireNonNull(status, "Status cannot be null");
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = Objects.requireNonNull(arrivalDate, "Arrival date cannot be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id.equals(book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Book{id='%s', title='%s', author='%s', price=%.2f, status=%s}",
                id, title, author, price, status);
    }
}