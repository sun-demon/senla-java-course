import java.math.BigDecimal;

public class Rose extends Flower {

    public Rose(FlowerColor color) {
        super(color, getPriceByColor(color));
    }

    private static BigDecimal getPriceByColor(FlowerColor color) {
        BigDecimal basePrice = BigDecimal.valueOf(3.00);
        double score = switch (color) {
            case RED, ORANGE -> 1.0;
            case WHITE  -> 1.1;
            case PINK   -> 1.05;
            case YELLOW -> 0.95;
            case PURPLE -> 1.2;
            default -> throw new UnsupportedFlowerColorException(Rose.class, color);
        };
        return basePrice.multiply(BigDecimal.valueOf(score));
    }
}