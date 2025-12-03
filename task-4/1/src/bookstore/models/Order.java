package bookstore.models;

import bookstore.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Order {
    private final String id;
    private final Customer customer;
    private final List<OrderItem> items;
    private final LocalDateTime orderDate;
    private LocalDateTime completionDate;
    private OrderStatus status;

    public Order(String id, Customer customer, List<OrderItem> items) {
        this.id = Objects.requireNonNull(id, "Order ID cannot be null");
        this.customer = Objects.requireNonNull(customer, "Customer cannot be null");
        this.items = new ArrayList<>(Objects.requireNonNull(items, "Items cannot be null"));

        if (this.items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.IN_PROGRESS;
    }

    // Геттеры
    public String getId() { return id; }
    public Customer getCustomer() { return customer; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }
    public LocalDateTime getOrderDate() { return orderDate; }
    public LocalDateTime getCompletionDate() { return completionDate; }
    public OrderStatus getStatus() { return status; }

    public double getTotalAmount() {
        return items.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
    }

    public boolean containsBook(String bookId) {
        return items.stream()
                .anyMatch(item -> item.book().getId().equals(bookId));
    }

    public void setStatus(OrderStatus status) {
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        if (status == OrderStatus.COMPLETED) {
            this.completionDate = LocalDateTime.now();
        } else if (status == OrderStatus.CANCELLED) {
            this.completionDate = null;
        }
    }

    @Override
    public String toString() {
        return String.format("Order #%s: %s books for the amount of $%.2f [%s]",
                id, items.size(), getTotalAmount(), status);
    }

    // Inner class
        public record OrderItem(Book book, int quantity) {
            public OrderItem(Book book, int quantity) {
                this.book = Objects.requireNonNull(book, "Book cannot be null");
                if (quantity <= 0) {
                    throw new IllegalArgumentException("Quantity must be positive");
                }
                this.quantity = quantity;
            }

            public double getTotalPrice() {
                return book.getPrice() * quantity;
            }

            @Override
            public String toString() {
                return String.format("%s x%d = $%.2f",
                        book.getTitle(), quantity, getTotalPrice());
            }
        }
}