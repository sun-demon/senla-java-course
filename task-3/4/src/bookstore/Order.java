package bookstore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {
    private final String orderId;
    private OrderStatus status;
    private final List<OrderItem> items;
    private double totalPrice;

    public Order() {
        this.orderId = UUID.randomUUID().toString().substring(0, 8);
        this.status = OrderStatus.NEW;
        this.items = new ArrayList<>();
        this.totalPrice = 0.0;
    }

    // Геттеры
    public String getOrderId() { return orderId; }
    public OrderStatus getStatus() { return status; }
    public List<OrderItem> getItems() { return new ArrayList<>(items); }
    public double getTotalPrice() { return totalPrice; }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        calculateTotal();
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        calculateTotal();
    }

    private void calculateTotal() {
        totalPrice = items.stream()
                .mapToDouble(OrderItem::getItemTotal)
                .sum();
    }

    public boolean containsBook(Book book) {
        return items.stream()
                .anyMatch(item -> item.getBook().equals(book));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Заказ #%s | Статус: %s | Итого: $%.2f\n",
                orderId, status.getDescription(), totalPrice));
        sb.append("Состав заказа:\n");
        for (OrderItem item : items) {
            sb.append("  - ").append(item.toString()).append("\n");
        }
        return sb.toString();
    }
}