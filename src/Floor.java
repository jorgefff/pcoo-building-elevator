import java.util.LinkedList;
import java.util.List;

import pt.ua.concurrent.Mutex;
import pt.ua.concurrent.MutexCV;

public class Floor {

    protected Building building;

    protected int floorNum;                 // Floor number
    protected Request calling;              // Floor called elevator
    protected List<Person> people;          // List of people in this floor

    protected final Mutex peopleMtx;
    protected final Mutex elevatorDoorMtx;
    protected final MutexCV waitingForElevator;
    protected final Mutex buttonMtx;

    public Floor(int floorNum, Building building) {
        assert floorNum >= 0;

        this.building = building;
        this.floorNum = floorNum;
        this.calling = null;
        this.people = new LinkedList<>();
        this.peopleMtx = new Mutex(true);
        this.elevatorDoorMtx = new Mutex(true);
        this.waitingForElevator = elevatorDoorMtx.newCV();
        this.buttonMtx = new Mutex(true);
    }

    public int getFloorNum() {
        return floorNum;
    }

    public void enter (Person p) {
        assert p != null;
        assert people != null;
        assert !people.contains(p);

        peopleMtx.lock();
        people.add(p);
        Graphical.getInstance().updateFloor(this);
        peopleMtx.unlock();

        assert people.contains(p);
    }

    public void callElevator (Person p) {
        assert p != null;
        assert people != null;
        assert people.contains(p);

        buttonMtx.lock();
        if (calling == null) {
            calling = new Request(floorNum, "building");
        }
        building.callElevator();
        buttonMtx.unlock();
    }

    public Elevator queueForElevator (Person p) {
        assert p != null;
        assert people != null;
        assert people.contains(p);

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
        assert people != null;
        assert people.contains(p);

        peopleMtx.lock();
        people.remove(p);
        Graphical.getInstance().updateFloor(this);
        peopleMtx.unlock();

        assert !people.contains(p);
    }

    public int getOccupancy() {
        assert people != null;

        return people.size();
    }

    public boolean contains(Person p) {
        return people.contains(p);
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
