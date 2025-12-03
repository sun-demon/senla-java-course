package bookstore.ui;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private final String name;
    private final String title;
    private final List<MenuItem> items;

    public Menu(String name, String title) {
        this.name = name;
        this.title = title;
        this.items = new ArrayList<>();
    }

    public void addItem(MenuItem item) {
        items.add(item);
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public List<MenuItem> getItems() {
        return new ArrayList<>(items);
    }

    public int getItemCount() {
        return items.size();
    }

    public MenuItem getItem(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return null;
    }
}