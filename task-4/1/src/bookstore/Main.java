package bookstore;

import bookstore.enums.OrderStatus;
import bookstore.models.Order;
import bookstore.services.BookstoreService;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== ELECTRONIC BOOKSTORE ===\n");

        BookstoreService service = new BookstoreService();
        service.initializeBooks();

        System.out.println("1. ALL BOOKS IN STORE:");
        service.getAllBooks().forEach(book ->
                System.out.println("- " + book.getTitle() + " (" + book.getStatus() + ")"));

        // Create order
        System.out.println("\n2. CREATING ORDER:");
        Map<String, Integer> orderBooks = new HashMap<>();
        orderBooks.put("B001", 1);
        orderBooks.put("B003", 1); // Out of stock

        Order order = service.createOrder(
                "Ivan Ivanov",
                "ivan@example.com",
                "+79161234567",
                orderBooks
        );

        if (order != null) {
            System.out.println(service.getOrderDetails(order.getId()));

            // Add book to stock
            System.out.println("3. ADDING BOOK TO STOCK:");
            boolean added = service.addBookToStock("B003");
            System.out.println("Result: " + (added ? "SUCCESS" : "FAILED"));

            // Complete order
            System.out.println("\n4. COMPLETING ORDER:");
            boolean completed = service.changeOrderStatus(order.getId(), OrderStatus.COMPLETED);
            System.out.println("Result: " + (completed ? "SUCCESS" : "FAILED"));

            if (completed) {
                System.out.println(service.getOrderDetails(order.getId()));
            }
        } else {
            System.out.println("Failed to create order!");
        }

        System.out.println("\n=== DEMONSTRATION COMPLETED ===");
    }
}