public interface Elevator_Prsn {

    /* **********************************************************************************************************
     * Common methods
     */

    @Override
    public String toString();

    /**
     * Returns current floor number
     * @return
     */
    public int getFloorN();

    /**
     * Returns maximum capacity
     * @return
     */
    public int getCapacity();

    /**
     * Returns how many people are currently inside
     * @return
     */
    public int getOccupancy();

    /**
     * Returns true if elevator is currently moving
     * @return
     */
    public boolean isMoving();

    /**
     * Returns true if elevator is at a floor
     * Returns false if elevator is between floors
     * @return
     */
    public boolean isAtAFloor();

    /**
     * Returns true if elevator is at the floor
     * Returns false if elevator is between floors or at a different floor
     * @param n
     * @return
     */
    public boolean isAtFloor(int n);

    /* **********************************************************************************************************
     * Person methods
     */

    /**
     * Returns true if elevator is at maximum capacity
     * @return
     */
    public boolean isFull();

    /**
     * Person enters the elevator
     * @param p The person entering must not already be inside
     */
    public void enter (Person p);

    /**
     * Person waits inside until the elevator reaches the desired floor
     * @param p The person must already be inside
     */
    public void waitForFloor (Person p);

    /**
     * Person presses the button to the desired floor
     * @param p The person must be inside the elevator
     */
    public void pressButton (Person p);

}
