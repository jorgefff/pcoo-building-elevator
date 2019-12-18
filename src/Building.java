import pt.ua.concurrent.Mutex;
import pt.ua.concurrent.MutexCV;


public class Building {

    protected Floor[] floors;
    protected Elevator elevator;
    protected int numFloors;
    protected int elevatorCap;
    protected boolean newRequests;

    protected final Mutex elevatorIdle;
    protected final MutexCV elevatorIdleCV;
    protected final Mutex[] elevatorQueue;          // Elevator queue for each floor
    protected final MutexCV[] elevatorQueueCV;

    public Building (Floor[] floors, Elevator elevator) {
        assert floors != null;
        assert elevator != null;

        this.floors = floors;
        this.numFloors = floors.length;
        this.elevator = elevator;
        this.elevatorCap = elevator.getCapacity();
        this.newRequests = false;

        this.elevatorIdle = new Mutex(true);
        this.elevatorIdleCV = elevatorIdle.newCV();
        this.elevatorQueue = new Mutex[numFloors];
        this.elevatorQueueCV = new MutexCV[numFloors];
        for(int i = 0; i < numFloors; i++)
        {
            elevatorQueue[i] = new Mutex(true);
            elevatorQueueCV[i] = elevatorQueue[i].newCV();
        }
    }

    @Override
    public String toString() {
        String str = "";
        int elevatorFloor = elevator.getFloorN();
        for (int i = numFloors-1; i >= 0; i--) {
            str += "[ " + floors[i].getOccupancy() + " ]";
            if (i == elevatorFloor) {
                str += "[ " + elevator.getOccupancy() + " ]";
            }
            str += "\n";
        }
        return str;
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

    public int getFloorOccupancy(int floor) {
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
        elevatorIdle.lock();
        elevatorIdleCV.await();
        elevatorIdle.unlock();
    }

    public Floor enterFloor (Person p, int n) {
        assert n >= 0 && n < numFloors;
        assert p != null;
        assert floors != null;
        assert floors[n] != null;

        floors[n].enter(p);
        return floors[n];
    }

    public void queueForElevator (Person p) {
        assert p != null;
        assert floors[p.start] != null;
        assert floors[p.start].getPeople().contains(p);



    }


}
