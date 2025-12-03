package bookstore.factory;

import bookstore.ui.Menu;
import bookstore.ui.MenuItem;

public interface MenuFactory {
    Menu createMenu(String name, String title);
    MenuItem createMenuItem(String title, Runnable action);
    MenuItem createMenuItem(String title, Runnable action, Menu nextMenu);
}