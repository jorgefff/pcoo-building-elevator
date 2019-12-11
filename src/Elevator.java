
public class Elevator {

    protected int capacity;
    protected boolean doorsOpen;
    protected double pos;
    protected int floor;


    public Elevator (int capacity) {
        assert capacity > 0;

        this.capacity = capacity;
        this.doorsOpen = false;
        this.pos = 0;
        this.floor = 0;
    }


}
