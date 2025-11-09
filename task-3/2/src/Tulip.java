import java.math.BigDecimal;

public class Tulip extends Flower {

    public Tulip(FlowerColor color) {
        super(color, getPriceByColor(color));
    }

    private static BigDecimal getPriceByColor(FlowerColor color) {
        BigDecimal basePrice = BigDecimal.valueOf(1.20);
        double multiplier = switch (color) {
            case RED, PINK -> 1.0;
            case YELLOW -> 0.9;
            case WHITE -> 1.1;
            case PURPLE -> 1.3;
            case ORANGE -> 1.15;
            default -> throw new UnsupportedFlowerColorException(Tulip.class, color);
        };
        return basePrice.multiply(BigDecimal.valueOf(multiplier));
    }
}