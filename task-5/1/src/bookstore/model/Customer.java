package bookstore.model;

import java.util.Objects;

public record Customer(String name, String email, String phone) {
    public Customer(String name, String email, String phone) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.email = validateEmail(email);
        this.phone = Objects.requireNonNull(phone, "Phone cannot be null");
    }

    private String validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return email.equals(customer.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return String.format("%s <%s>", name, email);
    }
}