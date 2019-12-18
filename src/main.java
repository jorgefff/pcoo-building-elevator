import java.util.LinkedList;
import java.util.List;

import static java.lang.System.*;

public class main {

    private final static int NUM_FLOORS = 10;
    private final static int ELEVATOR_CAPACITY = 4;
    private final static int NUM_PEOPLE = 2;

    public static void main(String[] args) {

        // Initialize shared area - floors
        Floor[] floors = new Floor[NUM_FLOORS];
        for (int i = 0; i < floors.length; i ++) {
            floors[i] = new Floor(i);
        }

        // Initialize shared area - elevator
        Elevator elevator = new Elevator(ELEVATOR_CAPACITY, NUM_FLOORS);

        // Initialize shared area - building
        Building building = new Building(floors, elevator);

        // Initialize active entity - elevator controller
        ElevatorControl controller = new ElevatorControl(building);
        controller.start();

        // Initialize active entity - person
        Person [] people = new Person[NUM_PEOPLE];
        for (int i = 0; i < NUM_PEOPLE; i++) {
            people[i] = new Person(building);
            people[i].start();
        }

        // Wait for every person to finish
        for (int i = 0; i < NUM_PEOPLE; i++) {
            try {
                people[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
