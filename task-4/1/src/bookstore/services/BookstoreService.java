package bookstore.services;

import bookstore.enums.BookStatus;
import bookstore.enums.OrderStatus;
import bookstore.models.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class BookstoreService {
    private final Map<String, Book> books = new HashMap<>();
    private final Map<String, Order> orders = new HashMap<>();
    private final Map<String, BookRequest> bookRequests = new HashMap<>();
    private final Map<String, Customer> customers = new HashMap<>();

    public void initializeBooks() {
        Book[] initialBooks = {
                new Book("B001", "Master and Margarita", "Mikhail Bulgakov", "978-5-17-067842-4",
                        LocalDate.of(1967, 1, 1), 29.99, BookStatus.AVAILABLE,
                        LocalDate.of(2024, 1, 15), "A novel about the devil visiting Moscow in the 1920s."),
                new Book("B002", "Crime and Punishment", "Fyodor Dostoevsky", "978-5-04-111917-7",
                        LocalDate.of(1866, 1, 1), 24.99, BookStatus.AVAILABLE,
                        LocalDate.of(2024, 2, 20), "A novel about the moral struggles of student Raskolnikov."),
                new Book("B003", "War and Peace", "Leo Tolstoy", "978-5-699-89038-9",
                        LocalDate.of(1869, 1, 1), 34.99, BookStatus.OUT_OF_STOCK,
                        LocalDate.of(2024, 3, 10), "An epic about the war of 1812 and Russian society."),
                new Book("B004", "Anna Karenina", "Leo Tolstoy", "978-5-389-06591-3",
                        LocalDate.of(1877, 1, 1), 27.50, BookStatus.AVAILABLE,
                        LocalDate.of(2024, 1, 5), "A novel about the tragic love of a married woman."),
                new Book("B005", "Dead Souls", "Nikolai Gogol", "978-5-17-080115-0",
                        LocalDate.of(1842, 1, 1), 22.99, BookStatus.OUT_OF_STOCK,
                        LocalDate.of(2023, 12, 1), "A poem about Russian landlord society.")
        };

        for (Book book : initialBooks) {
            books.put(book.getId(), book);
        }
    }

    // Main functions from the TOR

    public boolean writeOffBook(String bookId) {
        Book book = books.get(bookId);
        if (book != null && book.getStatus() == BookStatus.AVAILABLE) {
            book.setStatus(BookStatus.OUT_OF_STOCK);
            System.out.println("Book '" + book.getTitle() + "' written off from stock");
            return true;
        }
        System.out.println("Failed to write off book: " + bookId);
        return false;
    }

    public Order createOrder(String customerName, String customerEmail,
                             String customerPhone, Map<String, Integer> bookQuantities) {
        Customer customer = customers.computeIfAbsent(customerEmail,
                email -> new Customer(customerName, customerEmail, customerPhone));

        List<Order.OrderItem> items = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : bookQuantities.entrySet()) {
            Book book = books.get(entry.getKey());
            if (book != null) {
                items.add(new Order.OrderItem(book, entry.getValue()));

                if (book.getStatus() == BookStatus.OUT_OF_STOCK) {
                    createBookRequest(book.getId(), customerEmail);
                }
            }
        }

        if (items.isEmpty()) {
            return null;
        }

        String orderId = "ORD" + (orders.size() + 1);
        Order order = new Order(orderId, customer, items);
        orders.put(orderId, order);

        return order;
    }

    public boolean cancelOrder(String orderId) {
        Order order = orders.get(orderId);
        if (order != null && order.getStatus() == OrderStatus.IN_PROGRESS) {
            order.setStatus(OrderStatus.CANCELLED);
            return true;
        }
        return false;
    }

    public boolean changeOrderStatus(String orderId, OrderStatus newStatus) {
        Order order = orders.get(orderId);
        if (order == null) {
            return false;
        }

        if (newStatus == OrderStatus.COMPLETED) {
            for (Order.OrderItem item : order.getItems()) {
                if (item.book().getStatus() != BookStatus.AVAILABLE) {
                    return false;
                }
            }
        }

        order.setStatus(newStatus);
        return true;
    }

    // A method for adding an existing book to a warehouse
    public boolean addExistingBookToStock(String bookId) {
        Book book = books.get(bookId);
        if (book == null) {
            System.out.println("Book with ID '" + bookId + "' not found");
            return false;
        }

        if (book.getStatus() == BookStatus.AVAILABLE) {
            System.out.println("Book '" + book.getTitle() + "' is already in stock");
            return false;
        }

        book.setStatus(BookStatus.AVAILABLE);
        book.setArrivalDate(LocalDate.now());

        // Close all requests for this book
        int closedRequests = 0;
        for (BookRequest request : bookRequests.values()) {
            if (request.getBookId().equals(bookId)) {
                closedRequests++;
            }
        }
        bookRequests.values().removeIf(request -> request.getBookId().equals(bookId));

        System.out.println("Book '" + book.getTitle() + "' added to stock. Closed " + closedRequests + " requests.");
        return true;
    }

    public String addNewBookToStock(String title, String author, String isbn, LocalDate publicationDate, double price,
                                     String description) {

        // Generate new ID (B001, B002, ...)
        String bookId = generateBookId();

        try {
            Book newBook = new Book(
                    bookId,
                    Objects.requireNonNull(title, "Title cannot be null"),
                    Objects.requireNonNull(author, "Author cannot be null"),
                    Objects.requireNonNull(isbn, "ISBN cannot be null"),
                    Objects.requireNonNull(publicationDate, "Publication date cannot be null"),
                    validatePrice(price),
                    BookStatus.AVAILABLE,
                    LocalDate.now(),
                    Objects.requireNonNull(description, "Description cannot be null")
            );

            books.put(bookId, newBook);

            System.out.println("New book '" + title + "' added to stock with ID: " + bookId);
            return bookId;

        } catch (IllegalArgumentException e) {
            System.out.println("Failed to add new book: " + e.getMessage());
            return null;
        }
    }

    private double validatePrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        return price;
    }

    private String generateBookId() {
        int maxNumber = 0;

        // We find the maximum number among the existing IDs in the format B001, B002, ...
        for (String id : books.keySet()) {
            if (id.matches("B\\d+")) {
                try {
                    int number = Integer.parseInt(id.substring(1));
                    if (number > maxNumber) {
                        maxNumber = number;
                    }
                } catch (NumberFormatException e) {
                    // Ignoring IDs that do not match the format
                }
            }
        }

        // Generate next ID
        return String.format("B%03d", maxNumber + 1);
    }

    public BookRequest createBookRequest(String bookId, String customerEmail) {
        Book book = books.get(bookId);
        if (book != null && book.getStatus() == BookStatus.OUT_OF_STOCK) {
            String requestId = "REQ" + (bookRequests.size() + 1);
            BookRequest request = new BookRequest(requestId, bookId, customerEmail);
            bookRequests.put(requestId, request);

            System.out.println("Created request #" + requestId + " for book '" +
                    book.getTitle() + "' from " + customerEmail);
            return request;
        }
        System.out.println("Cannot create request for book: " + bookId);
        return null;
    }

    // Additional functions for view

    public List<Book> getBooksSorted(String sortBy) {
        List<Book> bookList = new ArrayList<>(books.values());

        // Normalize the parameter: remove spaces, reduce to lowercase
        String normalizedSortBy = sortBy.toLowerCase().replaceAll("\\s+", "");

        switch (normalizedSortBy) {
            case "title":
                bookList.sort(Comparator.comparing(Book::getTitle));
                break;
            case "publicationdate":
                bookList.sort(Comparator.comparing(Book::getPublicationDate));
                break;
            case "price":
                bookList.sort(Comparator.comparing(Book::getPrice));
                break;
            case "status":
                bookList.sort(Comparator.comparing(b -> b.getStatus().name()));
                break;
            default:
                // By default, we sort by name.
                bookList.sort(Comparator.comparing(Book::getTitle));
                System.out.println("Unknown sort parameter: '" + sortBy + "'. Sorting by title.");
                break;
        }

        return bookList;
    }

    public List<Order> getOrdersSorted(String sortBy) {
        List<Order> orderList = new ArrayList<>(orders.values());

        String normalizedSortBy = sortBy.toLowerCase().replaceAll("\\s+", "");

        switch (normalizedSortBy) {
            case "completiondate":
                orderList.sort(Comparator.comparing(
                        order -> order.getCompletionDate() != null ?
                                order.getCompletionDate() : LocalDateTime.MIN));
                break;
            case "price":
                orderList.sort(Comparator.comparing(Order::getTotalAmount));
                break;
            case "status":
                orderList.sort(Comparator.comparing(order -> order.getStatus().name()));
                break;
            default:
                // By default, we sort by order date.
                orderList.sort(Comparator.comparing(Order::getOrderDate));
                System.out.println("Unknown sort parameter: '" + sortBy + "'. Sorting by order date.");
                break;
        }

        return orderList;
    }

    // List of book requests (sort by number of requests, alphabetically)
    public List<Map<String, Object>> getBookRequestsSorted(String sortBy) {
        // Grouping queries by book
        Map<String, Long> requestCounts = bookRequests.values().stream()
                .collect(Collectors.groupingBy(
                        BookRequest::getBookId,
                        Collectors.counting()
                ));

        List<Map<String, Object>> result = new ArrayList<>();

        for (Map.Entry<String, Long> entry : requestCounts.entrySet()) {
            Book book = books.get(entry.getKey());
            if (book != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("book", book);
                item.put("requestCount", entry.getValue());
                result.add(item);
            }
        }

        String normalizedSortBy = sortBy.toLowerCase().replaceAll("\\s+", "");

        if (normalizedSortBy.equals("count")) {
            result.sort((a, b) -> Long.compare(
                    (Long) b.get("requestCount"),
                    (Long) a.get("requestCount")
            ));
        } else if (normalizedSortBy.equals("alphabet") || normalizedSortBy.equals("title")) {
            result.sort(Comparator.comparing(
                    item -> ((Book) item.get("book")).getTitle()
            ));
        }

        return result;
    }

    // List of completed orders over a period of time (sort by date, price)
    public List<Order> getCompletedOrdersForPeriod(LocalDateTime start, LocalDateTime end,
                                                   String sortBy) {
        List<Order> completedOrders = orders.values().stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .filter(o -> o.getCompletionDate() != null)
                .filter(o -> !o.getCompletionDate().isBefore(start) &&
                        !o.getCompletionDate().isAfter(end))
                .collect(Collectors.toList());

        String normalizedSortBy = sortBy.toLowerCase().replaceAll("\\s+", "");

        if (normalizedSortBy.equals("date")) {
            completedOrders.sort(Comparator.comparing(Order::getCompletionDate));
        } else if (normalizedSortBy.equals("price")) {
            completedOrders.sort(Comparator.comparing(Order::getTotalAmount));
        }

        return completedOrders;
    }

    // The amount of money earned over a period of time
    public double getRevenueForPeriod(LocalDateTime start, LocalDateTime end) {
        return getCompletedOrdersForPeriod(start, end, "date").stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }

    // The number of completed orders over a period of time
    public int getCompletedOrdersCountForPeriod(LocalDateTime start, LocalDateTime end) {
        return getCompletedOrdersForPeriod(start, end, "date").size();
    }

    public List<Book> getUnsoldBooks(String sortBy) {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);

        Set<String> soldBookIds = orders.values().stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .filter(o -> o.getCompletionDate() != null &&
                        !o.getCompletionDate().isBefore(sixMonthsAgo))
                .flatMap(o -> o.getItems().stream())
                .map(item -> item.book().getId())
                .collect(Collectors.toSet());

        List<Book> unsoldBooks = books.values().stream()
                .filter(b -> b.getStatus() == BookStatus.AVAILABLE)
                .filter(b -> !soldBookIds.contains(b.getId()))
                .collect(Collectors.toList());

        String normalizedSortBy = sortBy.toLowerCase().replaceAll("\\s+", "");

        switch (normalizedSortBy) {
            case "arrivaldate":
                unsoldBooks.sort(Comparator.comparing(Book::getArrivalDate));
                break;
            case "price":
                unsoldBooks.sort(Comparator.comparing(Book::getPrice));
                break;
            default:
                // By default, we sort by receipt date.
                unsoldBooks.sort(Comparator.comparing(Book::getArrivalDate));
                break;
        }

        return unsoldBooks;
    }

    public String getOrderDetails(String orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            return "Order not found";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== Order #").append(orderId).append(" ===\n");
        sb.append("Customer: ").append(order.getCustomer().name()).append("\n");
        sb.append("Email: ").append(order.getCustomer().email()).append("\n");
        sb.append("Phone: ").append(order.getCustomer().phone()).append("\n");
        sb.append("Status: ").append(order.getStatus()).append("\n");
        sb.append("Order date: ").append(order.getOrderDate()).append("\n");
        sb.append("Completion date: ").append(order.getCompletionDate()).append("\n");
        sb.append("Total amount: $").append(String.format("%.2f", order.getTotalAmount())).append("\n");
        sb.append("Books:\n");

        for (Order.OrderItem item : order.getItems()) {
            sb.append("  - ").append(item.toString()).append("\n");
        }

        return sb.toString();
    }

    public String getBookDescription(String bookId) {
        Book book = books.get(bookId);
        return book != null ? book.getDescription() : "Book not found";
    }

    // Auxiliary methods

    public Book getBook(String bookId) {
        return books.get(bookId);
    }

    public Order getOrder(String orderId) {
        return orders.get(orderId);
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders.values());
    }

    public List<BookRequest> getAllBookRequests() {
        return new ArrayList<>(bookRequests.values());
    }
}