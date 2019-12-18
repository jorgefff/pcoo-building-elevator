import static java.lang.System.*;

public class ElevatorControl extends Thread {

    protected Building building;
    protected Elevator elevator;
    protected boolean goingUp;

    public ElevatorControl(Building building) {
        this.building = building;
        this.elevator = building.getElevator();
        this.goingUp = false;
    }

    @Override
    public void run() {
        while (true) {
            out.println("\n\n\n\n"+building);
            // Check current state of elevator
            int currFloorN = elevator.getFloorN();
            boolean atAFloor = elevator.atAFloor();
            boolean isMoving = elevator.isMoving();
            boolean floorRequesting = building.getFloor(currFloorN).requestingElevator();

            
        }
    }
}
