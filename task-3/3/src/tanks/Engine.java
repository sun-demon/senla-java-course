package tanks;

import assemblyline.IProductPart;

public record Engine(int horsepower, String fuelType) implements IProductPart {
    public Engine {
        if (horsepower < 0)
            throw new IllegalArgumentException("Engine power cannot be negative");
    }

    @Override
    public String toString() {
        return horsepower + "hp " + fuelType + " engine";
    }
}