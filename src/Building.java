import static java.lang.System.*;

import pt.ua.concurrent.Mutex;
import pt.ua.concurrent.MutexCV;


public class Building {

    protected int numFloors;
    protected Floor[] floors;
    protected Elevator elevator;

    protected Mutex idle;
    protected MutexCV idleCV;

    public Building (int numFloors, Elevator elevator) {
        assert numFloors > 0;
        assert elevator != null;

        this.numFloors = numFloors;
        this.elevator = elevator;

        this.idle = new Mutex(true);
        this.idleCV = idle.newCV();
    }

    /* **********************************************************************************************************
     * Common methods
     */
    public void generateFloors() {
        assert floors == null;
        floors = new Floor[numFloors];
        for (int i = 0; i < numFloors; i++) {
            floors[i] = new Floor(i, this);
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

    public Elevator getElevator() {
        assert elevator != null;

        return elevator;
    }

    /* **********************************************************************************************************
     * Person methods
     */

    public Floor enterFloor (Person p, int n) {
        assert n >= 0 && n < numFloors;
        assert p != null;
        assert !floors[n].contains(p);

        floors[n].enter(p);
        return floors[n];
    }

    public void callElevator() {
        idle.lock();
        idleCV.broadcast();
        idle.unlock();
    }

    /* **********************************************************************************************************
     * ElevatorControl methods
     */

    public void idle() {
        idle.lock();
        while (!pendingRequests()) {
            idleCV.await();
        }
        idle.unlock();
    }

    public boolean pendingRequests() {
        assert floors != null;
        for(Floor f: floors) {
            if (f.isCalling()) return true;
        }
        return false;
    }

}
