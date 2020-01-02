import pt.ua.concurrent.Mutex;
import pt.ua.concurrent.MutexCV;

public class Building {
    protected boolean ELEVATOR_PRIORITY;
    protected int numFloors;
    protected Floor[] floors;
    protected Elevator elevator;

    protected Mutex idle;
    protected MutexCV idleCV;

    public Building() {
        this.ELEVATOR_PRIORITY = false;
        this.idle = new Mutex(true);
        this.idleCV = idle.newCV();
    }
    public Building(boolean elePriority) {
        this.ELEVATOR_PRIORITY = elePriority;
        this.idle = new Mutex(true);
        this.idleCV = idle.newCV();
    }

    /* **********************************************************************************************************
     * Common methods
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

    public void generateElevator(int capacity) {
        assert elevator == null : "Elevator already created";
        assert floors != null : "Floors must be created first";
        assert capacity > 0 : "Bad capacity";

        elevator = new Elevator (this, capacity, numFloors);
    }

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

    public int getNumFloors() {
        return numFloors;
    }

    public Floor getFloor(int n) {
        assert floors != null;
        assert n >= 0 && n < numFloors;

        return floors[n];
    }

    public Elevator getElevator() {
        assert elevator != null;

        return elevator;
    }

    /* **********************************************************************************************************
     * Person methods
     */

    public Floor enterFloor (Person p, int n) {
        assert n >= 0 && n < numFloors;
        assert p != null;
        assert !floors[n].contains(p);

        floors[n].enter(p);
        return floors[n];
    }

    public void callElevator() {
        idle.lock();
        idleCV.broadcast();
        idle.unlock();
    }

    /* **********************************************************************************************************
     * ElevatorControl methods
     */

    public void idle() {
        idle.lock();
        while (!pendingRequests()) {
            idleCV.await();
        }
        idle.unlock();
    }

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

    public boolean isRequesting (int n) {
        assert n >= 0 && n < numFloors;

        return  elevator.getRequests()[n] != null ||
                floors[n].calling != null;
    }

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

    public void clearRequests(int n) {
        assert n >= 0 && n < numFloors;
        assert floors != null;
        assert elevator != null;

        floors[n].clearRequest();
        elevator.clearRequest(n);
    }
}
