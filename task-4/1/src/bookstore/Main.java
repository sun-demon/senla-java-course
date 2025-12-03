package bookstore;

import bookstore.models.Order;
import bookstore.services.BookstoreService;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== ELECTRONIC BOOKSTORE ===\n");

        BookstoreService service = new BookstoreService();
        service.initializeBooks();

        // 1. Show the initial assortment
        System.out.println("1. INITIAL BOOK COLLECTION:");
        service.getAllBooks().forEach(book ->
                System.out.println("  " + book.getId() + ": " + book.getTitle() + " - " + book.getStatus()));

        // 2. Add a new book (auto-generation ID)
        System.out.println("\n2. ADD NEW BOOK TO STOCK (auto-generated ID):");
        String newBookId = service.addNewBookToStock(
                "The Brothers Karamazov",
                "Fyodor Dostoevsky",
                "978-0-14-044924-2",
                LocalDate.of(1880, 1, 1),
                32.99,
                "A philosophical novel about faith, doubt, and reason."
        );

        if (newBookId != null) {
            System.out.println("  Success! New book ID: " + newBookId);
        }

        // 3. Add another new book (auto-generation ID)
        System.out.println("\n3. ADD ANOTHER NEW BOOK TO STOCK (auto-generated ID):");
        String newAnotherIdBook = service.addNewBookToStock(
                "Dead Souls",
                "Nikolai Gogol",
                "978-0-14-044807-8",
                LocalDate.of(1842, 1, 1),
                22.99,                         // Price
                "A satirical novel about Russian society."
        );

        System.out.println("  Result: " + (newAnotherIdBook != null ? "SUCCESS" : "FAILED"));

        // 4. Add an existing book to the warehouse (which was OUT_OF_STOCK)
        System.out.println("\n4. ADD EXISTING BOOK TO STOCK (B003):");
        boolean existingBook = service.addExistingBookToStock("B003");
        System.out.println("  Result: " + (existingBook ? "SUCCESS" : "FAILED"));

        // 5. Try to add a non-existent book
        System.out.println("\n5. ADD NON-EXISTENT BOOK TO STOCK:");
        boolean nonExistent = service.addExistingBookToStock("NONEXISTENT");
        System.out.println("  Result: " + (nonExistent ? "SUCCESS (UNEXPECTED)" : "FAILED (EXPECTED)"));

        // 6. Show the updated assortment
        System.out.println("\n6. UPDATED BOOK COLLECTION:");
        service.getAllBooks().forEach(book ->
                System.out.println("  " + book.getId() + ": " + book.getTitle() + " - " + book.getStatus()));

        // 7. Create an order with a new bookСоздаем заказ с новой книгой
        System.out.println("\n7. CREATING ORDER WITH NEW BOOK:");
        Map<String, Integer> orderBooks = new HashMap<>();
        orderBooks.put("B001", 1);
        orderBooks.put(newBookId, 2);

        Order order = service.createOrder(
                "John Smith",
                "john@example.com",
                "+1234567890",
                orderBooks
        );

        if (order != null) {
            System.out.println("  Order created: " + order);
        }

        // 8. Statistics
        System.out.println("\n8. STORE STATISTICS:");
        System.out.println("  Total books: " + service.getAllBooks().size());
        System.out.println("  Total orders: " + service.getAllOrders().size());

        System.out.println("\n=== DEMONSTRATION COMPLETED ===");
    }
}