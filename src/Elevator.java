import pt.ua.concurrent.Mutex;
import pt.ua.concurrent.MutexCV;

import java.util.LinkedList;
import java.util.List;

public class Elevator implements Elevator_Prsn, Elevator_Ctrl {

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

    /**
     * Returns current floor number
     * @return
     */
    public int getFloorN() {
        return floorN;
    }

    /**
     * Returns maximum capacity
     * @return
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Returns how many people are currently inside
     * @return
     */
    public int getOccupancy() {
        return people.size();
    }

    /**
     * Returns true if elevator is currently moving
     * @return
     */
    public boolean isMoving() {
        return moving;
    }

    /**
     * Returns true if elevator is at a floor
     * Returns false if elevator is between floors
     * @return
     */
    public boolean isAtAFloor() {
        return floorN * UNIT == pos;
    }

    /**
     * Returns true if elevator is at the floor
     * Returns false if elevator is between floors or at a different floor
     * @param n
     * @return
     */
    public boolean isAtFloor(int n) {
        return floorN == n && isAtAFloor();
    }



    /* **********************************************************************************************************
     * Person methods
     */

    /**
     * Returns true if elevator is at maximum capacity
     * @return
     */
    public boolean isFull() {
        return people.size() == capacity;
    }

    /**
     * Person enters the elevator
     * @param p The person entering must not already be inside
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

    /**
     * Person waits inside until the elevator reaches the desired floor
     * @param p The person must already be inside
     */
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

    /**
     * Person presses the button to the desired floor
     * @param p The person must be inside the elevator
     */
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

    /**
     * Returns how fast the elevator moves, used for graphical purposes
     * @return
     */
    public int getMovementUnit() {
        return UNIT;
    }

    /**
     * Returns real position
     * @return
     */
    public double getPosition() {
        return pos;
    }

    /**
     * Start elevator movement.
     * People can no longer enter or leave.
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

    /**
     * Stop elevator movement.
     * People can now enter or leave.
     */
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

    /**
     * Move elevator up or down
     * @param direction Elevator goes up if direction is positive, down if it is negative
     */
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

    /**
     * Clears requests made for a certain floor from inside the elevator
     * @param n
     */
    public  void clearRequest(int n) {
        requestsLock.lock();
        try {
            requests[n] = null;
        }
        finally {
            requestsLock.unlock();
        }
    }

    /**
     * Gets the list of requests
     * @return
     */
    public Request[] getRequests() {
        assert requests != null;

        return requests;
    }

    /**
     * Returns true if elevator just started moving.
     * Used to prevent being stuck in a floor
     * @return
     */
    public boolean startedMovement() {
        return startedMovement;
    }

}
