package bookstore;

public class BookStoreTest {
    public static void main(String[] args) {
        System.out.println("=== ТЕСТИРОВАНИЕ ЭЛЕКТРОННОГО КНИЖНОГО МАГАЗИНА ===\n");

        BookStore store = new BookStore();

        // 1. Показываем начальный каталог
        store.displayAllBooks();

        // 2. Создаем заказ
        System.out.println("\n--- СОЗДАНИЕ ЗАКАЗА ---");
        Order order1 = store.createOrder();
        store.addBookToOrder(order1.getOrderId(), "978-5-699-12014-7", 2); // В наличии
        store.addBookToOrder(order1.getOrderId(), "978-5-389-08243-8", 1); // Нет в наличии - создаст запрос

        // 3. Показываем запросы
        store.displayPendingRequests();

        // 4. Добавляем отсутствующую книгу на склад
        System.out.println("\n--- ПОСТУПЛЕНИЕ КНИГ НА СКЛАД ---");
        store.addBook("978-5-389-08243-8", "Преступление и наказание", "Фёдор Достоевский", 14.75, 5);

        // 5. Показываем обновленный каталог и запросы
        store.displayAllBooks();
        store.displayPendingRequests();

        // 6. Создаем еще один заказ
        System.out.println("\n--- ВТОРОЙ ЗАКАЗ ---");
        Order order2 = store.createOrder();
        store.addBookToOrder(order2.getOrderId(), "978-5-17-090823-9", 1);
        store.addBookToOrder(order2.getOrderId(), "978-5-17-100389-7", 2);

        // 7. Изменяем статус заказа
        System.out.println("\n--- ИЗМЕНЕНИЕ СТАТУСА ЗАКАЗА ---");
        store.updateOrderStatus(order2.getOrderId(), OrderStatus.COMPLETED);

        // 8. Списание книг
        System.out.println("\n--- СПИСАНИЕ КНИГ ---");
        store.writeOffBook("978-5-699-12014-7", 1);

        // 9. Показываем итоговое состояние
        System.out.println("\n=== ИТОГОВОЕ СОСТОЯНИЕ ===");
        store.displayAllBooks();
        store.displayAllOrders();
        store.displayPendingRequests();

        System.out.println("\n=== ТЕСТИРОВАНИЕ ЗАВЕРШЕНО ===");
    }
}