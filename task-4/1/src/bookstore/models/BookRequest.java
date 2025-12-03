package bookstore.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class BookRequest {
    private final String id;
    private final String bookId;
    private final String customerEmail;
    private final LocalDateTime requestDate;

    public BookRequest(String id, String bookId, String customerEmail) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.bookId = Objects.requireNonNull(bookId, "Book ID cannot be null");
        this.customerEmail = validateEmail(customerEmail);
        this.requestDate = LocalDateTime.now();
    }

    private String validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        return email;
    }

    public String getId() { return id; }
    public String getBookId() { return bookId; }
    public String getCustomerEmail() { return customerEmail; }
    public LocalDateTime getRequestDate() { return requestDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookRequest that = (BookRequest) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Request #%s for %s book from %s", id, bookId, customerEmail);
    }
}