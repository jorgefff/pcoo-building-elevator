import static java.lang.System.*;

import pt.ua.concurrent.Mutex;
import pt.ua.concurrent.MutexCV;


public class Building {

    protected Floor[] floors;
    protected Elevator elevator;
    protected Request[] requests;
    protected int numFloors;
    protected int elevatorCap;

    protected final Mutex elevatorRequests;
    protected final MutexCV elevatorIdleCV;
    protected final Mutex[] elevatorDoor;          // Elevator queue for each floor
    protected final MutexCV[] elevatorQueueCV;

    public Building (Floor[] floors, Elevator elevator) {
        assert floors != null;
        assert elevator != null;

        this.floors = floors;
        this.numFloors = floors.length;
        this.elevator = elevator;
        this.elevatorCap = elevator.getCapacity();
        this.requests = new Request[numFloors];

        this.elevatorRequests = new Mutex(true);
        this.elevatorIdleCV = elevatorRequests.newCV();
        this.elevatorDoor = new Mutex[numFloors];
        this.elevatorQueueCV = new MutexCV[numFloors];
        for(int i = 0; i < numFloors; i++)
        {
            elevatorDoor[i] = new Mutex(true);
            elevatorQueueCV[i] = elevatorDoor[i].newCV();
        }
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

        return floors[n];
    }

    public Floor[] getFloors() {
        assert floors != null;

        return floors;
    }

    public Elevator getElevator() {
        assert elevator != null;

        return elevator;
    }

    public int getFloorOccupancy (int floor) {
        assert floor >= 0;
        assert floor < numFloors;
        assert floors != null;
        assert floors[floor] != null;

        return floors[floor].getOccupancy();
    }

    public int[] getAllOccupancies() {
        assert floors != null;

        int[] occupancy = new int[numFloors];

        for (int i = 0; i < numFloors; i++) {
            occupancy[i] = floors[i].getOccupancy();
        }
        return occupancy;
    }

    public double getElevatorPos() {
        assert elevator != null;
        return elevator.getPosition();
    }

    public int getElevatorFloor() {
        assert elevator != null;
        return elevator.getFloorN();
    }

    public void elevatorIdle() {
        elevatorRequests.lock();
        while (!pendingRequests()) {
            elevatorIdleCV.await();
        }
        elevatorRequests.unlock();
    }

    public Floor enterFloor (Person p, int n) {
        assert n >= 0 && n < numFloors;
        assert p != null;
        assert floors != null;
        assert floors[n] != null;
        assert !floors[n].contains(p);

        floors[n].enter(p);
        return floors[n];
    }

    public void callElevator (Person p) {
        assert p != null;
        assert floors[p.start] != null;
        assert floors[p.start].contains(p);;

        elevatorRequests.lock();
        if (requests[p.start] == null) {
            requests[p.start] = new Request(p.start);
            elevatorIdleCV.broadcast();
        }
        elevatorRequests.unlock();
    }

    public Elevator queueForElevator (Person p) {
        assert p != null;
        assert floors[p.start] != null;
        assert floors[p.start].contains(p);
        assert elevatorDoor[p.start].lockIsMine();

        while (!elevator.atFloor(p.start) || !elevator.isMoving()) {
            elevatorQueueCV[p.start].await();
        }

        floors[p.start].exit(p);
        elevator.enter(p);

        return elevator;
    }

    public void openDoors(int n) {
        elevatorDoor[n].lock();
        elevatorQueueCV[n].broadcast();
        elevatorDoor[n].unlock();
    }

    public void resetRequest(int n) {
        assert n >= 0 && n < numFloors;
        assert requests != null;
        assert requests[n] != null;

        requests[n] = null;
    }

    public boolean pendingRequests() {
        for (Request r : requests) {
            if (r != null) { return true; }
        }
        return false;
    }

    public boolean pendingRequest(int n) {
        assert floors != null;

        return requests[n] != null;
    }

    public Request getNextDestination() {
        assert pendingRequests();

        Request req = new Request(numFloors);

        for (int i = 0; i < numFloors; i++) {
            Request newReq = requests[i];
            if (newReq == null) { continue; }
            if ( req.timestamp > newReq.timestamp) {
                req = newReq;
            }
        }
        return req;
    }

    public void grabDoor(Person p) {
        assert p != null;
        assert floors != null;
        assert floors[p.start].contains(p);

        elevatorDoor[p.start].lock();
    }

    public void releaseDoor(Person p) {
        assert p != null;
        assert elevatorDoor[p.start].lockIsMine();

        elevatorDoor[p.start].unlock();
    }

    public void grabDoor(int n) {
        assert n >= 0 && n < numFloors;

        elevatorDoor[n].lock();
    }

    public void releaseDoor(int n) {
        assert n >= 0 && n < numFloors;
        assert elevatorDoor[n].lockIsMine();

        elevatorDoor[n].unlock();
    }
}
