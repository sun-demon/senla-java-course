package tanks;

import assemblyline.IAssemblyLine;
import assemblyline.IProduct;

public record TankAssemblyLine(
        HullLineStep hullLineStep,
        EngineLineStep engineLineStep,
        TurretLineStep turretLineStep
) implements IAssemblyLine {
    @Override
    public IProduct assembleProduct(IProduct product) {
        if (product instanceof Tank) {
            product.installFirstPart(hullLineStep.buildProductPart());
            product.installSecondPart(engineLineStep.buildProductPart());
            product.installThirdPart(turretLineStep.buildProductPart());
            return product;
        } else {
            throw new ClassCastException("Tank assembly line construct only Tank type objects");
        }
    }

    @Override
    public String toString() {
        return "tank assembly line with " + hullLineStep + ", " + engineLineStep + " and " + turretLineStep;
    }
}
