public class ElevatorControl extends Thread {

    protected Building building;

    public ElevatorControl(Building building) {
        this.building = building;
    }

    @Override
    public void run() {
        // Cycle:
        // - Check requests
        // - Move elevator 1 unit
        // Open doors
        // Close doors
    }
}
