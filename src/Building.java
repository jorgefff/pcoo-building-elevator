
public class Building {

    protected Floor[] floors;

    public Building(int numFloors) {
        assert numFloors > 0;

        floors = new Floor[numFloors];
        for(int i = 0; i < numFloors; i++) {
            floors[i] = new Floor();
        }

    }
}
