import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import pt.ua.concurrent.Mutex;
import pt.ua.concurrent.MutexCV;

public class Floor {

    protected List<Person> people;          // List of people in this floor
    protected int floorNum;                 // Floor number

    protected final Mutex floorMtx;
    protected final MutexCV floorCV;

    public Floor(int floorNum) {
        assert floorNum >= 0;

        this.floorNum = floorNum;
        this.people = new LinkedList<>();
        this.floorMtx = new Mutex(true);
        this.floorCV = floorMtx.newCV();
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
        assert people != null;

        return people.size();
    }

    public boolean contains(Person p) {
        return people.contains(p);
    }

}
