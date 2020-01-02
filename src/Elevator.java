import pt.ua.concurrent.Mutex;
import pt.ua.concurrent.MutexCV;

import java.util.LinkedList;
import java.util.List;

public class Elevator {

    protected final static int UNIT = 10;               // To correct double conversions
    protected final static int MOVE_UNIT = UNIT/10;     // 10% of the UNIT

    protected Building building;
    protected int capacity;                 // How many fit inside
    protected int pos;                      // Elevator position
    protected int floorN;                   // Current floor
    protected int numFloors;                // Total num of floors
    protected boolean moving;               // Moving state
    protected boolean startedMovement;
    protected List<Person> people;          // People currently inside

    protected Mutex peopleMtx;
    protected MutexCV peopleCV;
    protected Request[] requests;
    protected Mutex requestsLock;

    public Elevator (Building building, int capacity, int numFloors) {
        assert capacity > 0;

        this.startedMovement = false;
        this.building = building;
        this.capacity = capacity;
        this.pos = 0;
        this.floorN = 0;
        this.moving = false;
        this.people = new LinkedList<>();
        this.numFloors = numFloors;
        this.peopleMtx = new Mutex(true);
        this.peopleCV = peopleMtx.newCV();
        this.requests = new Request[numFloors];
        this.requestsLock = new Mutex(true);
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

    public int getMovementUnit() {
        return UNIT;
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
        return people.size();
    }

    public boolean isMoving() {
        return moving;
    }

    public boolean isAtAFloor() {
        return floorN * UNIT == pos;
    }

    public boolean isAtFloor(int n) {
        return floorN == n && isAtAFloor();
    }

    public void startMoving() {
        peopleMtx.lock();
        moving = true;
        startedMovement = true;
        peopleMtx.unlock();
    }

    public void stopMoving() {
        moving = false;
        peopleMtx.lock();
        peopleCV.broadcast();
        peopleMtx.unlock();
    }

    public void move (int direction) {
        assert moving;
        assert direction == -1 || direction == 1;
        assert (MOVE_UNIT * direction) + pos >= 0;
        assert (MOVE_UNIT * direction) + pos <= (numFloors-1) * UNIT;

        startedMovement = false;
        pos += (MOVE_UNIT * direction);
        floorN = pos / UNIT;
    }

    public boolean isFull() {
        return people.size() == capacity;
    }

    public void enter (Person p) {
        assert p != null;
        assert !moving;
        assert isAtFloor(p.start);
        assert !people.contains(p);
        assert people.size() < capacity;

        peopleMtx.lock();
        people.add(p);
        pressButton(p);
        peopleMtx.unlock();

        assert people.contains(p);
    }

    public void exit (Person p) {
        assert p != null;
        assert people.contains(p);

        peopleMtx.lock();
        people.add(p);
        peopleMtx.unlock();

        assert !people.contains(p);
    }

    public void waitForFloor (Person p) {
        assert p != null;
        assert  people.contains(p);

        peopleMtx.lock();
        while (!isAtFloor(p.goal) || isMoving()) {
            pressButton(p);
            peopleCV.await();
        }
        people.remove(p);
        peopleMtx.unlock();
    }

    public void pressButton (Person p) {
        assert p != null;
        assert people != null;
        assert people.contains(p);

        requestsLock.lock();
        if (requests[p.goal] == null) {
            requests[p.goal] = new Request(p.goal, "elevator");
            building.callElevator();
        }
        requestsLock.unlock();
    }

    public  void clearRequest(int n) {
        requestsLock.lock();
        requests[n] = null;
        requestsLock.unlock();
    }

    public Request[] getRequests() {
        assert requests != null;

        return requests;
    }

    public boolean startedMovement() {
        return startedMovement;
    }

}
