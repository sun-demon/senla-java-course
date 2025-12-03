package bookstore.ui;

import java.util.Scanner;

public class Navigator {
    private Menu currentMenu;
    private boolean isRunning;

    public Navigator(Menu rootMenu) {
        this.currentMenu = rootMenu;
        this.isRunning = true;
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
        System.out.print("Select option: ");
    }

    public void navigate(int choice) {
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
        Scanner scanner = new Scanner(System.in);

        while (isRunning) {
            printMenu();
            try {
                String input = scanner.nextLine().trim();
                if (!input.isEmpty()) {
                    int choice = Integer.parseInt(input);
                    navigate(choice);
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number!");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        scanner.close();
    }

    public void setCurrentMenu(Menu menu) {
        this.currentMenu = menu;
    }
}