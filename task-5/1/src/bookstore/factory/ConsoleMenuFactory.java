package bookstore.factory;

import bookstore.ui.Menu;
import bookstore.ui.MenuItem;

public class ConsoleMenuFactory implements MenuFactory {
    private static ConsoleMenuFactory instance;

    private ConsoleMenuFactory() {}

    public static synchronized ConsoleMenuFactory getInstance() {
        if (instance == null) {
            instance = new ConsoleMenuFactory();
        }
        return instance;
    }

    @Override
    public Menu createMenu(String name, String title) {
        return new Menu(name, title);
    }

    @Override
    public MenuItem createMenuItem(String title, Runnable action) {
        return new MenuItem(title, action::run);
    }

    @Override
    public MenuItem createMenuItem(String title, Runnable action, Menu nextMenu) {
        return new MenuItem(title, action::run, nextMenu);
    }
}