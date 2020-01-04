import pt.ua.concurrent.Mutex;
import pt.ua.concurrent.MutexCV;


public class Building implements Building_Prsn, Building_Ctrl{

    protected boolean ELEVATOR_PRIORITY;
    protected int numFloors;
    protected Floor[] floors;
    protected Elevator elevator;

    protected Mutex idle;
    protected MutexCV idleCV;

    /**
     * Constructor
     * @param elePriority whether the controller gives priority to requests coming from elevator or just uses the oldest one
     */
    public Building(boolean elePriority) {
        this.ELEVATOR_PRIORITY = elePriority;
        this.idle = new Mutex(true);
        this.idleCV = idle.newCV();
    }

    /**
     * Initializes building floors
     * @param n Number of floors
     */
    public void generateFloors(int n) {
        assert floors == null : "Floors already created";
        assert n > 0 : "Bad number of floors";

        numFloors = n;
        floors = new Floor[numFloors];
        for (int i = 0; i < numFloors; i++) {
            floors[i] = new Floor(i, this);
        }
    }

    /**
     * Generates the building elevator
     * @param capacity How many people fit inside the elevator at once
     */
    public void generateElevator(int capacity) {
        assert elevator == null : "Elevator already created";
        assert floors != null : "Floors must be created first";
        assert capacity > 0 : "Bad capacity";

        elevator = new Elevator (this, capacity, numFloors);
    }

    /* **********************************************************************************************************
     * Common methods
     */

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        int elevatorFloor = elevator.getFloorN();
        for (int i = numFloors-1; i >= 0; i--) {
            str.append("[ ").append(floors[i].getOccupancy()).append(" ]");
            if (i == elevatorFloor) {
                str.append("[ ").append(elevator.getOccupancy()).append(" ] y=").append(elevator.getPosition());
            }
            str.append("\n");
        }
        return str.toString();
    }

    /**
     * Returns number of floors
     * @return
     */
    public int getNumFloors() {
        return numFloors;
    }

    /**
     * Returns a floor
     * @param n Floor wanted
     * @return
     */
    public Floor getFloor(int n) {
        assert floors != null;
        assert n >= 0 && n < numFloors;

        return floors[n];
    }

    /**
     * Gets elevator instance
     * @return
     */
    public Elevator getElevator() {
        assert elevator != null;

        return elevator;
    }

    /* **********************************************************************************************************
     * Person methods
     */

    /**
     * Person enters a floor
     * @param p Person must not be inside floor
     * @return returns floor entered
     */
    public Floor enterFloor (Person p) {
        assert p != null;
        assert p.start >= 0 && p.start < numFloors;
        assert !floors[p.start].contains(p);

        floors[p.start].enter(p);
        return floors[p.start];
    }

    /**
     * Person called elevator on a floor
     * Wakes up controller
     */
    public void callElevator() {
        idle.lock();
        try {
            idleCV.broadcast();
        }
        finally {
            idle.unlock();
        }
    }

    /* **********************************************************************************************************
     * ElevatorControl methods
     */

    /**
     * Controller has no pending requests, idles
     */
    public void idle() {
        idle.lock();
        try {
            while (!pendingRequests()) {
                idleCV.await();
            }
        }
        finally {
            idle.unlock();
        }
    }

    /**
     * Checks if there are pending requests from floors and elevator
     * @return
     */
    public boolean pendingRequests() {
        assert floors != null;
        assert elevator != null;

        for (Floor f: floors) {
            if (f.isCalling()) { return true; }
        }

        for (Request r: elevator.getRequests()) {
            if (r != null) { return true; }
        }

        return false;
    }

    /**
     * Checks if a floor requested the elevator
     * @param n The floor to be checked
     * @return
     */
    public boolean isRequesting (int n) {
        assert n >= 0 && n < numFloors;

        return  elevator.getRequests()[n] != null ||
                floors[n].calling != null;
    }

    /**
     * Returns the oldest {@link Request}
     * Gives priority to elevator requests if set on the constructor
     * @return
     */
    public Request getNextDestination() {
        assert floors != null;
        assert elevator != null;

        Request req = null;

        // Get oldest elevator request
        for (Request newReq : elevator.getRequests()) {
            if (newReq == null) { continue; }
            if (req == null) { req = newReq; }
            if (newReq.timestamp < req.timestamp) { req = newReq; }
        }

        if (ELEVATOR_PRIORITY && req != null) return req;

        // Get oldest building request
        for (Floor f : floors) {
            Request newReq = f.calling;
            if (newReq == null) { continue; }
            if (req == null) { req = newReq; }
            if (newReq.timestamp < req.timestamp) { req = newReq; }
        }

        assert req != null;
        return req;
    }

    /**
     * Clears the requests made for this floor coming from the floor and elevator
     * @param n The floor
     */
    public void clearRequests(int n) {
        assert n >= 0 && n < numFloors;
        assert floors != null;
        assert elevator != null;

        floors[n].clearRequest();
        elevator.clearRequest(n);
    }
}
