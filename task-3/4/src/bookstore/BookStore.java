package bookstore;

import java.util.*;

public class BookStore {
    private final List<Book> books;
    private final List<Order> orders;
    private final List<BookRequest> requests;

    public BookStore() {
        this.books = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.requests = new ArrayList<>();
        initializeSampleBooks();
    }

    private void initializeSampleBooks() {
        books.add(new Book("978-5-699-12014-7", "Мастер и Маргарита", "Михаил Булгаков", 15.99, 10));
        books.add(new Book("978-5-17-090823-9", "1984", "Джордж Оруэлл", 12.50, 5));
        books.add(new Book("978-5-389-08243-8", "Преступление и наказание", "Фёдор Достоевский", 14.75, 0));
        books.add(new Book("978-5-04-103497-9", "Гарри Поттер и философский камень", "Дж. К. Роулинг", 18.99, 8));
        books.add(new Book("978-5-17-100389-7", "Война и мир", "Лев Толстой", 22.25, 3));
    }

    // 1. Добавить книгу на склад
    public void addBook(String isbn, String title, String author, double price, int quantity) {
        Book existingBook = findBookByIsbn(isbn);
        if (existingBook != null) {
            existingBook.increaseQuantity(quantity);
            System.out.println("Добавлены книги: " + title + " (+" + quantity + " шт.)");
            fulfillRequestsForBook(existingBook);
        } else {
            Book newBook = new Book(isbn, title, author, price, quantity);
            books.add(newBook);
            System.out.println("Добавлена новая книга: " + title);
        }
    }

    // 2. Списать книгу со склада
    public void writeOffBook(String isbn, int quantity) {
        Book book = findBookByIsbn(isbn);
        if (book == null) {
            throw new IllegalArgumentException("Книга с ISBN " + isbn + " не найдена");
        }
        book.decreaseQuantity(quantity);
        System.out.println("Списаны книги: " + book.getTitle() + " (-" + quantity + " шт.)");
    }

    // 3. Создать заказ
    public Order createOrder() {
        Order order = new Order();
        orders.add(order);
        System.out.println("Создан новый заказ #" + order.getOrderId());
        return order;
    }

    public void addBookToOrder(String orderId, String isbn, int quantity) {
        Order order = findOrderById(orderId);
        Book book = findBookByIsbn(isbn);

        if (order == null || book == null) {
            throw new IllegalArgumentException("Заказ или книга не найдены");
        }

        if (book.getStatus() == BookStatus.OUT_OF_STOCK) {
            addBookRequest(book, quantity);
            System.out.println("Книга '" + book.getTitle() + "' отсутствует. Создан запрос.");
        }

        OrderItem item = new OrderItem(book, quantity);
        order.addItem(item);
        System.out.println("Добавлена в заказ: " + item.toString());
    }

    // 4. Отменить заказ
    public void cancelOrder(String orderId) {
        Order order = findOrderById(orderId);
        if (order != null) {
            order.setStatus(OrderStatus.CANCELLED);
            System.out.println("Заказ #" + orderId + " отменен");
        }
    }

    // 5. Изменить статус заказа
    public void updateOrderStatus(String orderId, OrderStatus newStatus) {
        Order order = findOrderById(orderId);
        if (order != null) {
            order.setStatus(newStatus);
            System.out.println("Статус заказа #" + orderId + " изменен на: " + newStatus.getDescription());

            if (newStatus == OrderStatus.COMPLETED) {
                // При завершении заказа списываем книги
                for (OrderItem item : order.getItems()) {
                    Book book = item.getBook();
                    if (book.getStatus() == BookStatus.IN_STOCK) {
                        book.decreaseQuantity(item.getQuantity());
                    }
                }
            }
        }
    }

    // 6. Оставить запрос на книгу
    public void addBookRequest(Book book, int quantity) {
        BookRequest request = new BookRequest(book, quantity);
        requests.add(request);
        System.out.println("Создан запрос на книгу: " + request.toString());
    }

    // 7. Выполнить запрос на книгу (при поступлении книги)
    private void fulfillRequestsForBook(Book book) {
        for (BookRequest request : requests) {
            if (!request.isFulfilled() && request.getBook().equals(book)) {
                if (book.getQuantity() >= request.getQuantity()) {
                    request.markFulfilled();
                    System.out.println("Выполнен запрос #" + request.getRequestId() + " на книгу: " + book.getTitle());
                }
            }
        }
        // Удаляем выполненные запросы
        requests.removeIf(BookRequest::isFulfilled);
    }

    // Вспомогательные методы
    private Book findBookByIsbn(String isbn) {
        return books.stream()
                .filter(book -> book.getIsbn().equals(isbn))
                .findFirst()
                .orElse(null);
    }

    private Order findOrderById(String orderId) {
        return orders.stream()
                .filter(order -> order.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    // Методы для получения информации
    public void displayAllBooks() {
        System.out.println("\n=== КАТАЛОГ КНИГ ===");
        books.forEach(System.out::println);
    }

    public void displayAllOrders() {
        System.out.println("\n=== ВСЕ ЗАКАЗЫ ===");
        orders.forEach(System.out::println);
    }

    public void displayPendingRequests() {
        System.out.println("\n=== ОЖИДАЮЩИЕ ЗАПРОСЫ ===");
        requests.stream()
                .filter(request -> !request.isFulfilled())
                .forEach(System.out::println);
    }
}