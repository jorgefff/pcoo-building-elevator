import static java.lang.System.*;

public class Person extends Thread {

    protected final long MIN_START_SLEEP = 200;
    protected final long MAX_START_SLEEP = 2000;

    protected int start;
    protected int goal;
    protected Building building;

    public Person (Building building) {
        assert building != null;

        this.building = building;
        start = randFloor (building.getNumFloors());
        do {
            goal = randFloor (building.getNumFloors());
        } while (goal == start);
        start = 2;  //DEBUG
        goal = 0;  //DEBUG
    }

    @Override
    public String toString() {
        return "PERSON-START( "+start+" )-GOAL( "+goal+" )";
    }

    private int randFloor (int max) {
        int min = 0;
        int range = max - min;
        int rand = (int) (Math.random() * range) + min;
        return rand;
    }

    private void randStartSleep() {
        long range = MAX_START_SLEEP - MIN_START_SLEEP;
        long randSleep = (long) (Math.random() * range) + MIN_START_SLEEP;
        try {
            Thread.sleep(randSleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {

        randStartSleep();

        out.println(this);

        Floor floor = building.enterFloor(this, start);

        building.callElevator(this);

        building.grabDoor(this);

        Elevator elevator = building.queueForElevator(this);

        building.releaseDoor(this);

//        elevator.waitForMyFloor(this);


        //building.enterFloor(this, goal);

    }
}
