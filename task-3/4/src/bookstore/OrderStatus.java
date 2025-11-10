package bookstore;

public enum OrderStatus {
    NEW("Новый"),
    PROCESSING("В обработке"),
    COMPLETED("Выполнен"),
    CANCELLED("Отменен");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}