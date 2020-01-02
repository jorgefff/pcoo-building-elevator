import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

import static java.lang.System.*;

public class main {

    private final static int NUM_FLOORS = 10;
    private final static int ELEVATOR_CAPACITY = 4;
    private final static int NUM_PEOPLE = 200;

    public static void main(String[] args) {

        // Initialize shared areas
        Building building = new Building();
        building.generateFloors(NUM_FLOORS);
        building.generateElevator(ELEVATOR_CAPACITY);

        // Set up graphical
        Graphical g = Graphical.getInstance();
        g.setBuilding(building);

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
        out.println("EVERY PERSON ARRIVED");
        try {
            controller.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
}
