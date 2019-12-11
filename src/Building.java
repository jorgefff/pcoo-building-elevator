
public class Building {

    protected static Building instance = null;

    protected Floor[] floors;
    protected Elevator elevator;

    protected int numFloors;
    protected int elevatorCap;

    private Building (int numFloors, int elevatorCapacity) {
        assert numFloors > 0;
        assert elevatorCapacity > 0;

        floors = new Floor[numFloors];
        for (int i = 0; i < numFloors; i++) {
            floors[i] = new Floor(i);
        }

        elevator = new Elevator (elevatorCapacity);

        this.numFloors = numFloors;
        this.elevatorCap = elevatorCapacity;
    }

    public static Building initialize (int numFloors, int elevatorCapacity) {
        assert instance == null;
        instance = new Building (numFloors, elevatorCapacity);
        return instance;
    }

    public static Building getInstance() {
        assert instance != null;
        return instance;
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

    public Floor enterFloor (Person p, int n) {
        assert n > 0 && n < numFloors;
        assert p != null;
        assert floors != null;
        assert floors[n] != null;

        floors[n].enter(p);
        return floors[n];
    }
}
