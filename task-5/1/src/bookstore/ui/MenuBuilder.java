package bookstore.ui;

import bookstore.controller.ActionController;

public class MenuBuilder {
    private static MenuBuilder instance;
    private Menu mainMenu;

    private MenuBuilder() {
        buildMenu();
    }

    public static MenuBuilder getInstance() {
        if (instance == null) {
            instance = new MenuBuilder();
        }
        return instance;
    }

    private void buildMenu() {
        ActionController actionController = ActionController.getInstance();

        // Main menu
        mainMenu = new Menu("main", "BOOKSTORE MANAGEMENT");

        // Books menu
        Menu booksMenu = new Menu("books", "BOOK MANAGEMENT");
        booksMenu.addItem(new MenuItem("View books", actionController::showBooksWithOptions, booksMenu));
        booksMenu.addItem(new MenuItem("Add new book", actionController::addBook, booksMenu));
        booksMenu.addItem(new MenuItem("Restock book", actionController::restockBook, booksMenu));
        booksMenu.addItem(new MenuItem("Write off book", actionController::writeOffBook, booksMenu));
        booksMenu.addItem(new MenuItem("View book details", actionController::viewBookDetails, booksMenu));

        // Orders menu
        Menu ordersMenu = new Menu("orders", "ORDER MANAGEMENT");
        ordersMenu.addItem(new MenuItem("Create order", actionController::createOrder, ordersMenu));
        ordersMenu.addItem(new MenuItem("View orders", actionController::viewOrdersSorted, ordersMenu));
        ordersMenu.addItem(new MenuItem("Cancel order", actionController::cancelOrder, ordersMenu));
        ordersMenu.addItem(new MenuItem("Change order status", actionController::changeOrderStatus, ordersMenu));
        ordersMenu.addItem(new MenuItem("View order details", actionController::viewOrderDetails, ordersMenu));

        // Reports menu
        Menu reportsMenu = new Menu("reports", "REPORTS");
        reportsMenu.addItem(new MenuItem("Unsold books report", actionController::showUnsoldBooks, reportsMenu));
        reportsMenu.addItem(new MenuItem("Completed orders report", actionController::showCompletedOrdersPeriod, reportsMenu));
        reportsMenu.addItem(new MenuItem("Revenue report", actionController::showRevenueReport, reportsMenu));

        // Requests menu
        Menu requestsMenu = new Menu("requests", "BOOK REQUESTS");
        requestsMenu.addItem(new MenuItem("Create request", actionController::createBookRequest, requestsMenu));
        requestsMenu.addItem(new MenuItem("View requests", actionController::viewBookRequestsSorted, requestsMenu));

        // Main menu items
        mainMenu.addItem(new MenuItem("Books", () -> {}, booksMenu));
        mainMenu.addItem(new MenuItem("Orders", () -> {}, ordersMenu));
        mainMenu.addItem(new MenuItem("Reports", () -> {}, reportsMenu));
        mainMenu.addItem(new MenuItem("Book Requests", () -> {}, requestsMenu));
    }

    public Menu getMainMenu() {
        return mainMenu;
    }
}