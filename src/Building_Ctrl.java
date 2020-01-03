public interface Building_Ctrl {

    @Override
    public String toString();

    /**
     * Returns number of floors
     * @return
     */
    public int getNumFloors();

    /**
     * Returns a floor
     * @param n Number of floor wanted
     * @return
     */
    public Floor getFloor(int n);
    /**
     * Gets elevator instance
     * @return
     */
    public Elevator getElevator();

    /**
     * Controller has no pending requests, idles
     */
    public void idle();
    /**
     * Checks if there are pending requests from floors and elevator
     * @return
     */
    public boolean pendingRequests();
    /**
     * Checks if a floor requested the elevator
     * @param n The floor to be checked
     * @return
     */
    public boolean isRequesting (int n);

    /**
     * Returns the oldest {@link Request}
     * Gives priority to elevator requests if set on the constructor
     * @return
     */
    public Request getNextDestination();
    /**
     * Clears the requests made for this floor coming from the floor and elevator
     * @param n The floor
     */
    public void clearRequests(int n);
}
