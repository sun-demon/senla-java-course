package bookstore.ui;

public class ConsoleUI {
    private static ConsoleUI instance;
    private Navigator navigator;

    private ConsoleUI() {
        MenuBuilder builder = MenuBuilder.getInstance();
        this.navigator = new Navigator(builder.getMainMenu());
    }

    public static ConsoleUI getInstance() {
        if (instance == null) {
            instance = new ConsoleUI();
        }
        return instance;
    }

    public void start() {
        System.out.println("=".repeat(50));
        System.out.println("WELCOME TO ELECTRONIC BOOKSTORE");
        System.out.println("=".repeat(50));
        navigator.run();
    }
}