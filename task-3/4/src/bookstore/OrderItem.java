package bookstore;

public class OrderItem {
    private final Book book;
    private final int quantity;

    public OrderItem(Book book, int quantity) {
        this.book = book;
        if (quantity <= 0) {
            throw new IllegalArgumentException("Количество должно быть положительным");
        }
        this.quantity = quantity;
    }

    public Book getBook() { return book; }
    public int getQuantity() { return quantity; }
    public double getItemTotal() { return book.getPrice() * quantity; }

    @Override
    public String toString() {
        return String.format("%s × %d = $%.2f", book.getTitle(), quantity, getItemTotal());
    }
}