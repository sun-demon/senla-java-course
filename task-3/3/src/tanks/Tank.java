package tanks;

import assemblyline.IProduct;
import assemblyline.IProductPart;

public class Tank implements IProduct {
    private Hull hull;
    private Engine engine;
    private Turret turret;

    Tank() {}

    @Override
    public void installFirstPart(IProductPart part) {
        if (part instanceof Hull)
            this.hull = (Hull) part;
        else
            throw new ClassCastException("First part type must be Hull");
    }

    @Override
    public void installSecondPart(IProductPart part) {
        if (part instanceof Engine)
            this.engine = (Engine) part;
        else
            throw new ClassCastException("Second part type must be Engine");
    }

    @Override
    public void installThirdPart(IProductPart part) {
        if (part instanceof Turret)
            this.turret = (Turret) part;
        else
            throw new ClassCastException("Third part type must be Turret");
    }

    @Override
    public String toString() {
        return "tank with " + hull + ", " + engine + " and " + turret;
    }
}
