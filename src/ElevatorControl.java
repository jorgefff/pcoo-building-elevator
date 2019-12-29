import static java.lang.System.*;

public class ElevatorControl extends Thread {

    protected Building building; // TODO: trocar para Building_Ctrl
    protected Elevator elevator;
    protected Request destination;
    protected int direction;

    public ElevatorControl(Building building) {
        this.building = building;
        this.elevator = building.getElevator();
        this.destination = null;
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
        State currState = State.IDLE;
        State nextState = State.IDLE;
        for(;;currState = nextState) {
            // Update info
            int currFloorN = elevator.getFloorN();
            Floor currFloor = building.getFloor(currFloorN);


            out.println("\n\n\n\n"+building);

            switch (currState) {
                case IDLE:
                    building.idle();
                    nextState = State.CHECK_REQUESTS;
                    break;

                case MOVING:
                    elevator.move(direction);
                    break;

                case STOPPED:
                    break;

                case CHECK_REQUESTS:
                    if (!building.pendingRequests()) {
                        nextState = State.IDLE;
                        break;
                    }
                    destination = building.getNextDestination();
                    if (destination.floor > currFloorN) { direction = 1; }
                    else                                { direction = -1; }
                    currFloor.grabElevatorDoor();
                    elevator.startMoving();
                    currFloor.releaseElevatorDoor();
                    nextState = State.MOVING;
                    break;
            }

            pause();
        }
    }

    private enum State {
        IDLE,
        MOVING,
        STOPPED,
        CHECK_REQUESTS,
    }
}
