package bookstore;

import bookstore.ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        ConsoleUI ui = ConsoleUI.getInstance();
        ui.start();
    }
}