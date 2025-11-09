package tanks;

import assemblyline.ILineStep;
import assemblyline.IProductPart;

public record HullLineStep(String armorType, double thickness) implements ILineStep {
    public HullLineStep {
        if (thickness < 0)
            throw new IllegalArgumentException("Hull thickness cannot be negative");
    }

    @Override
    public IProductPart buildProductPart() {
        return new Hull("composite", 120.0);
    }

    @Override
    public String toString() {
        return thickness + "mm " + armorType + " hull line step";
    }
}