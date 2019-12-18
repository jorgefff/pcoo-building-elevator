import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Elevator {

    protected final static double MOVE_UNIT = 0.1;
    protected int capacity;
    protected boolean doorsAreOpen;
    protected double pos;
    protected int floor;
    protected int numFloors;
    protected boolean moving;
    protected List<Person> peopleInside;
    protected Map<Integer, Boolean> buttonsPressed;

    public Elevator (int capacity, int numFloors) {
        assert capacity > 0;

        this.capacity = capacity;
        this.doorsAreOpen = false;
        this.pos = 0;
        this.floor = 0;
        this.moving = false;
        this.peopleInside = new LinkedList<>();
        this.numFloors = numFloors;
        this.buttonsPressed = new HashMap<>();
    }

    @Override
    public String toString() {
        return "ELEVATOR-POS( "+pos+" )-FLR( "+floor+" )";
    }

    public double getPosition() {
        return pos;
    }

    public int getFloorN() {
        return floor;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getOccupancy() {
        return peopleInside.size();
    }

    public boolean isMoving() {
        return moving;
    }

    public boolean doorsAreOpen() {
        return doorsAreOpen;
    }

    public boolean atAFloor() {
        return (double) floor == pos;
    }

    public void startMoving() {
        assert !moving;
        assert !doorsAreOpen;
        moving = true;
    }

    public void stopMoving() {
        assert moving;
        moving = false;
    }

    public void openDoors() {
        assert !moving;
        doorsAreOpen = true;
    }

    public void goUp() {
        assert moving;
        pos = pos + MOVE_UNIT;
        floor = (int) Math.floor(pos);
    }

    public void goDown() {
        assert moving;
        pos = pos - MOVE_UNIT;
        floor = (int) Math.floor(pos);
    }

    public void enter (Person p) {
        assert p != null;
        assert !moving;
        assert doorsAreOpen;
        assert !peopleInside.contains(p);
        assert peopleInside.size() < capacity;

        peopleInside.add(p);
        buttonsPressed.put(p.goal, true);

        // Lock
        // while(not my floor) wait
        // exit
        // unlock
    }


}
