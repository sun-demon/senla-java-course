package bookstore;

import java.util.Objects;

public class Book {
    private final String isbn;
    private final String title;
    private final String author;
    private double price;
    private BookStatus status;
    private int quantity;

    public Book(String isbn, String title, String author, double price, int quantity) {
        this.isbn = Objects.requireNonNull(isbn, "ISBN не может быть null");
        this.title = Objects.requireNonNull(title, "Название не может быть null");
        this.author = Objects.requireNonNull(author, "Автор не может быть null");
        setPrice(price);
        setQuantity(quantity);
    }

    // Геттеры
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public double getPrice() { return price; }
    public BookStatus getStatus() { return status; }
    public int getQuantity() { return quantity; }

    // Сеттеры с валидацией
    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Цена не может быть отрицательной");
        }
        this.price = price;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Количество не может быть отрицательным");
        }
        this.quantity = quantity;
        this.status = quantity > 0 ? BookStatus.IN_STOCK : BookStatus.OUT_OF_STOCK;
    }

    public void increaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Количество для добавления должно быть положительным");
        }
        setQuantity(this.quantity + amount);
    }

    public void decreaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Количество для списания должно быть положительным");
        }
        if (amount > this.quantity) {
            throw new IllegalArgumentException("Недостаточно книг на складе");
        }
        setQuantity(this.quantity - amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book book)) return false;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return String.format("'%s' - %s (ISBN: %s) | %s | %d шт. | $%.2f",
                title, author, isbn, status.getDescription(), quantity, price);
    }
}