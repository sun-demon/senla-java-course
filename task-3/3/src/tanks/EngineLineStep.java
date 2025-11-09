package tanks;

import assemblyline.ILineStep;
import assemblyline.IProductPart;

public record EngineLineStep(int horsepower, String fuelType) implements ILineStep {
    public EngineLineStep {
        if (horsepower < 0)
            throw new IllegalArgumentException("Engine power cannot be negative");
    }

    @Override
    public IProductPart buildProductPart() {
        return new Engine(horsepower, fuelType);
    }

    @Override
    public String toString() {
        return horsepower + "hp " + fuelType + " engine line step";
    }
}