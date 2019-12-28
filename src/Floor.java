import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import pt.ua.concurrent.Mutex;
import pt.ua.concurrent.MutexCV;

public class Floor {

    protected Building building;
    protected Elevator elevator;

    protected int floorNum;                 // Floor number
    protected boolean calling;              // Floor called elevator
    protected List<Person> people;          // List of people in this floor

    protected final Mutex peopleMtx;
    protected final Mutex elevatorDoorMtx;
    protected final MutexCV waitingForElevator;

    public Floor(int floorNum, Building building) {
        assert floorNum >= 0;

        this.building = building;
        this.elevator = building.getElevator();
        this.floorNum = floorNum;
        this.calling = false;
        this.people = new LinkedList<>();
        this.peopleMtx = new Mutex(true);
        this.elevatorDoorMtx = new Mutex(true);
        this.waitingForElevator = elevatorDoorMtx.newCV();
    }

    public void enter (Person p) {
        assert p != null;
        assert people != null;
        assert !people.contains(p);

        peopleMtx.lock();
        people.add(p);
        peopleMtx.unlock();

        assert people.contains(p);
    }

    public void callElevator (Person p) {
        assert p != null;
        assert people != null;
        assert people.contains(p);

        building.callElevator();
    }

    public Elevator queueForElevator (Person p) {
        assert p != null;
        assert people != null;
        assert people.contains(p);


        elevatorDoorMtx.lock();
        while (elevator.isFull() || !elevator.isAtFloor(floorNum) || elevator.isMoving()) {
            //TODO: press button again?
            waitingForElevator.await();
        }
        return elevator;
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
        return calling;
    }

}
