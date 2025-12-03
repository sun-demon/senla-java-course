package bookstore.ui;

public class MenuItem {
    private final String title;
    private final Action action;
    private Menu nextMenu;

    public MenuItem(String title, Action action) {
        this.title = title;
        this.action = action;
    }

    public MenuItem(String title, Action action, Menu nextMenu) {
        this.title = title;
        this.action = action;
        this.nextMenu = nextMenu;
    }

    public void execute() {
        if (action != null) {
            action.execute();
        }
    }

    public String getTitle() {
        return title;
    }

    public Menu getNextMenu() {
        return nextMenu;
    }

    public void setNextMenu(Menu nextMenu) {
        this.nextMenu = nextMenu;
    }

    @Override
    public String toString() {
        return title;
    }
}