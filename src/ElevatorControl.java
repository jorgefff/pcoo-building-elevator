import static java.lang.System.*;

public class ElevatorControl extends Thread {

    protected Building building;
    protected Elevator elevator;
    protected int destination;
    protected int direction;

    public ElevatorControl(Building building) {
        this.building = building;
        this.elevator = building.getElevator();
        this.destination = elevator.getFloorN();
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
            boolean elevatorReq = elevator.isFloorRequesting(currFloorN);
            boolean isFull = elevator.isFull();
            boolean floorRequesting = atAFloor && building.pendingRequest(currFloorN);
            boolean arrived = elevator.atFloor(destination);
            boolean pendingEReqs = elevator.pendingRequests();
            boolean pendingBReqs = building.pendingRequests();
            boolean pendingReqs = pendingBReqs || pendingEReqs;

            // Decide what to do
            Actions task;
            if (    (isMoving && elevatorReq) ||
                    (isMoving && floorRequesting && !isFull)) {
                out.println("STOP");
//                out.println("isMoving:"+isMoving);
//                out.println("elevatorReq:"+elevatorReq);
//                out.println("floorRequesting:"+floorRequesting);
//                out.println("isFull:"+isFull);
//                out.println("CurrFloor:"+currFloorN);
//                for (Long r : building.requests) { out.print(" F:"+r); }
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
                    building.openDoors(currFloorN);
                    building.resetRequest(currFloorN);
                    break;
                case IDLE:
                    building.elevatorIdle();
                    break;
                case NEW_DESTINATION:
                    Request req = null;
                    Request breq = null;
                    if (pendingEReqs) {
                        req = elevator.getNextDestination();
                    }
                    if (pendingBReqs) {
                        breq = building.getNextDestination();
                        if (req == null || req.timestamp > breq.timestamp) {
                            req = breq;
                        }
                    }
                    destination = req.floor;
                    if (destination > currFloorN)   { direction = 1; }
                    else                            { direction = -1; }
                    out.println("-->"+destination);
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
