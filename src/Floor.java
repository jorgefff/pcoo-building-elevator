import java.util.LinkedList;
import java.util.List;

public class Floor {

    protected List<Person> people;

    public Floor() {
        people = new LinkedList<>();
    }

    public void enter (Person p) {
        assert p != null;
        assert people != null;
        assert !people.contains(p);

        people.add(p);
    }

    public void exit (Person p) {
        assert p != null;
        assert people != null;
        assert people.contains(p);

        people.remove(p);
    }


    public int getPeopleCounter() {
        return people.size();
    }


    public void pressElevatorButton (Person p) {
        assert p != null;
        assert people != null;
        assert people.contains(p);

    }


    public void enterElevatorQueue(Person p) {
        assert p != null;
        assert people != null;
        assert people.contains(p);

    }
}
