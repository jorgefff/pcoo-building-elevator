import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.System.*;

public class main {

    private static boolean graphicalMode = true;

    private static int PPL_THREADS = 10;  // Threads used for people
    private static int NUM_FLOORS = 10;   // Number of floors in the building
    private static int ELEVATOR_CAPACITY = 4;
    private static int NUM_PEOPLE = 50;
    private static long CTRL_PAUSE = 30;              // Time the elevator controller pauses
    private static boolean ELEV_PRIORITY = true;      // Elevator requests have higher priority
    private static final Color FLOOR_COLOR = Color.black;   // Floor occupancy text color
    private static final Color ELEV_COLOR = Color.green;    // Elevator occupancy text color


    public static void main(String[] args) {
        boolean badArgs = false;
        if (    args.length < 1 ||
                (args.length > 1 && args.length < 7) ||
                args.length > 7) {
            badArgs = true;
        }
        else if (args.length == 7) {
            try {
                NUM_FLOORS = Integer.parseInt(args[1]);
                NUM_PEOPLE = Integer.parseInt(args[2]);
                ELEVATOR_CAPACITY = Integer.parseInt(args[3]);
                if (args[4].toLowerCase().equals("y")) {
                    ELEV_PRIORITY = true;
                } else if (args[4].toLowerCase().equals("n")) {
                    ELEV_PRIORITY = false;
                } else {
                    badArgs = true;
                }
                if (args[5].toLowerCase().equals("y")) {
                    graphicalMode = true;
                } else if (args[5].toLowerCase().equals("n")) {
                    graphicalMode = false;
                } else {
                    badArgs = true;
                }
                PPL_THREADS = Integer.parseInt(args[6]);
                if (PPL_THREADS < 1) {
                    badArgs = true;
                }
            } catch (Exception e) {
                badArgs = true;
            }
        }
        if (badArgs) {
            err.println("Bad arguments!");
            out.println("Usage (default values): java main -ea");
            out.println("Usage (custom values): java main -ea <Num of floors> <Num of people> <Elevator capacity> <Elevator priority(Y / N)> <graphical mode(Y / N)> <People threads(MIN: 1)>");
            System.exit(1);
        }

        ExecutorService thrPool = Executors.newFixedThreadPool(PPL_THREADS);

        // Initialize shared areas
        Building building = new Building(ELEV_PRIORITY);
        building.generateFloors(NUM_FLOORS);
        building.generateElevator(ELEVATOR_CAPACITY);


        // Set up graphical
        Graphical g = Graphical.getInstance();
        g.toggle(graphicalMode);
        g.config(building, NUM_PEOPLE, FLOOR_COLOR, ELEV_COLOR);


        // Initialize active entity - elevator controller
        ElevatorControl controller = new ElevatorControl(building, CTRL_PAUSE);
        controller.setDaemon(true);
        controller.start();


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
