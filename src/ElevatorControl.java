import static java.lang.System.*;

public class ElevatorControl extends Thread {

    protected Building building; // TODO: trocar para Building_Ctrl
    protected Elevator elevator;
    protected Request destination;
    protected int direction;

    public ElevatorControl(Building building) {
        this.building = building;
        this.elevator = building.getElevator();
        this.destination = new Request(elevator.floorN);
        this.direction = 0;
    }

    // Pause to simulate waiting for people's actions
    private void pause() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
            exit(1);
        }
    }


    @Override
    public void run() {
        while (true) {
            out.println("\n\n\n\n"+building);

            // Check current state of elevator
            int currFloorN = elevator.getFloorN();
            boolean atAFloor = elevator.atAFloor();
            boolean isMoving = elevator.isMoving();
            boolean isFull = elevator.isFull();
            boolean requesting = atAFloor && elevator.pendingRequest(currFloorN);
            boolean arrived = elevator.atFloor(destination.floor);
            boolean pendingReqs = elevator.pendingRequests();

            // Decide what to do
            Actions task;
            if (isMoving && requesting && !isFull) {
                out.println("STOP");
                task = Actions.STOP;
            }
            else if (!isMoving && arrived && pendingReqs) {
                out.println("NEW_DESTINATION");
                task = Actions.NEW_DESTINATION;
            }
            else if (pendingReqs) {
                out.println("MOVE");
                task = Actions.MOVE;
            }
            else {
                out.println("IDLE");
                task = Actions.IDLE;
            }


            switch (task) {
                case MOVE:
                    if (!isMoving) {
                        building.grabDoor(currFloorN);
                        elevator.startMoving();
                        building.releaseDoor(currFloorN);
                    }
                    elevator.move(direction);
                    break;

                case STOP:
                    elevator.stopMoving();
                    building.unlockDoors(currFloorN);
                    elevator.clearRequest(currFloorN);
                    break;

                case IDLE:
                    elevator.idleController();
                    break;

                case NEW_DESTINATION:
                    destination = elevator.getNextDestination();
                    if (destination.floor > currFloorN) { direction = 1; }
                    else                                { direction = -1; }
                    out.println("-->"+destination.floor);
                    break;
            }
            pause();
        }
    }

    private enum Actions {
        NEW_DESTINATION,
        MOVE,
        STOP,
        IDLE,
    }
}
