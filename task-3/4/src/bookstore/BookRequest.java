package bookstore;

import java.util.UUID;

public class BookRequest {
    private final String requestId;
    private final Book book;
    private final int quantity;
    private boolean fulfilled;

    public BookRequest(Book book, int quantity) {
        this.requestId = UUID.randomUUID().toString().substring(0, 8);
        this.book = book;
        this.quantity = quantity;
        this.fulfilled = false;
    }

    // Геттеры
    public String getRequestId() { return requestId; }
    public Book getBook() { return book; }
    public int getQuantity() { return quantity; }
    public boolean isFulfilled() { return fulfilled; }

    public void markFulfilled() {
        this.fulfilled = true;
    }

    @Override
    public String toString() {
        return String.format("Запрос #%s: %s × %d шт. | %s",
                requestId, book.getTitle(), quantity,
                fulfilled ? "Выполнен" : "Ожидание");
    }
}