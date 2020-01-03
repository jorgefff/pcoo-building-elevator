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

    public boolean isFull() {
        return people.size() == capacity;
    }

    /* **********************************************************************************************************
     * Person methods
     */

    public void enter (Person p) {
        assert p != null;
        assert !moving;
        assert isAtFloor(p.start);
        assert !people.contains(p);
        assert people.size() < capacity;

        peopleMtx.lock();
        try {
            people.add(p);
            Graphical.getInstance().updateElevatorPpl(this);
            pressButton(p);
        }
        finally {
            peopleMtx.unlock();
        }

        assert people.contains(p);
    }

    public void waitForFloor (Person p) {
        assert p != null;
        assert  people.contains(p);

        peopleMtx.lock();
        try {
            while (!isAtFloor(p.goal) || isMoving()) {
                pressButton(p);
                peopleCV.await();
            }
            people.remove(p);
            Graphical.getInstance().updateElevatorPpl(this);
        }
        finally {
            peopleMtx.unlock();
        }
    }

    public void pressButton (Person p) {
        assert p != null;
        assert people != null;
        assert people.contains(p);

        requestsLock.lock();
        try {
            if (requests[p.goal] == null) {
                requests[p.goal] = new Request(p.goal, "elevator");
                building.callElevator();
            }
        }
        finally {
            requestsLock.unlock();
        }
    }

    /* **********************************************************************************************************
     * ElevatorControl methods
     */

    public void startMoving() {
        peopleMtx.lock();
        try {
            moving = true;
            startedMovement = true;
        }
        finally {
            peopleMtx.unlock();
        }
    }

    public void stopMoving() {
        moving = false;
        peopleMtx.lock();
        try {
            peopleCV.broadcast();
        }
        finally {
            peopleMtx.unlock();
        }
    }

    public void move (int direction) {
        assert moving;
        assert direction == -1 || direction == 1;
        assert (MOVE_UNIT * direction) + pos >= 0;
        assert (MOVE_UNIT * direction) + pos <= (numFloors-1) * UNIT;

        startedMovement = false;
        int oldPos = pos;
        pos += (MOVE_UNIT * direction);
        Graphical.getInstance().updateElevatorPos(oldPos, pos);
        floorN = pos / UNIT;
    }

    public  void clearRequest(int n) {
        requestsLock.lock();
        try {
            requests[n] = null;
        }
        finally {
            requestsLock.unlock();
        }
    }

    public Request[] getRequests() {
        assert requests != null;

        return requests;
    }

    public boolean startedMovement() {
        return startedMovement;
    }

}
