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
        while (true) {
            // Check current state of elevator
            int currFloorN = elevator.getFloorN();

            // Decide what to do
            Actions task = Actions.IDLE;

            switch (task) {
                case MOVE:
                    break;

                case STOP:
                    break;

                case IDLE:
                    break;

                case NEW_DESTINATION:
                    break;
            }
            out.println("\n\n\n\n"+building);
            pause();
        }
    }

    private enum Actions {
        ARRIVED,
        NEW_DESTINATION,
        MOVE,
        STOP,
        IDLE,
    }
}
