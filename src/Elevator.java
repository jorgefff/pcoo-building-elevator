
public class Elevator {

    protected final static double MOVE_UNIT = 0.1;
    protected int capacity;
    protected boolean doorsAreOpen;
    protected double pos;
    protected int floor;
    protected boolean moving;

    public Elevator (int capacity) {
        assert capacity > 0;

        this.capacity = capacity;
        this.doorsAreOpen = false;
        this.pos = 0;
        this.floor = 0;
        this.moving = false;
    }

    @Override
    public String toString() {
        return "ELEVATOR-POS( "+pos+" )-FLR( "+floor+" )";
    }

    public double getPosition() {
        return pos;
    }

    public int getFloor() {
        return floor;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean doorsAreOpen() {
        return doorsAreOpen;
    }

    public void startMoving() {
        assert !moving;
        moving = true;
    }

    public void stopMoving() {
        assert moving;
        moving = false;
    }

    public void goUp() {
        pos = pos + MOVE_UNIT;
        floor = (int) Math.floor(pos);
    }

    public void goDown() {
        pos = pos - MOVE_UNIT;
        floor = (int) Math.floor(pos);
    }
}
