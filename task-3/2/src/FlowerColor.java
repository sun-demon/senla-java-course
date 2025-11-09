public enum FlowerColor {
    RED,
    WHITE,
    YELLOW,
    PINK,
    ORANGE,
    PURPLE;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}