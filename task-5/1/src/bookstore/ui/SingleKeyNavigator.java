package bookstore.ui;

import java.util.Scanner;

public class SingleKeyNavigator {
    private Menu currentMenu;
    private boolean isRunning;
    private final Scanner scanner;

    public SingleKeyNavigator(Menu rootMenu) {
        this.currentMenu = rootMenu;
        this.isRunning = true;
        this.scanner = new Scanner(System.in);
    }

    public void printMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println(currentMenu.getTitle());
        System.out.println("=".repeat(50));

        int index = 1;
        for (MenuItem item : currentMenu.getItems()) {
            System.out.printf("%d. %s%n", index++, item.getTitle());
        }

        System.out.println("0. " + (currentMenu.getName().equals("main") ? "Exit" : "Back"));
        System.out.println("=".repeat(50));
        System.out.print("Select option (0-" + (index-1) + "): ");
    }

    public void navigate(String input) {
        if (input.length() != 1) {
            System.out.println("Please enter a single digit!");
            return;
        }

        char key = input.charAt(0);
        int choice = Character.getNumericValue(key);

        if (choice == 0) {
            if (currentMenu.getName().equals("main")) {
                isRunning = false;
                System.out.println("\nGoodbye!");
            } else {
                currentMenu = MenuBuilder.getInstance().getMainMenu();
            }
            return;
        }

        int itemIndex = choice - 1;
        if (itemIndex >= 0 && itemIndex < currentMenu.getItemCount()) {
            MenuItem selectedItem = currentMenu.getItem(itemIndex);

            // Execute action
            selectedItem.execute();

            // Navigate to next menu if specified
            if (selectedItem.getNextMenu() != null) {
                currentMenu = selectedItem.getNextMenu();
            }
        } else {
            System.out.println("Invalid option!");
        }
    }

    public void run() {
        while (isRunning) {
            printMenu();
            try {
                String input = scanner.nextLine().trim();
                if (input.length() == 1) {
                    navigate(input);
                } else {
                    System.out.println("Please enter a single digit!");
                }
            } catch (Exception e) {
                System.out.println("Invalid input! Please try again.");
            }
        }
        scanner.close();
    }

    public void setCurrentMenu(Menu menu) {
        this.currentMenu = menu;
    }
}