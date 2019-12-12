import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import pt.ua.concurrent.Mutex;
import pt.ua.concurrent.MutexCV;

public class Floor {

    protected List<Person> people;      // List of people in this floor
    protected Queue<Person> queue;      // Queue to enter the elevator

    protected int floorNum;             // Floor number

    protected boolean buttonPressed;    // Button to call elevator

    protected final Mutex floorMtx;

    public Floor(int floorNum) {
        assert floorNum >= 0;

        this.floorNum = floorNum;
        buttonPressed = false;
        people = new LinkedList<>();
        queue = new LinkedList<>();

        floorMtx = new Mutex(true);
    }

    public void enter (Person p) {
        assert p != null;
        assert people != null;
        assert !people.contains(p);

        floorMtx.lock();
        people.add(p);
        floorMtx.unlock();

        assert people.contains(p);
    }

    public void exit (Person p) {
        assert p != null;
        assert people != null;
        assert people.contains(p);

        floorMtx.lock();
        people.remove(p);
        floorMtx.unlock();

        assert !people.contains(p);
    }


    public int getOccupancy() {
        return people.size();
    }


    public void pressElevatorButton (Person p) {
        assert p != null;
        assert people != null;
        assert people.contains(p);

        buttonPressed = true;
    }

    public void enterElevatorQueue (Person p) {
        assert p != null;
        assert people != null;
        assert people.contains(p);
        assert !queue.contains(p);

        queue.add(p);

        assert !queue.contains(p);
    }

}
