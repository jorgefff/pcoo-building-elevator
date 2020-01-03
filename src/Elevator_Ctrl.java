public interface Elevator_Ctrl {

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
     * ElevatorControl methods
     */

    /**
     * Returns how fast the elevator moves, used for graphical purposes
     * @return
     */
    public int getMovementUnit();

    /**
     * Returns real position
     * @return
     */
    public double getPosition();

    /**
     * Start elevator movement.
     * People can no longer enter or leave.
     */
    public void startMoving();

    /**
     * Stop elevator movement.
     * People can now enter or leave.
     */
    public void stopMoving();

    /**
     * Move elevator up or down
     * @param direction Elevator goes up if direction is positive, down if it is negative
     */
    public void move (int direction);

    /**
     * Clears requests made for a certain floor from inside the elevator
     * @param n
     */
    public  void clearRequest(int n);

    /**
     * Gets the list of requests
     * @return
     */
    public Request[] getRequests();

    /**
     * Returns true if elevator just started moving.
     * Used to prevent being stuck in a floor
     * @return
     */
    public boolean startedMovement();
}
