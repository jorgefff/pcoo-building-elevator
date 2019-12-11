
public class main {

    private final static int NUM_FLOORS = 10;
    private final static int ELEVATOR_CAPACITY = 10;
    private final static int NUM_PEOPLE = 1;

    public static void main(String[] args) {

        Building b = Building.initialize (NUM_FLOORS, ELEVATOR_CAPACITY);

        Person [] people = new Person[NUM_PEOPLE];
        for (int i = 0; i < NUM_PEOPLE; i++) {
            people[i] = new Person();
            people[i].start();
        }

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
