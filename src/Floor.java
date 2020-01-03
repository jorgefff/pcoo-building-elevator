import java.util.LinkedList;
import java.util.List;

import pt.ua.concurrent.Mutex;
import pt.ua.concurrent.MutexCV;

public class Floor {

    protected Building building;

    protected int floorNum;                 // Floor number
    protected Request calling;              // Floor called elevator
    protected List<Person> peopleIn;        // List of people waiting for elevator
    protected List<Person> peopleOut;       // List of people that arrived

    protected final Mutex peopleMtx;
    protected final Mutex arriveMtx;
    protected final Mutex elevatorDoorMtx;
    protected final MutexCV waitingForElevator;
    protected final Mutex buttonMtx;

    /**
     * Constructor
     * @param floorNum This floor's number
     * @param building building instance this floor belongs to
     */
    public Floor(int floorNum, Building building) {
        assert floorNum >= 0;
        assert building != null;
        assert floorNum < building.getNumFloors();

        this.building = building;
        this.floorNum = floorNum;
        this.calling = null;
        this.peopleIn = new LinkedList<>();
        this.peopleOut = new LinkedList<>();
        this.peopleMtx = new Mutex(true);
        this.elevatorDoorMtx = new Mutex(true);
        this.waitingForElevator = elevatorDoorMtx.newCV();
        this.buttonMtx = new Mutex(true);
        this.arriveMtx = new Mutex(true);
    }

    /**
     * Get this floor's number
     * @return This floor's number
     */
    public int getFloorNum() {
        return floorNum;
    }

    /**
     * Person enters the floor to queue for the elevator
     * @param p person must not be inside already
     */
    public void enter (Person p) {
        assert p != null;
        assert peopleIn != null;
        assert !peopleIn.contains(p);

        peopleMtx.lock();
        try {
            peopleIn.add(p);
            Graphical.getInstance().updateFloor(this);
        }
        finally {
            peopleMtx.unlock();
        }

        assert peopleIn.contains(p);
    }

    /**
     * Person calls the elevator to its floor
     * @param p Person needs to be in the floor
     */
    public void callElevator (Person p) {
        assert p != null;
        assert peopleIn != null;
        assert peopleIn.contains(p);

        buttonMtx.lock();
        try {
            if (calling == null) {
                calling = new Request(floorNum, "building");
            }
            building.callElevator();
        }
        finally {
            buttonMtx.unlock();
        }
    }

    /**
     * Person queues for the elevator
     * @param p Person needs to be in the floor
     * @return Returns elevator instance and holds the door (needs to be released externally)
     */
    public Elevator queueForElevator (Person p) {
        assert p != null;
        assert peopleIn != null;
        assert peopleIn.contains(p);

        elevatorDoorMtx.lock();
        Elevator ele = building.getElevator();
        while (!ele.isAtFloor(floorNum) || ele.isMoving() || ele.isFull()) {
            callElevator(p);
            waitingForElevator.await();
        }
        return ele;
    }

    /**
     * Holds elevator door mutex
     * Used to make transitions between floor and elevator
     */
    public void grabElevatorDoor() {
        assert !elevatorDoorMtx.lockIsMine();

        elevatorDoorMtx.lock();
    }

    /**
     * Releases elevator door mutex
     * Used to make transitions between floor and elevator
     */
    public void releaseElevatorDoor() {
        assert elevatorDoorMtx.lockIsMine();

        elevatorDoorMtx.unlock();
    }

    /**
     * Person exits its starting floor
     * @param p Person must be inside floor
     */
    public void exit (Person p) {
        assert p != null;
        assert peopleIn != null;
        assert peopleIn.contains(p);

        peopleMtx.lock();
        try {
            peopleIn.remove(p);
            Graphical.getInstance().updateFloor(this);
        }
        finally {
            peopleMtx.unlock();
        }

        assert !peopleIn.contains(p);
    }

    /**
     * Person arrived at its goal floor
     * @param p Person must not be in arrival list
     */
    public void arrive (Person p) {
        assert p != null;
        assert peopleOut != null;
        assert !peopleOut.contains(p);

        arriveMtx.lock();
        try {
            peopleOut.add(p);
            Graphical.getInstance().updateArrival(this);
        }
        finally {
            arriveMtx.unlock();
        }

        assert peopleOut.contains(p);
    }

    /**
     * Number of people that arrived at this floor (as a goal)
     * @return
     */
    public int getArrivedCount() {
        assert peopleOut != null;

        return peopleOut.size();
    }

    /**
     * People currently in the floor waiting to enter elevator
     * @return
     */
    public int getOccupancy() {
        assert peopleIn != null;

        return peopleIn.size();
    }

    /**
     * Returns true if the person is in this floor
     * @param p The person being checked
     * @return
     */
    public boolean contains(Person p) {
        return peopleIn.contains(p);
    }

    /**
     * Returns true if someone in this floor called the elevator
     * @return
     */
    public boolean isCalling() {
        return calling != null;
    }

    /**
     * Announces to every person waiting for the elevator that it arrived the floor
     */
    public void openDoors() {
        elevatorDoorMtx.lock();
        try {
            waitingForElevator.broadcast();
        }
        finally {
            elevatorDoorMtx.unlock();
        }
    }

    /**
     * Clears the requests for the elevator coming from this floor
     */
    public void clearRequest() {
        buttonMtx.lock();
        try {
            calling = null;
        }
        finally {
            buttonMtx.unlock();
        }
    }
}
