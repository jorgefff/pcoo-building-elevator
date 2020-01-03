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

    public Floor(int floorNum, Building building) {
        assert floorNum >= 0;

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

    public int getFloorNum() {
        return floorNum;
    }

    public void enter (Person p) {
        assert p != null;
        assert peopleIn != null;
        assert !peopleIn.contains(p);

        peopleMtx.lock();
        peopleIn.add(p);
        Graphical.getInstance().updateFloor(this);
        peopleMtx.unlock();

        assert peopleIn.contains(p);
    }

    public void callElevator (Person p) {
        assert p != null;
        assert peopleIn != null;
        assert peopleIn.contains(p);

        buttonMtx.lock();
        if (calling == null) {
            calling = new Request(floorNum, "building");
        }
        building.callElevator();
        buttonMtx.unlock();
    }

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

    public void grabElevatorDoor() {
        assert !elevatorDoorMtx.lockIsMine();

        elevatorDoorMtx.lock();
    }

    public void releaseElevatorDoor() {
        assert elevatorDoorMtx.lockIsMine();

        elevatorDoorMtx.unlock();
    }

    public void exit (Person p) {
        assert p != null;
        assert peopleIn != null;
        assert peopleIn.contains(p);

        peopleMtx.lock();
        peopleIn.remove(p);
        Graphical.getInstance().updateFloor(this);
        peopleMtx.unlock();

        assert !peopleIn.contains(p);
    }

    public void arrive (Person p) {
        assert p != null;
        assert peopleOut != null;
        assert !peopleOut.contains(p);

        arriveMtx.lock();
        peopleOut.add(p);
        Graphical.getInstance().updateArrival(this);
        arriveMtx.unlock();

        assert peopleOut.contains(p);
    }

    public int getArrivedCount() {
        assert peopleOut != null;

        return peopleOut.size();
    }

    public int getOccupancy() {
        assert peopleIn != null;

        return peopleIn.size();
    }

    public boolean contains(Person p) {
        return peopleIn.contains(p);
    }

    public boolean isCalling() {
        return calling != null;
    }

    public void openDoors() {
        elevatorDoorMtx.lock();
        waitingForElevator.broadcast();
        elevatorDoorMtx.unlock();
    }

    public void clearRequest() {
        buttonMtx.lock();
        calling = null;
        buttonMtx.unlock();
    }
}
