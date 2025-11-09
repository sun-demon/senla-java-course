package tanks;

import assemblyline.ILineStep;
import assemblyline.IProductPart;

public record TurretLineStep(double gunCaliber, boolean hasStabilizer) implements ILineStep {
    public TurretLineStep {
        if (gunCaliber < 0)
            throw new IllegalArgumentException("Turret gun caliber cannot be negative");
    }

    @Override
    public IProductPart buildProductPart() {
        return new Turret(125, true);
    }

    @Override
    public String toString() {
        return "turret line step with " + gunCaliber + "mm " + (hasStabilizer ? "" : "no ") + "gun";
    }
}