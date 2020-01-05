import static java.lang.System.*;

public class Person extends Thread {

    private static int personCounter = 0;

    protected final long MIN_START_SLEEP = 100;
    protected final long MAX_START_SLEEP = 1000;

    protected int id;
    protected int start;
    protected int goal;
    protected Building_Prsn building;

    public Person (Building_Prsn building) {
        assert building != null;

        this.building = building;
        this.id = personCounter++;
        start = randFloor (building.getNumFloors());
        do {
            goal = randFloor (building.getNumFloors());
        } while (goal == start);
    }

    @Override
    public String toString() {
        return "PERSON-START( "+start+" )->GOAL( "+goal+" )";
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
        assert start != goal;

        randStartSleep();
        out.println(this);

        Floor floor;
        Elevator elevator;

        floor = building.enterFloor(this);
        floor.callElevator(this);
        elevator = floor.queueForElevator(this);
        floor.exit(this);
        elevator.enter(this);
        floor.releaseElevatorDoor();
        elevator.waitForFloor(this);
        building.getFloor(goal).arrive(this);

        out.println("PERSON ARRIVED");
    }
}
