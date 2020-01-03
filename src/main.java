import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.System.*;

public class main {

    private static final int MAX_THREADS = 20;
    private static final int NUM_FLOORS = 10;
    private static final int ELEVATOR_CAPACITY = 4;
    private static final int NUM_PEOPLE = 50;
    private static final long CTRL_PAUSE = 50;
    private static final boolean ELEV_PRIORITY = true;
    private static final Color FLOOR_COLOR = Color.black;
    private static final Color ELEV_COLOR = Color.green;


    public static void main(String[] args) {
        ExecutorService thrPool = Executors.newFixedThreadPool(MAX_THREADS);

        // Initialize shared areas
        Building building = new Building(ELEV_PRIORITY);
        building.generateFloors(NUM_FLOORS);
        building.generateElevator(ELEVATOR_CAPACITY);

        // Set up graphical
        Graphical g = Graphical.getInstance();
        g.setBuilding(building, NUM_PEOPLE, FLOOR_COLOR, ELEV_COLOR);

        // Initialize active entity - elevator controller
        ElevatorControl controller = new ElevatorControl(building, CTRL_PAUSE);
        thrPool.execute(controller);

        // Initialize active entity - person
        Runnable [] people = new Person[NUM_PEOPLE];
        for (int i = 0; i < NUM_PEOPLE; i++) {
            people[i] = new Person(building);
            thrPool.execute(people[i]);
        }

        thrPool.shutdown();
        try {
            thrPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }

        out.println("EVERY PERSON ARRIVED");
        System.exit(0);
    }
}
