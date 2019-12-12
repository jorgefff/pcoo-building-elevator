
public class Building {

    protected Floor[] floors;
    protected Elevator elevator;

    protected int numFloors;
    protected int elevatorCap;


    public Building (Floor[] floors, Elevator elevator) {
        assert floors != null;
        assert elevator != null;
        this.floors = floors;
        this.numFloors = floors.length;
        this.elevator = elevator;
        this.elevatorCap = elevator.getCapacity();
    }

    public int getNumFloors() {
        return numFloors;
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
        return elevator.getFloor();
    }

    public Floor enterFloor (Person p, int n) {
        assert n > 0 && n < numFloors;
        assert p != null;
        assert floors != null;
        assert floors[n] != null;

        floors[n].enter(p);
        return floors[n];
    }
}
