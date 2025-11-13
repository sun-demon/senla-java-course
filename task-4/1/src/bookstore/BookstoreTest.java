package bookstore;

import bookstore.enums.OrderStatus;
import bookstore.services.BookstoreService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class BookstoreTest {
    public static void main(String[] args) {
        System.out.println("=== BOOKSTORE SYSTEM TESTS ===\n");

        testBookOperations();
        testOrderOperations();
        testBookRequests();
        testStatistics();
        testEdgeCases();

        System.out.println("\n=== ALL TESTS COMPLETED ===");
    }

    private static void testBookOperations() {
        System.out.println("TEST 1: BOOK OPERATIONS");
        System.out.println("-".repeat(50));

        BookstoreService service = new BookstoreService();
        service.initializeBooks();

        // 1.1 Check initialization
        System.out.println("1.1 Checking book initialization:");
        System.out.println("Total books: " + service.getAllBooks().size());

        // 1.2 Write off a book
        System.out.println("\n1.2 Writing off book B001:");
        boolean writtenOff = service.writeOffBook("B001");
        System.out.println("Result: " + (writtenOff ? "SUCCESS" : "FAILED"));

        var book = service.getBook("B001");
        if (book != null) {
            System.out.println("Book status: " + book.getStatus());
        }

        // 1.3 Add book back to stock
        System.out.println("\n1.3 Adding book B001 back to stock:");
        boolean added = service.addBookToStock("B001");
        System.out.println("Result: " + (added ? "SUCCESS" : "FAILED"));

        // 1.4 Book sorting
        System.out.println("\n1.4 Books sorted by price:");
        service.getBooksSorted("price").forEach(b ->
                System.out.printf("  %s: %.2f ₽%n", b.getTitle(), b.getPrice()));
    }

    private static void testOrderOperations() {
        System.out.println("\n\nTEST 2: ORDER OPERATIONS");
        System.out.println("-".repeat(50));

        BookstoreService service = new BookstoreService();
        service.initializeBooks();

        // 2.1 Create order
        System.out.println("2.1 Creating order with available books:");
        Map<String, Integer> books = new HashMap<>();
        books.put("B001", 1);
        books.put("B002", 2);

        var order = service.createOrder(
                "Test Customer",
                "test@example.com",
                "+79160000000",
                books
        );

        System.out.println("Order created: " + (order != null ? "YES (#" + order.getId() + ")" : "NO"));

        // 2.2 View order details
        if (order != null) {
            System.out.println("\n2.2 Order details:");
            System.out.println(service.getOrderDetails(order.getId()));
        }

        // 2.3 Change status
        System.out.println("\n2.3 Changing order status to COMPLETED:");
        if (order != null) {
            boolean changed = service.changeOrderStatus(order.getId(), OrderStatus.COMPLETED);
            System.out.println("Result: " + (changed ? "SUCCESS" : "FAILED"));
        }

        // 2.4 Cancel order
        System.out.println("\n2.4 Creating and canceling order:");
        Map<String, Integer> books2 = new HashMap<>();
        books2.put("B004", 1);

        var order2 = service.createOrder(
                "Another Customer",
                "another@example.com",
                "+79161111111",
                books2
        );

        if (order2 != null) {
            boolean cancelled = service.cancelOrder(order2.getId());
            System.out.println("Order cancelled: " + (cancelled ? "YES" : "NO"));
        }
    }

    private static void testBookRequests() {
        System.out.println("\n\nTEST 3: BOOK REQUESTS");
        System.out.println("-".repeat(50));

        BookstoreService service = new BookstoreService();
        service.initializeBooks();

        // 3.1 Create request
        System.out.println("3.1 Creating request for out-of-stock book:");
        var request = service.createBookRequest("B003", "customer@example.com");
        System.out.println("Request created: " + (request != null ? "YES" : "NO"));

        // 3.2 Create order with out-of-stock book (should auto-create request)
        System.out.println("\n3.2 Creating order with out-of-stock book (auto-request):");
        Map<String, Integer> books = new HashMap<>();
        books.put("B005", 1); // Out of stock

        var order = service.createOrder(
                "Request Customer",
                "request@example.com",
                "+79162222222",
                books
        );

        System.out.println("Order created: " + (order != null ? "YES" : "NO"));
        System.out.println("Active requests: " + service.getAllBookRequests().size());

        // 3.3 Add book to stock and check requests
        System.out.println("\n3.3 Adding book B005 to stock:");
        service.addBookToStock("B005");

        // 3.4 Check that requests are cleared
        System.out.println("Active requests after adding to stock: " +
                service.getAllBookRequests().size());
    }

    private static void testStatistics() {
        System.out.println("\n\nTEST 4: STATISTICS AND REPORTS");
        System.out.println("-".repeat(50));

        BookstoreService service = new BookstoreService();
        service.initializeBooks();

        // 4.1 Create some orders for statistics
        System.out.println("4.1 Creating test orders:");

        // Order 1
        Map<String, Integer> order1Books = new HashMap<>();
        order1Books.put("B001", 1);
        order1Books.put("B002", 1);
        var order1 = service.createOrder("Customer1", "c1@test.ru", "111", order1Books);
        if (order1 != null) {
            service.changeOrderStatus(order1.getId(), OrderStatus.COMPLETED);
        }

        // Order 2
        Map<String, Integer> order2Books = new HashMap<>();
        order2Books.put("B004", 2);
        var order2 = service.createOrder("Customer2", "c2@test.ru", "222", order2Books);
        if (order2 != null) {
            service.changeOrderStatus(order2.getId(), OrderStatus.COMPLETED);
        }

        System.out.println("Total orders created: " + service.getAllOrders().size());

        // 4.2 Period statistics
        System.out.println("\n4.2 Statistics for last 30 days:");
        LocalDateTime start = LocalDateTime.now().minusDays(30);
        LocalDateTime end = LocalDateTime.now();

        double revenue = service.getRevenueForPeriod(start, end);
        int ordersCount = service.getCompletedOrdersCountForPeriod(start, end);

        System.out.println("Revenue: " + String.format("$%.2f", revenue));
        System.out.println("Completed orders: " + ordersCount);

        // 4.3 Unsold books
        System.out.println("\n4.3 Unsold books (not sold for >6 months):");
        var unsoldBooks = service.getUnsoldBooks("arrivaldate");
        System.out.println("Count: " + unsoldBooks.size());

        // 4.4 Order sorting
        System.out.println("\n4.4 Orders sorted by total amount:");
        service.getOrdersSorted("price").forEach(o ->
                System.out.printf("  Order #%s: $%.2f%n", o.getId(), o.getTotalAmount())
        );
    }

    private static void testEdgeCases() {
        System.out.println("\n\nTEST 5: EDGE CASES");
        System.out.println("-".repeat(50));

        BookstoreService service = new BookstoreService();
        service.initializeBooks();

        // 5.1 Empty order
        System.out.println("5.1 Trying to create empty order:");
        try {
            Map<String, Integer> emptyBooks = new HashMap<>();
            var order = service.createOrder("Customer", "test@test.ru", "123", emptyBooks);
            System.out.println("Result: " + (order == null ? "EMPTY ORDER NOT CREATED (EXPECTED)" : "ERROR"));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        // 5.2 Order with non-existent book
        System.out.println("\n5.2 Order with non-existent book:");
        Map<String, Integer> invalidBooks = new HashMap<>();
        invalidBooks.put("NONEXISTENT", 1);
        invalidBooks.put("B001", 1); // Existing book

        var order = service.createOrder("Customer", "test@test.ru", "123", invalidBooks);
        System.out.println("Order created: " + (order != null ? "YES" : "NO"));

        // 5.3 Change status of non-existent order
        System.out.println("\n5.3 Changing status of non-existent order:");
        boolean changed = service.changeOrderStatus("NONEXISTENT", OrderStatus.COMPLETED);
        System.out.println("Result: " + (changed ? "SUCCESS (UNEXPECTED)" : "FAILED (EXPECTED)"));

        // 5.4 Write off non-existent book
        System.out.println("\n5.4 Writing off non-existent book:");
        boolean writtenOff = service.writeOffBook("NONEXISTENT");
        System.out.println("Result: " + (writtenOff ? "SUCCESS (UNEXPECTED)" : "FAILED (EXPECTED)"));

        // 5.5 Add non-existent book to stock
        System.out.println("\n5.5 Adding non-existent book to stock:");
        boolean added = service.addBookToStock("NONEXISTENT");
        System.out.println("Result: " + (added ? "SUCCESS (UNEXPECTED)" : "FAILED (EXPECTED)"));

        // 5.6 Create request for available book
        System.out.println("\n5.6 Creating request for available book:");
        var request = service.createBookRequest("B001", "test@example.com");
        System.out.println("Result: " + (request != null ? "REQUEST CREATED (UNEXPECTED)" : "NO REQUEST (EXPECTED)"));
    }
}
