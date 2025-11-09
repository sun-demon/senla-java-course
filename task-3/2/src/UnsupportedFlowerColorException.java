public class UnsupportedFlowerColorException extends UnsupportedOperationException {
    public UnsupportedFlowerColorException(Class<? extends Flower> flowerClass, FlowerColor color) {
        super("Unsupported " + color + " color for " + flowerClass.getName().toLowerCase() + " flower");
    }
}
