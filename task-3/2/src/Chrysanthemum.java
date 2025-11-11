import java.math.BigDecimal;

public class Chrysanthemum extends Flower {

    public Chrysanthemum(FlowerColor color) {
        super(color, getPriceByColor(color));
    }

    private static BigDecimal getPriceByColor(FlowerColor color) {
        BigDecimal basePrice = BigDecimal.valueOf(1.50);
        double multiplier = switch (color) {
            case WHITE, YELLOW -> 1.0;
            case PINK, RED -> 1.1;
            case PURPLE -> 1.25;
            case ORANGE -> 1.15;
            default -> throw new UnsupportedFlowerColorException(Chrysanthemum.class, color);
        };
        return basePrice.multiply(BigDecimal.valueOf(multiplier));
    }
}