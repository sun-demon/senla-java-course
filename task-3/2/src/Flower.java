import java.math.BigDecimal;
import java.util.Objects;

public abstract class Flower {
    protected final FlowerColor color;
    protected final BigDecimal price; // $

    public Flower(FlowerColor color, BigDecimal price) {
        this.color = color;
        if (price.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Flower price cannot be negative");
        this.price = price;
    }

    public FlowerColor getColor() { return color; }
    public BigDecimal getPrice() { return price; }

    @Override
    public String toString() {
        return color.toString() + " " + getClass().getSimpleName().toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Flower flower)) return false;
        if (!this.getClass().equals(o.getClass())) return false;
        return color == flower.color && price.equals(flower.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), color, price);
    }
}