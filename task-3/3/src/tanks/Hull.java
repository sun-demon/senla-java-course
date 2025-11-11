package tanks;

import assemblyline.IProductPart;

/**
 * @param thickness mm
 */
public record Hull(String armorType, double thickness) implements IProductPart {
    public Hull {
        if (thickness < 0)
            throw new IllegalArgumentException("Hull thickness cannot be negative");
    }

    @Override
    public String toString() {
        return thickness + "mm " + armorType + " hull";
    }
}