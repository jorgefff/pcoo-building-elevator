import java.util.LinkedList;
import java.util.List;

public class Floor {

    protected List<Person> people;

    public Floor() {
        people = new LinkedList<>();
    }

    public void enter (Person p) {
        assert p != null;
    }

    public void exit (Person p) {
        assert p != null;
        assert people.size() > 0;
    }

    public int getPersonCounter() {
        return people.size();
    }
}
