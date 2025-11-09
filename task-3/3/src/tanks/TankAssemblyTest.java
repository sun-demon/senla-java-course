package tanks;

import static java.lang.System.out;

public class TankAssemblyTest {
    public static void main(String[] args) {
        HullLineStep hullLineStep = new HullLineStep("composite", 120);
        out.println("Created " + hullLineStep);
        EngineLineStep engineLineStep = new EngineLineStep(1200, "diesel");
        out.println("Created " + engineLineStep);
        TurretLineStep turretLineStep = new TurretLineStep(125, true);
        out.println("Created " + turretLineStep);

        TankAssemblyLine tankAssemblyLine = new TankAssemblyLine(hullLineStep, engineLineStep, turretLineStep);
        out.println("Created " + tankAssemblyLine);
        Tank tank = (Tank) tankAssemblyLine.assembleProduct(new Tank());
        out.println("Created " + tank);
    }
}
