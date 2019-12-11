
public class Building {

    protected Floor[] floors;
    protected int numFloors;
    protected static Building instance = null;

    private Building(int numFloors) {
        assert numFloors > 0;

        floors = new Floor[numFloors];
        for (int i = 0; i < numFloors; i++) {
            floors[i] = new Floor();
        }
        this.numFloors = numFloors;
    }


    public static Building initialize (int numFloors) {
        assert instance == null;

        instance = new Building (numFloors);
        return instance;
    }


    public static Building getInstance() {
        assert instance != null;

        return instance;
    }


    public void enterFloor (Person p, int n) {
        assert n > 0 && n < numFloors;
        assert p != null;

        floors[n].enter(p);
    }


    public void pressElevatorButton() {
        
    }


}
