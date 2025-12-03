package bookstore.service;

import bookstore.model.Book;
import bookstore.model.BookRequest;
import bookstore.model.Customer;
import bookstore.model.Order;
import bookstore.enums.BookStatus;
import bookstore.enums.OrderStatus;

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
        // The minimum set for testing
        addTestBook("B001", "Master and Margarita", "Mikhail Bulgakov", 29.99);
        addTestBook("B002", "Crime and Punishment", "Fyodor Dostoevsky", 24.99);
        addTestBook("B003", "War and Peace", "Leo Tolstoy", 34.99);
    }

    private void addTestBook(String id, String title, String author, double price) {
        books.put(id, new Book(
                id, title, author, "ISBN-" + id,
                LocalDate.now().minusYears(1), price, BookStatus.AVAILABLE,
                LocalDate.now().minusMonths(3), "Classic novel"
        ));
    }

    // Basic methods
    public boolean writeOffBook(String bookId) {
        Book book = books.get(bookId);
        if (book != null && book.getStatus() == BookStatus.AVAILABLE) {
            book.setStatus(BookStatus.OUT_OF_STOCK);
            return true;
        }
        return false;
    }

    public Order createOrder(String name, String email, String phone,
                             Map<String, Integer> bookQuantities) {
        Customer customer = new Customer(name, email, phone);
        customers.put(email, customer);

        List<Order.OrderItem> items = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : bookQuantities.entrySet()) {
            Book book = books.get(entry.getKey());
            if (book != null) {
                items.add(new Order.OrderItem(book, entry.getValue()));
                if (book.getStatus() == BookStatus.OUT_OF_STOCK) {
                    createBookRequest(book.getId(), email);
                }
            }
        }

        if (items.isEmpty()) return null;

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
        if (order == null) return false;

        // Verification according to the TOR
        if (newStatus == OrderStatus.COMPLETED) {
            // We check that all books are available.
            boolean allBooksAvailable = order.getItems().stream()
                    .allMatch(item -> item.book().getStatus() == BookStatus.AVAILABLE);

            if (!allBooksAvailable) {
                return false; // It cannot be completed if there are books out of stock.
            }

            // We check that there are no open requests for books from the order.
            boolean hasOpenRequests = order.getItems().stream()
                    .anyMatch(item -> bookRequests.values().stream()
                            .anyMatch(req -> req.getBookId().equals(item.book().getId())));

            if (hasOpenRequests) {
                return false; // It cannot be completed if there are open requests.
            }
        }

        order.setStatus(newStatus);

        // If the order is cancelled, we will return the books to the "in stock" status
        if (newStatus == OrderStatus.CANCELLED) {
            order.getItems().forEach(item ->
                    item.book().setStatus(BookStatus.AVAILABLE));
        }

        return true;
    }

    public boolean addExistingBookToStock(String bookId) {
        Book book = books.get(bookId);
        if (book != null && book.getStatus() == BookStatus.OUT_OF_STOCK) {
            book.setStatus(BookStatus.AVAILABLE);
            book.setArrivalDate(LocalDate.now());
            return true;
        }
        return false;
    }

    public String addNewBookToStock(String title, String author, String isbn,
                                    LocalDate pubDate, double price, String description) {
        try {
            String bookId = "B" + String.format("%03d", books.size() + 1);
            Book book = new Book(bookId, title, author, isbn, pubDate,
                    price, BookStatus.AVAILABLE, LocalDate.now(), description);
            books.put(bookId, book);
            return bookId;
        } catch (Exception e) {
            return null;
        }
    }

    public BookRequest createBookRequest(String bookId, String email) {
        Book book = books.get(bookId);
        if (book != null && book.getStatus() == BookStatus.OUT_OF_STOCK) {
            String requestId = "REQ" + (bookRequests.size() + 1);
            BookRequest request = new BookRequest(requestId, bookId, email);
            bookRequests.put(requestId, request);
            return request;
        }
        return null;
    }

    // Viewing methods
    public List<Book> getBooksSorted(String sortBy, boolean ascending) {
        return books.values().stream()
                .sorted(getBookComparator(sortBy, ascending))
                .collect(Collectors.toList());
    }

    public List<Order> getOrdersSorted(String sortBy, boolean ascending) {
        return orders.values().stream()
                .sorted(getOrderComparator(sortBy, ascending))
                .collect(Collectors.toList());
    }

    public double getRevenueForPeriod(LocalDateTime start, LocalDateTime end) {
        return orders.values().stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .filter(o -> o.getCompletionDate() != null)
                .filter(o -> !o.getCompletionDate().isBefore(start) &&
                        !o.getCompletionDate().isAfter(end))
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }

    public List<Book> getUnsoldBooks(String sortBy, boolean ascending) {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);

        Set<String> recentlySoldBookIds = orders.values().stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .filter(o -> o.getCompletionDate() != null &&
                        o.getCompletionDate().isAfter(sixMonthsAgo))
                .flatMap(o -> o.getItems().stream())
                .map(item -> item.book().getId())
                .collect(Collectors.toSet());

        Comparator<Book> comparator = getUnsoldBookComparator(sortBy, ascending);

        return books.values().stream()
                .filter(b -> b.getStatus() == BookStatus.AVAILABLE)
                .filter(b -> !recentlySoldBookIds.contains(b.getId()))
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public List<Order> getCompletedOrdersForPeriod(LocalDateTime start, LocalDateTime end,
                                                   String sortBy, boolean ascending) {
        Comparator<Order> comparator;
        if (sortBy.equalsIgnoreCase("price")) {
            comparator = Comparator.comparing(Order::getTotalAmount);
        } else {
            comparator = Comparator.comparing(Order::getCompletionDate);
        }

        if (!ascending) {
            comparator = comparator.reversed();
        }

        return orders.values().stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .filter(o -> o.getCompletionDate() != null)
                .filter(o -> !o.getCompletionDate().isBefore(start) &&
                        !o.getCompletionDate().isAfter(end))
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public int getCompletedOrdersCountForPeriod(LocalDateTime start, LocalDateTime end) {
        return (int) orders.values().stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .filter(o -> o.getCompletionDate() != null)
                .filter(o -> !o.getCompletionDate().isBefore(start) &&
                        !o.getCompletionDate().isAfter(end))
                .count();
    }

    // Auxiliary methods
    public Book getBook(String bookId) {
        return books.get(bookId);
    }

    public List<Map<String, Object>> getBookRequestsSorted(String sortBy, boolean ascending) {
        Map<String, Long> requestCounts = bookRequests.values().stream()
                .collect(Collectors.groupingBy(
                        BookRequest::getBookId,
                        Collectors.counting()
                ));

        List<Map<String, Object>> result = requestCounts.entrySet().stream()
                .map(entry -> {
                    Book book = books.get(entry.getKey());
                    if (book != null) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("book", book);
                        item.put("requestCount", entry.getValue());
                        return item;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        String normalized = sortBy.toLowerCase().replaceAll("[^a-z]", "");

        Comparator<Map<String, Object>> comparator;
        if (normalized.equals("count")) {
            comparator = Comparator.comparing(item -> (Long) item.get("requestCount"));
        } else {
            comparator = Comparator.comparing(item -> ((Book) item.get("book")).getTitle());
        }

        if (!ascending) {
            comparator = comparator.reversed();
        }

        result.sort(comparator);
        return result;
    }

    public Optional<Book> getBookOptional(String bookId) {
        return Optional.ofNullable(books.get(bookId));
    }

    public Optional<Order> getOrderOptional(String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    public String getBookDescription(String bookId) {
        return getBookOptional(bookId)
                .map(Book::getDescription)
                .orElse("Book not found");
    }

    public String getOrderDetails(String orderId) {
        return getOrderOptional(orderId)
                .map(this::formatOrderDetails)
                .orElse("Order not found");
    }

    private String formatOrderDetails(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Order #").append(order.getId()).append(" ===\n");
        sb.append("Customer: ").append(order.getCustomer().name()).append("\n");
        sb.append("Email: ").append(order.getCustomer().email()).append("\n");
        sb.append("Phone: ").append(order.getCustomer().phone()).append("\n");
        sb.append("Status: ").append(order.getStatus()).append("\n");
        sb.append("Total: $").append(String.format("%.2f", order.getTotalAmount())).append("\n");
        sb.append("Books:\n");

        order.getItems().forEach(item ->
                sb.append("  - ").append(item.toString()).append("\n"));

        return sb.toString();
    }

    private Comparator<Book> getBookComparator(String sortBy, boolean ascending) {
        String normalized = sortBy.toLowerCase().replaceAll("[^a-z]", "");

        Comparator<Book> comparator = switch (normalized) {
            case "date", "publicationdate" -> Comparator.comparing(Book::getPublicationDate);
            case "price" -> Comparator.comparing(Book::getPrice);
            case "status", "availability" -> Comparator.comparing(b -> b.getStatus().name());
            case "arrival" -> Comparator.comparing(Book::getArrivalDate);
            default -> Comparator.comparing(Book::getTitle);
        };

        return ascending ? comparator : comparator.reversed();
    }

    private Comparator<Order> getOrderComparator(String sortBy, boolean ascending) {
        String normalized = sortBy.toLowerCase().replaceAll("[^a-z]", "");

        Comparator<Order> comparator = switch (normalized) {
            case "date", "completiondate" -> Comparator.comparing(
                    o -> o.getCompletionDate() != null ?
                            o.getCompletionDate() : LocalDateTime.MIN);
            case "price" -> Comparator.comparing(Order::getTotalAmount);
            case "status" -> Comparator.comparing(o -> o.getStatus().name());
            default -> Comparator.comparing(Order::getOrderDate);
        };

        return ascending ? comparator : comparator.reversed();
    }

    private Comparator<Book> getUnsoldBookComparator(String sortBy, boolean ascending) {
        String normalized = sortBy.toLowerCase().replaceAll("[^a-z]", "");

        Comparator<Book> comparator = switch (normalized) {
            case "price" -> Comparator.comparing(Book::getPrice);
            case "arrival", "arrivaldate" -> Comparator.comparing(Book::getArrivalDate);
            default -> Comparator.comparing(Book::getArrivalDate);
        };

        return ascending ? comparator : comparator.reversed();
    }
}