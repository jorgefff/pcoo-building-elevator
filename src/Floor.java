import java.util.LinkedList;
import java.util.List;

public class Floor {

    protected Building building;
    protected List<Person> people;
    protected int floor;
    protected boolean buttonPressed;

    public Floor(int floor) {
        assert floor >= 0;

        this.floor = floor;
        buttonPressed = false;
        people = new LinkedList<>();
        building = Building.getInstance();
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


    public int getOccupancy() {
        return people.size();
    }


    public void pressElevatorButton (Person p) {
        assert p != null;
        assert people != null;
        assert people.contains(p);

        if (!buttonPressed) {
            buttonPressed = true;
            // Persistent message to elevator control
        }
    }


    public void enterElevatorQueue (Person p) {
        assert p != null;
        assert people != null;
        assert people.contains(p);

    }
}
