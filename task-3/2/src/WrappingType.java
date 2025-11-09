import java.math.BigDecimal;

public enum WrappingType {
    PAPER,
    FILM,
    CRAFT_PAPER,
    ORGANZA,
    SATIN,
    GIFT_BOX;

    public BigDecimal getPrice() {
        double price = switch (this) {
            case PAPER -> 0.30;
            case FILM -> 0.40;
            case CRAFT_PAPER -> 0.50;
            case ORGANZA -> 0.70;
            case SATIN -> 1.20;
            case GIFT_BOX -> 2.50;
            default -> throw new UnsupportedOperationException("Unreleased getPrice for wrapping " + this);
        };
        return BigDecimal.valueOf(price);
    }

    @Override
    public String toString() {
        return name().toLowerCase().replace('_', ' ');
    }
}
