package bookstore.controller;

import bookstore.model.Book;
import bookstore.model.BookRequest;
import bookstore.model.Order;
import bookstore.enums.OrderStatus;
import bookstore.service.BookstoreService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ActionController {
    private static ActionController instance;
    private final BookstoreService service;

    private ActionController() {
        this.service = new BookstoreService();
        this.service.initializeBooks();
    }

    public static ActionController getInstance() {
        if (instance == null) {
            instance = new ActionController();
        }
        return instance;
    }

    // Books
    public void showBooksWithOptions() {
        System.out.println("\n=== VIEW BOOKS ===");
        System.out.println("1. View all (sorted by title)");
        System.out.println("2. View with custom sorting");
        System.out.print("Enter your choice: ");

        Scanner scanner = new Scanner(System.in);
        try {
            String choice = scanner.nextLine();

            if (choice.equals("2")) {
                showBooksSorted(scanner);
            } else {
                showBooks();
            }
        } finally {
            waitForEnter(scanner);
        }
    }

    public void showBooks() {
        System.out.println("\n=== BOOKS (SORTED BY TITLE) ===");
        List<Book> books = service.getBooksSorted("title", true);

        if (books.isEmpty()) {
            System.out.println("No books available");
        } else {
            books.forEach(book ->
                    System.out.printf("%s: %s by %s - $%.2f [%s]%n",
                            book.getId(), book.getTitle(), book.getAuthor(),
                            book.getPrice(), book.getStatus()));
        }
    }

    private void showBooksSorted(Scanner scanner) {
        System.out.print("\nSort by (alphabet/date/price/status): ");
        String sortBy = scanner.nextLine();

        System.out.print("Sort ascending? (y/n): ");
        boolean ascending = scanner.nextLine().equalsIgnoreCase("y");

        System.out.println("\n=== BOOKS SORTED BY " + sortBy.toUpperCase() + " ===");
        List<Book> books = service.getBooksSorted(sortBy, ascending);

        if (books.isEmpty()) {
            System.out.println("No books available");
        } else {
            books.forEach(book ->
                    System.out.printf("%s: %s by %s - $%.2f [%s]%n",
                            book.getId(), book.getTitle(), book.getAuthor(),
                            book.getPrice(), book.getStatus()));
        }
    }

    public void viewBookDetails() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("\n=== BOOK DETAILS ===");
            System.out.print("Enter book ID: ");
            String bookId = scanner.nextLine();

            String description = service.getBookDescription(bookId);
            System.out.println("\n" + description);
        } finally {
            waitForEnter(scanner);
        }
    }

    public void addBook() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("\n=== ADD NEW BOOK ===");

            System.out.print("Title: ");
            String title = scanner.nextLine();

            System.out.print("Author: ");
            String author = scanner.nextLine();

            System.out.print("Price: ");
            double price = Double.parseDouble(scanner.nextLine());

            System.out.print("Description: ");
            String description = scanner.nextLine();

            String isbn = "ISBN-" + System.currentTimeMillis();
            LocalDate pubDate = LocalDate.now();

            String bookId = service.addNewBookToStock(title, author, isbn, pubDate, price, description);
            if (bookId != null) {
                System.out.println("Book added: " + bookId);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            waitForEnter(scanner);
        }
    }

    public void restockBook() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("\n=== RESTOCK BOOK ===");
            System.out.print("Book ID: ");
            String bookId = scanner.nextLine();

            boolean success = service.addExistingBookToStock(bookId);
            System.out.println(success ? "Book restocked" : "Book not found or already in stock");
        } finally {
            waitForEnter(scanner);
        }
    }

    public void writeOffBook() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("\n=== WRITE OFF BOOK ===");
            System.out.print("Book ID: ");
            String bookId = scanner.nextLine();

            boolean success = service.writeOffBook(bookId);
            System.out.println(success ? "Book written off" : "Book not found or already out of stock");
        } finally {
            waitForEnter(scanner);
        }
    }

    // Book requests
    public void createBookRequest() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("\n=== CREATE BOOK REQUEST ===");
            System.out.print("Book ID: ");
            String bookId = scanner.nextLine();

            System.out.print("Your email: ");
            String email = scanner.nextLine();

            BookRequest request = service.createBookRequest(bookId, email);
            System.out.println(request != null ? "Request created" : "Book available or not found");
        } finally {
            waitForEnter(scanner);
        }
    }

    public void viewBookRequestsSorted() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("\n=== BOOK REQUESTS ===");
            System.out.print("Sort by (count/alphabet): ");
            String sortBy = scanner.nextLine();

            System.out.print("Sort ascending? (y/n): ");
            boolean ascending = scanner.nextLine().equalsIgnoreCase("y");

            List<Map<String, Object>> requests = service.getBookRequestsSorted(sortBy, ascending);

            if (requests.isEmpty()) {
                System.out.println("No active requests");
            } else {
                requests.forEach(item -> {
                    Book book = (Book) item.get("book");
                    Long count = (Long) item.get("requestCount");
                    System.out.printf("%s: %d request(s)%n", book.getTitle(), count);
                });
            }
        } finally {
            waitForEnter(scanner);
        }
    }

    // Orders
    public void createOrder() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("\n=== CREATE ORDER ===");

            System.out.print("Customer name: ");
            String name = scanner.nextLine();

            System.out.print("Customer email: ");
            String email = scanner.nextLine();

            System.out.print("Customer phone: ");
            String phone = scanner.nextLine();

            Map<String, Integer> bookQuantities = new HashMap<>();

            while (true) {
                System.out.print("Book ID (or 'done'): ");
                String bookId = scanner.nextLine();

                if (bookId.equalsIgnoreCase("done")) {
                    break;
                }

                System.out.print("Quantity: ");
                int quantity = Integer.parseInt(scanner.nextLine());

                if (quantity > 0) {
                    bookQuantities.put(bookId, quantity);
                    System.out.println("Book added to order");
                }
            }

            if (!bookQuantities.isEmpty()) {
                Order order = service.createOrder(name, email, phone, bookQuantities);
                System.out.println("Order created: " + order.getId());
                System.out.printf("Total: $%.2f%n", order.getTotalAmount());
            } else {
                System.out.println("No books in order");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            waitForEnter(scanner);
        }
    }

    public void viewOrdersSorted() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("\n=== VIEW ORDERS ===");
            System.out.print("Sort by (date/price/status): ");
            String sortBy = scanner.nextLine();

            System.out.print("Sort ascending? (y/n): ");
            boolean ascending = scanner.nextLine().equalsIgnoreCase("y");

            List<Order> orders = service.getOrdersSorted(sortBy, ascending);
            System.out.println("\n=== ORDERS SORTED BY " + sortBy.toUpperCase() + " ===");

            if (orders.isEmpty()) {
                System.out.println("No orders");
            } else {
                orders.forEach(order ->
                        System.out.printf("%s: %s - $%.2f [%s]%n",
                                order.getId(), order.getCustomer().name(),
                                order.getTotalAmount(), order.getStatus()));
            }
        } finally {
            waitForEnter(scanner);
        }
    }

    public void cancelOrder() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("\n=== CANCEL ORDER ===");
            System.out.print("Order ID: ");
            String orderId = scanner.nextLine();

            boolean success = service.cancelOrder(orderId);
            System.out.println(success ? "Order cancelled" : "Cannot cancel order");
        } finally {
            waitForEnter(scanner);
        }
    }

    public void changeOrderStatus() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("\n=== CHANGE ORDER STATUS ===");
            System.out.print("Order ID: ");
            String orderId = scanner.nextLine();

            System.out.println("Available statuses: IN_PROGRESS, COMPLETED, CANCELLED");
            System.out.print("New status: ");

            try {
                OrderStatus status = OrderStatus.valueOf(scanner.nextLine().toUpperCase());
                boolean success = service.changeOrderStatus(orderId, status);
                System.out.println(success ? "Status changed" : "Failed to change status");
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid status");
            }
        } finally {
            waitForEnter(scanner);
        }
    }

    public void viewOrderDetails() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("\n=== ORDER DETAILS ===");
            System.out.print("Order ID: ");
            String orderId = scanner.nextLine();

            String details = service.getOrderDetails(orderId);
            System.out.println("\n" + details);
        } finally {
            waitForEnter(scanner);
        }
    }

    // Reports
    public void showUnsoldBooks() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("\n=== UNSOLD BOOKS (>6 MONTHS) ===");
            System.out.print("Sort by (arrival/price): ");
            String sortBy = scanner.nextLine();

            System.out.print("Sort ascending? (y/n): ");
            boolean ascending = scanner.nextLine().equalsIgnoreCase("y");

            List<Book> books = service.getUnsoldBooks(sortBy, ascending);

            if (books.isEmpty()) {
                System.out.println("All books sold recently");
            } else {
                books.forEach(book ->
                        System.out.printf("%s: %s - $%.2f (arrived: %s)%n",
                                book.getId(), book.getTitle(), book.getPrice(), book.getArrivalDate()));
            }
        } finally {
            waitForEnter(scanner);
        }
    }

    public void showCompletedOrdersPeriod() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("\n=== COMPLETED ORDERS FOR PERIOD ===");

            System.out.print("Start date (YYYY-MM-DD): ");
            LocalDate start = LocalDate.parse(scanner.nextLine());

            System.out.print("End date (YYYY-MM-DD): ");
            LocalDate end = LocalDate.parse(scanner.nextLine());

            System.out.print("Sort by (date/price): ");
            String sortBy = scanner.nextLine();

            System.out.print("Sort ascending? (y/n): ");
            boolean ascending = scanner.nextLine().equalsIgnoreCase("y");

            List<Order> orders = service.getCompletedOrdersForPeriod(
                    start.atStartOfDay(), end.atStartOfDay(), sortBy, ascending);

            if (orders.isEmpty()) {
                System.out.println("No completed orders in this period");
            } else {
                orders.forEach(order ->
                        System.out.printf("%s: %s - $%.2f on %s%n",
                                order.getId(), order.getCustomer().name(),
                                order.getTotalAmount(), order.getCompletionDate()));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            waitForEnter(scanner);
        }
    }

    public void showRevenueReport() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("\n=== REVENUE REPORT ===");

            System.out.print("Start date (YYYY-MM-DD): ");
            LocalDate start = LocalDate.parse(scanner.nextLine());

            System.out.print("End date (YYYY-MM-DD): ");
            LocalDate end = LocalDate.parse(scanner.nextLine());

            double revenue = service.getRevenueForPeriod(start.atStartOfDay(), end.atStartOfDay());
            int count = service.getCompletedOrdersCountForPeriod(start.atStartOfDay(), end.atStartOfDay());

            System.out.println("\n=== REPORT: " + start + " to " + end + " ===");
            System.out.printf("Completed orders: %d%n", count);
            System.out.printf("Total revenue: $%.2f%n", revenue);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            waitForEnter(scanner);
        }
    }

    // Helper method
    private void waitForEnter(Scanner scanner) {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}