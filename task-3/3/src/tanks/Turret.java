package tanks;

import assemblyline.IProductPart;

/**
 * @param gunCaliber mm
 */
public record Turret(double gunCaliber, boolean hasStabilizer) implements IProductPart {
    public Turret {
        if (gunCaliber < 0)
            throw new IllegalArgumentException("Turret gun caliber cannot be negative");
    }

    @Override
    public String toString() {
        return "turret with " + gunCaliber + "mm " + (hasStabilizer ? "" : "no ") + "stabilizer gun";
    }
}