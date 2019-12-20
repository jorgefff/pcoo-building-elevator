import static java.lang.System.*;
import pt.ua.concurrent.Mutex;
import pt.ua.concurrent.MutexCV;

import java.util.LinkedList;
import java.util.List;

public class Elevator {

    protected final static int UNIT = 10;       // For double / float conversions
    protected final static int MOVE_UNIT = 1;   // 10% of the UNIT
    protected int capacity;
    protected boolean doorsAreOpen;
    protected int pos;
    protected int floorN;
    protected int numFloors;
    protected boolean moving;
    protected List<Person> peopleInside;
    protected Mutex peopleLock;
    protected Request[] requests;
    protected Mutex requestsLock;
    protected MutexCV idleController;

    public Elevator (int capacity, int numFloors) {
        assert capacity > 0;

        this.capacity = capacity;
        this.doorsAreOpen = false;
        this.pos = 0;
        this.floorN = 0;
        this.moving = false;
        this.peopleInside = new LinkedList<>();
        this.numFloors = numFloors;
        this.peopleLock = new Mutex(true);
        this.requests = new Request[numFloors];
        this.requestsLock = new Mutex(true);
        this.idleController = requestsLock.newCV();
    }

    /* **********************************************************************************************************
     * Common methods
     */

    /* **********************************************************************************************************
     * Person methods
     */

    /* **********************************************************************************************************
     * ElevatorControl methods
     */

    @Override
    public String toString() {
        return "ELEVATOR-POS( "+pos+" )-FLR( "+ floorN +" )";
    }

    public double getPosition() {
        return pos;
    }

    public int getFloorN() {
        return floorN;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getOccupancy() {
        return peopleInside.size();
    }

    public boolean isMoving() {
        return moving;
    }

    public boolean doorsAreOpen() {
        return doorsAreOpen;
    }

    public boolean atAFloor() {
        return floorN * UNIT == pos;
    }

    public boolean atFloor(int n) {
        return floorN == n && atAFloor();
    }
    public void startMoving() {
        moving = true;
    }

    public void stopMoving() {
        moving = false;
    }

    public void openDoors() {
        assert !moving;
        doorsAreOpen = true;
    }

    public void move (int direction) {
        assert moving;
        assert direction == -1 || direction == 1;
        assert (MOVE_UNIT * direction) + pos >= 0;
        assert (MOVE_UNIT * direction) + pos <= (numFloors-1) * UNIT;

        pos += (MOVE_UNIT * direction);
        floorN = pos / UNIT;
    }

    public boolean isFull() {
        return peopleInside.size() == capacity;
    }

    public void enter (Person p) {
        assert p != null;
        assert !moving;
        assert floorN == p.start;
        assert !peopleInside.contains(p);
        assert peopleInside.size() < capacity;

        peopleLock.lock();
        peopleInside.add(p);
        peopleLock.unlock();
    }

    public void exit (Person p) {
        assert p != null;
        assert !moving;
        assert atAFloor();
        assert peopleInside.contains(p);
        assert floorN == p.goal;

        peopleLock.lock();
        peopleInside.remove(p);
        peopleLock.unlock();
    }

    public void waitForMyFloor(Person p) {
        assert p != null;
        assert peopleInside.contains(p);

        //TODO
    }

    public void newRequest(Request r) {
        assert r != null;
        assert r.floor >= 0 && r.floor < numFloors;
        assert requests != null;

        requestsLock.lock();
        if (requests[r.floor] == null) {
            requests[r.floor] = r;
            idleController.broadcast();
        }
        requestsLock.unlock();
    }

    public boolean isFloorRequesting(int n) {
        assert requests != null;

        return requests[n] != null;
    }

    public boolean pendingRequests() {
        assert requests != null;
        for (Request req : requests) {
            if (req != null) { return true; }
        }
        return false;
    }

    public boolean pendingRequest(int n) {
        assert n >= 0 && n < numFloors;
        assert requests != null;

        return requests[n] != null;
    }

    public Request getNextDestination() {
        assert pendingRequests();

        Request req = new Request(numFloors, "elevator");

        for (int i = 0; i < numFloors; i++) {
            Request newReq = requests[i];
            if (newReq == null) { continue; }
            if ( req.timestamp > newReq.timestamp) {
                req = newReq;
            }
        }
        return req;
    }

    public void clearRequest(int n) {
        assert n >= 0 && n < numFloors;
        assert requests != null;

        requests[n] = null;
    }

    public void idleController() {
        requestsLock.lock();
        while (!pendingRequests()) {
            idleController.await();
        }
        requestsLock.unlock();
    }
}
